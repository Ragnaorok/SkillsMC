package ragnaorok.Main.progression;

import java.io.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import ragnaorok.Main.*;
import static org.junit.jupiter.api.Assertions.*;

class LevelCapTest {
    SkillsMCPlayer profile() { return new SkillsMCPlayer(null,10,10,7,23,4,ClassType.MAGE); }
    @Test void xpStopsAtCapWithoutOverflowOrBanking() {
        var p=profile();p.setLevel(24);p.addExperience(Integer.MAX_VALUE);
        assertEquals(25,p.getLevel());assertEquals(0,p.getExperience());
        p.addExperience(Integer.MAX_VALUE);p.setExperience(9999);
        assertEquals(25,p.getLevel());assertEquals(0,p.getExperience());
        p.setLevel(999);assertEquals(25,p.getLevel());
    }
    @Test void legacySaveIsClampedAndOtherBalancesSurvive() throws Exception {
        var p=profile();var field=SkillsMCPlayer.class.getDeclaredField("level");
        field.setAccessible(true);field.setInt(p,80);
        var xp=SkillsMCPlayer.class.getDeclaredField("experience");xp.setAccessible(true);xp.setInt(p,100);
        var bytes=new ByteArrayOutputStream();
        try(var out=new ObjectOutputStream(bytes)){out.writeObject(p);}
        SkillsMCPlayer loaded;
        try(var in=new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))){loaded=(SkillsMCPlayer)in.readObject();}
        assertEquals(25,loaded.getLevel());assertEquals(0,loaded.getExperience());
        assertEquals(7,loaded.getSouls());assertEquals(23,loaded.getCurrency());assertEquals(4,loaded.getBounty());
    }
    @Test void fullVarianceAndMixedGroupProtection() {
        assertEquals(20,MobScaling.level(List.of(25),-5));
        assertEquals(30,MobScaling.level(List.of(25),5));
        assertEquals(1,MobScaling.level(List.of(1),-5));
        assertEquals(6,MobScaling.level(List.of(1),5));
        for(int roll=-5;roll<=5;roll++)assertTrue(MobScaling.level(List.of(5,25),roll)<=10);
    }
}
