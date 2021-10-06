package com.randombananazz.dauntlessweapons.listeners;

import com.randombananazz.dauntlessweapons.DauntlessWeapons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BowListener implements Listener {
    private static final HashMap<Projectile, Integer> trackedArrows = new HashMap<>();
    private static final DauntlessWeapons plugin = DauntlessWeapons.getPlugin(DauntlessWeapons.class);
    private static final NamespacedKey weaponID = new NamespacedKey(plugin, "weaponID");
    private static final NamespacedKey ownerUUIDMost = new NamespacedKey(plugin, "OwnerUUIDMost");
    private static final NamespacedKey ownerUUIDLeast = new NamespacedKey(plugin, "OwnerUUIDLeast");

    @EventHandler
    public void onItemUse(@NotNull PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR
                || e.getAction() != Action.RIGHT_CLICK_BLOCK
                || e.getItem() == null) return;
        ItemStack bow = e.getItem();
        if (isSpecialBow(bow) == -1) return;
        if (!isCorrectOwner(e.getPlayer(), bow)) {
            e.setUseItemInHand(Event.Result.DENY);
            e.getPlayer().sendMessage(Component.text("You are not the owner of this weapon!").color(NamedTextColor.DARK_RED));
        }
    }

    @EventHandler
    public void onEntityShootBow(@NotNull EntityShootBowEvent e) {
        Projectile arrow = (Projectile) e.getProjectile();
        if (!(arrow.getShooter() instanceof Player) || e.getBow() == null) return;
        int bowID = isSpecialBow(e.getBow());
        if (bowID == -1) return;
        trackedArrows.put(arrow, bowID);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!trackedArrows.containsKey(e.getEntity())) return;
        int bowID = trackedArrows.get(e.getEntity());
        World w = e.getEntity().getWorld();
        Location loc = null;
        if (e.getHitBlock() != null) {
            loc = e.getHitBlock().getLocation();
        } else if (e.getHitEntity() != null) {
            loc = e.getHitEntity().getLocation();
        }
        if (loc == null) return;

        switch (bowID) {
            case 0 -> w.strikeLightning(loc);
            case 1 -> {
                Material lastType = w.getType(loc);
                w.setType(loc.getBlock().getLocation(), Material.WATER);
                for (int i = 0; i < 3; i++) {
                    w.spawn(loc, TNTPrimed.class, tnt -> tnt.setFuseTicks(0));
                }
                w.setType(loc.getBlock().getLocation(), lastType);
            }
            case 2 -> {
                for (LivingEntity q: w.getNearbyLivingEntities(loc, 5)) {
                    q.setFreezeTicks(q.getMaxFreezeTicks());
                    q.damage(8, e.getEntity());
                    q.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 0));
                }
            }
        }
        e.getEntity().remove();
    }

    private int isSpecialBow(@NotNull ItemStack item) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(weaponID, PersistentDataType.INTEGER, -1);
    }

    private boolean isCorrectOwner(@NotNull Player p, @NotNull ItemStack item) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(ownerUUIDLeast, PersistentDataType.LONG)
                || !pdc.has(ownerUUIDMost, PersistentDataType.LONG)) return false;
        Long uuidLeast = pdc.get(ownerUUIDLeast, PersistentDataType.LONG);
        Long uuidMost = pdc.get(ownerUUIDMost, PersistentDataType.LONG);
        if (uuidLeast == null || uuidMost == null) return false;
        return uuidLeast == p.getUniqueId().getLeastSignificantBits()
                && uuidMost == p.getUniqueId().getMostSignificantBits();
    }
}
