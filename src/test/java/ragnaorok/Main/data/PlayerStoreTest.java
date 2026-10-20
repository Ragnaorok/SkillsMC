package ragnaorok.Main.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ragnaorok.Main.ClassType;
import ragnaorok.Main.SkillsMCPlayer;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStoreTest {
    @TempDir Path directory;
    final UUID id = UUID.randomUUID();
    PlayerStore store() { return new PlayerStore(directory, ignored -> {}); }
    SkillsMCPlayer profile() {
        SkillsMCPlayer p = new SkillsMCPlayer(null, 10, 10, 7, 23, 4, ClassType.ARCHER);
        p.setLevel(8); p.setExperience(37); p.setMana(3);
        return p;
    }
    @Test void missingPlayerIsNew() throws Exception { assertNull(store().load(id)); }
    @Test void roundTripPreservesAllProgress() throws Exception {
        store().save(id, profile());
        SkillsMCPlayer p = store().load(id);
        assertEquals(8, p.getLevel()); assertEquals(37, p.getExperience());
        assertEquals(3, p.getMana()); assertEquals(10, p.getMaxMana());
        assertEquals(7, p.getSouls()); assertEquals(23, p.getCurrency()); assertEquals(4, p.getBounty());
        assertEquals(ClassType.ARCHER, p.getClassType()); assertNull(p.getPlayer());
    }
    @Test void readsExistingGzipObjectFormatWithoutMigration() throws Exception {
        try (var out = new ObjectOutputStream(new GZIPOutputStream(Files.newOutputStream(directory.resolve(id + ".dat"))))) {
            out.writeObject(profile());
        }
        assertEquals(37, store().load(id).getExperience());
    }
    @Test void corruptedPrimaryRecoversPreviousCompleteSave() throws Exception {
        var p = profile(); store().save(id, p);
        p.setSouls(50); store().save(id, p);
        Files.writeString(directory.resolve(id + ".dat"), "truncated");
        assertEquals(7, store().load(id).getSouls());
        store().save(id, store().load(id)); // A corrupt primary must not poison its backup.
        Files.writeString(directory.resolve(id + ".dat"), "truncated again");
        assertEquals(7, store().load(id).getSouls());
    }
    @Test void missingPrimaryRecoversBackup() throws Exception {
        store().save(id, profile()); store().save(id, profile());
        Files.delete(directory.resolve(id + ".dat"));
        assertNotNull(store().load(id));
    }
    @Test void corruptionWithoutBackupFailsInsteadOfResetting() throws Exception {
        Path file = directory.resolve(id + ".dat"); Files.writeString(file, "broken");
        assertThrows(IOException.class, () -> store().load(id));
        assertEquals("broken", Files.readString(file));
    }
    @Test void invalidProfileCannotOverwriteProgress() throws Exception {
        store().save(id, profile());
        var invalid = profile(); invalid.setClassType(null);
        assertThrows(IOException.class, () -> store().save(id, invalid));
        assertEquals(ClassType.ARCHER, store().load(id).getClassType());
    }
    @Test void failedBackupWriteLeavesPrimaryUntouched() throws Exception {
        store().save(id, profile());
        Path backup = directory.resolve(id + ".dat.bak");
        Files.createDirectory(backup); Files.writeString(backup.resolve("obstruction"), "keep");
        var changed = profile(); changed.setSouls(100);
        assertThrows(IOException.class, () -> store().save(id, changed));
        assertEquals(7, store().load(id).getSouls());
        try (var files = Files.list(directory)) {
            assertTrue(files.noneMatch(p -> p.getFileName().toString().endsWith(".tmp")));
        }
    }
}
