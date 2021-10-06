package com.randombananazz.minecraft;

import com.randombananazz.minecraft.commands.GiveWeapon;
import com.randombananazz.minecraft.listeners.BowListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DauntlessWeapons extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginManager pm = getServer().getPluginManager();

        getLogger().info("MyPlugin has been loaded");

        pm.registerEvents(new BowListener(), this);
        getCommand("weapon").setExecutor(new GiveWeapon());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
