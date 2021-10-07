package com.randombananazz.dauntlessweapons;

import com.randombananazz.dauntlessweapons.commands.GiveWeapon;
import com.randombananazz.dauntlessweapons.listeners.BowListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DauntlessWeapons extends JavaPlugin {
    public static DauntlessWeapons plugin;
    public static NamespacedKey weaponID;
    public static NamespacedKey ownerUUIDMost;
    public static NamespacedKey ownerUUIDLeast;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        weaponID = new NamespacedKey(plugin, "weaponID");
        ownerUUIDMost = new NamespacedKey(plugin, "OwnerUUIDMost");
        ownerUUIDLeast = new NamespacedKey(plugin, "OwnerUUIDLeast");

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
