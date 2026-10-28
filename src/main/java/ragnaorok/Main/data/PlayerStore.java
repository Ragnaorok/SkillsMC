package ragnaorok.Main.data;

import ragnaorok.Main.SkillsMCPlayer;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** Keeps the existing UUID.dat format and location; replaces files only after a complete write. */
public final class PlayerStore {
    private final Path directory;
    private final Consumer<String> warning;

    public PlayerStore(Path directory, Consumer<String> warning) {
        this.directory = directory;
        this.warning = warning;
    }

    public SkillsMCPlayer load(UUID id) throws IOException {
        Path file = file(id);
        Path backup = backup(id);
        if (!Files.exists(file) && !Files.exists(backup)) return null;
        try {
            return read(file);
        } catch (IOException primaryFailure) {
            try {
                SkillsMCPlayer recovered = read(backup);
                warning.accept("Recovered " + id + " from " + backup + "; primary file was unreadable.");
                return recovered;
            } catch (IOException backupFailure) {
                primaryFailure.addSuppressed(backupFailure);
                throw primaryFailure;
            }
        }
    }

    public void save(UUID id, SkillsMCPlayer profile) throws IOException {
        validate(profile);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(bytes))) {
            out.writeObject(profile);
        }
        Files.createDirectories(directory);
        Path file = file(id);
        // Never replace a good backup with a damaged primary file.
        if (Files.exists(file)) {
            try {
                read(file);
                replace(backup(id), Files.readAllBytes(file));
            } catch (InvalidProfileException e) {
                warning.accept("Keeping previous backup for " + id + ": " + e.getMessage());
            }
        }
        replace(file, bytes.toByteArray());
    }

    private SkillsMCPlayer read(Path path) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(Files.newInputStream(path)))) {
            in.setObjectInputFilter(info -> {
                if (info.depth() > 8 || info.references() > 100 || info.streamBytes() > 65536)
                    return ObjectInputFilter.Status.REJECTED;
                Class<?> type = info.serialClass();
                if (type == null) return ObjectInputFilter.Status.UNDECIDED;
                return type == SkillsMCPlayer.class || type == ragnaorok.Main.ClassType.class
                        || type == Enum.class || type == String.class
                        ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
            });
            Object object = in.readObject();
            if (!(object instanceof SkillsMCPlayer profile)) throw new InvalidProfileException("Unexpected profile type");
            validate(profile);
            return profile;
        } catch (ClassNotFoundException | RuntimeException e) {
            throw new InvalidProfileException("Invalid profile: " + path, e);
        } catch (IOException e) {
            if (e instanceof NoSuchFileException) throw e;
            throw new InvalidProfileException("Unreadable profile: " + path, e);
        }
    }

    private static void validate(SkillsMCPlayer p) throws IOException {
        if (p == null || p.getClassType() == null || p.getLevel() < 1
                || p.getLevel() > Integer.MAX_VALUE / 10 || p.getExperience() < 0
                || p.getExperience() >= p.getExpToNextLevel())
            throw new InvalidProfileException("Invalid class, level or XP");
    }

    private static void replace(Path destination, byte[] bytes) throws IOException {
        Path temp = Files.createTempFile(destination.getParent(), ".skillsmc-", ".tmp");
        try {
            Files.write(temp, bytes);
            try (FileChannel channel = FileChannel.open(temp, StandardOpenOption.WRITE)) { channel.force(true); }
            // Fail safely if the filesystem cannot provide atomic replacement.
            Files.move(temp, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private Path file(UUID id) { return directory.resolve(id + ".dat"); }
    private Path backup(UUID id) { return directory.resolve(id + ".dat.bak"); }
    private static final class InvalidProfileException extends IOException {
        InvalidProfileException(String message) { super(message); }
        InvalidProfileException(String message, Throwable cause) { super(message, cause); }
    }
}
