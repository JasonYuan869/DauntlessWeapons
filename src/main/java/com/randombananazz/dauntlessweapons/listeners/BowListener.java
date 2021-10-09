package com.randombananazz.dauntlessweapons.listeners;

import com.randombananazz.dauntlessweapons.DauntlessWeapons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

    @EventHandler
    public void onItemUse(@NotNull PlayerInteractEvent e) {
        if (!e.getAction().isRightClick()
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
        Location loc = e.getEntity().getLocation();

        if (e.getHitEntity() != null) {
            loc = e.getHitEntity().getLocation();
        }

        switch (bowID) {
            case 0 -> w.strikeLightning(loc);
            case 1 -> {
                loc.createExplosion(4f, false, false);
                loc.createExplosion(4f, false, false);
                loc.createExplosion(4f, false, false);
            }
            case 2 -> {
                w.playSound(loc, "block.glass.break", 1, 1);
                for (LivingEntity q: w.getNearbyLivingEntities(loc, 3)) {
                    q.setFreezeTicks(q.getMaxFreezeTicks());
                    q.damage(q.equals(e.getEntity().getShooter()) ? 4: 8, (Entity) e.getEntity().getShooter());
                    q.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 0));
                }

                if (!loc.getBlock().isSolid()) {
                    loc = loc.subtract(0,1,0);
                }
                if (loc.getBlock().isSolid()) w.setType(loc, Material.ICE);
            }
        }
        e.getEntity().remove();
    }

    private int isSpecialBow(@NotNull ItemStack item) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(DauntlessWeapons.weaponID, PersistentDataType.INTEGER, -1);
    }

    private boolean isCorrectOwner(@NotNull Player p, @NotNull ItemStack item) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(DauntlessWeapons.ownerUUIDMost, PersistentDataType.LONG)
                || !pdc.has(DauntlessWeapons.ownerUUIDLeast, PersistentDataType.LONG)) return true;
        Long uuidLeast = pdc.get(DauntlessWeapons.ownerUUIDLeast, PersistentDataType.LONG);
        Long uuidMost = pdc.get(DauntlessWeapons.ownerUUIDMost, PersistentDataType.LONG);
        if (uuidLeast == null || uuidMost == null) return false;
        return uuidLeast == p.getUniqueId().getLeastSignificantBits()
                && uuidMost == p.getUniqueId().getMostSignificantBits();
    }
}
