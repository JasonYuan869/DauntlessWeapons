package com.randombananazz.dauntlessweapons.commands;

import com.randombananazz.dauntlessweapons.DauntlessWeapons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GiveWeapon implements CommandExecutor {
    private static final DauntlessWeapons plugin = DauntlessWeapons.getPlugin(DauntlessWeapons.class);
    private static final NamespacedKey weaponID = new NamespacedKey(plugin, "weaponID");
    private static final NamespacedKey ownerUUIDMost = new NamespacedKey(plugin, "OwnerUUIDMost");
    private static final NamespacedKey ownerUUIDLeast = new NamespacedKey(plugin, "OwnerUUIDLeast");

    private static final String[] WEAPON_NAME = {
            "Lightning Bow",
            "Explosion",
            "Icetide Coil"
    };

    private static final String[] WEAPON_LORE = {
            "Calls Katae's power down to the material plane.",
            "Handheld TNT cannon that decimates everything on hit.",
            "TBD",
    };

    private static final NamedTextColor[] WEAPON_COLOR = {
            NamedTextColor.BLUE,
            NamedTextColor.RED,
            NamedTextColor.AQUA,
    };

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            int argsLength = args.length;
            Player owner;

            if (argsLength == 0 || argsLength > 2) {
                return false;
            } else if (argsLength == 2) {
                owner = Bukkit.getPlayer(args[1]);
                if (owner == null) {
                    p.sendMessage(Component.text("That player does not exist!").color(NamedTextColor.DARK_RED));
                    return true;
                }
            } else {
                owner = p;
            }

            int id;
            Material weaponType;

            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "katae" -> {
                    id = 0;
                    weaponType = Material.BOW;
                }
                case "megumin" -> {
                    id = 1;
                    weaponType = Material.BOW;
                }
                case "ferras" -> {
                    id = 2;
                    weaponType = Material.BOW;
                }
                default -> {
                    p.sendMessage(Component.text("That deity does not exist!").color(NamedTextColor.DARK_RED));
                    return true;
                }
            }
            giveWeapon(id, weaponType, p, owner);
            return true;
        }
        return false;
    }

    private void giveWeapon(int id, Material weaponType, Player p, Player owner) {
        UUID ownerUUID = owner.getUniqueId();
        TextComponent weaponName = Component.text(WEAPON_NAME[id], WEAPON_COLOR[id]);
        List<Component> weaponLore = new ArrayList<>();
        weaponLore.add(Component.text(WEAPON_LORE[id]));
        ItemStack weapon = new ItemStack(weaponType);
        weapon.editMeta(itemMeta -> {
            itemMeta.displayName(weaponName);
            itemMeta.lore(weaponLore);
            itemMeta.addEnchant(Enchantment.DURABILITY, 10, true);
            itemMeta.setUnbreakable(true);
            PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
            tags.set(weaponID, PersistentDataType.INTEGER, id);
            tags.set(ownerUUIDMost, PersistentDataType.LONG, ownerUUID.getMostSignificantBits());
            tags.set(ownerUUIDLeast, PersistentDataType.LONG, ownerUUID.getLeastSignificantBits());
        });

        HashMap<Integer, ItemStack> errors = p.getInventory().addItem(weapon);
        if (!errors.isEmpty()) {
            p.sendMessage(Component.text("Your inventory seems to be full!").color(NamedTextColor.DARK_RED));
        } else {
            plugin.getLogger().info("Gave " + weaponName.content() + " to " + p.getName() + " owned by " + owner.getName());
        }
    }
}
