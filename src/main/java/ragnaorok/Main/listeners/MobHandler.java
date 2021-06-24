package ragnaorok.Main.listeners;

import org.bukkit.entity.EntityType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/*
    @author Brandon
    Created 6/23/2021
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MobHandler {
    EntityType entity();

    EntityType[] entities() default {};
}
