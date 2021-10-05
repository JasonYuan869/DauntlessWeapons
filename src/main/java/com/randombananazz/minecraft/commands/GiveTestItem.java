package com.randombananazz.minecraft.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GiveTestItem implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            Component itemName = Component.text("Item name", TextColor.color(100, 150, 100));
            List<Component> itemLore = new ArrayList<>();
            itemLore.add(Component.text("Lore line 1").color(TextColor.color(255, 0, 0)));
            itemLore.add(Component.text("Lore line 2").color(TextColor.color(0, 255, 0)));
            ItemStack coolItem = new ItemStack(Material.STICK);
            ItemMeta item = coolItem.getItemMeta();
            item.displayName(itemName);
            item.lore(itemLore);
            coolItem.setItemMeta(item);
            p.getInventory().addItem(coolItem);
            return true;
        }
        return false;
    }
}
