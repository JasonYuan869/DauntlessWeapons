package com.randombananazz.minecraft;

import com.randombananazz.minecraft.commands.GiveTestItem;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("MyPlugin has been loaded");
        getCommand("givetestitem").setExecutor(new GiveTestItem());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
