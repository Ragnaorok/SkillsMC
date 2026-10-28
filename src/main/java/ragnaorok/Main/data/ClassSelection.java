package ragnaorok.Main.data;

import ragnaorok.Main.ClassType;
import ragnaorok.Main.SkillsMCPlayer;
import java.util.function.BooleanSupplier;

/** Changes only the class; a failed save leaves the original profile intact. */
public final class ClassSelection {
    private ClassSelection() { }
    public static boolean commit(SkillsMCPlayer profile, ClassType selection, BooleanSupplier save) {
        if (selection == null || selection == ClassType.NONE) throw new IllegalArgumentException("Choose a playable class");
        ClassType previous = profile.getClassType();
        if (previous == selection) return true;
        profile.setClassType(selection);
        boolean saved = false;
        try {
            saved = save.getAsBoolean();
            return saved;
        } finally {
            if (!saved) profile.setClassType(previous);
        }
    }
}
