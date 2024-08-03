package ragnaorok.Main.Particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ParticleShapes {
    public static void draw(EnumShapes shape, Location mloc, Particle particle, Player player) {
        Location particleLoc = mloc.clone();
        switch (shape) {
            case CIRCLE:
                for (int i = 0; i < 360; i += 5) {
                    particleLoc.setY(mloc.getY());
                    particleLoc.setZ(mloc.getZ() + Math.sin(i));
                    particleLoc.setX(mloc.getX() + Math.cos(i));
                    player.spawnParticle(particle, particleLoc, 1);
                    player.spawnParticle(particle, particleLoc, 1);
                }
            case HELIX:
                double phi = Math.PI / 8;
                for (double t = 0; t <= 2 * Math.PI; t += Math.PI / 16) {
                    for (double i = 0; i <= 1; i += 1) {
                        double x = 0.3 * (2 * Math.PI - t) * 0.5 * cos(t + phi + i * Math.PI);
                        double y = 0.4 * t;
                        double z = 0.3 * (2 * Math.PI - t) * 0.5 * sin(t + phi + i * Math.PI);
                        particleLoc.add(x, y, z);
                        player.spawnParticle(particle, particleLoc, 2);
                        particleLoc.subtract(x, y, z);
                    }
                }
            case SPHERE:
                for (double t = 0; t <= Math.PI; t += Math.PI / 10) {
                    double radius = Math.sin(t);
                    double y = Math.cos(t);
                    for (double i = 0; i < Math.PI * 2; i += Math.PI / 10) {
                        double x = Math.cos(i) * radius;
                        double z = Math.sin(i) * radius;
                        mloc.add(x, y, z);
                        player.spawnParticle(particle, particleLoc, 1);
                        mloc.subtract(x, y, z);
                    }
                }
            case CRESCENT:
                double yaw = particleLoc.getYaw();
                for (double i = 0; i < 180; i += 10) {
                    double angle = (i + yaw) * Math.PI / 180;
                    double x1 = particleLoc.getX() + 5 * Math.cos(angle);
                    double z1 = particleLoc.getZ() + 5 * Math.sin(angle);
                    player.spawnParticle(particle, x1, particleLoc.getY(), z1 ,10);
                }
        }
    }
}
