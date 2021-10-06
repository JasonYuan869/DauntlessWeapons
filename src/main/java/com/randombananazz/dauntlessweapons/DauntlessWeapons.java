package com.randombananazz.dauntlessweapons;

import com.randombananazz.dauntlessweapons.commands.GiveWeapon;
import com.randombananazz.dauntlessweapons.listeners.BowListener;
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
