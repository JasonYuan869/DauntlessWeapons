package com.randombananazz.minecraft.listeners;

import com.randombananazz.minecraft.DauntlessWeapons;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class BowListener implements Listener {
    private static final HashMap<Projectile, Integer> trackedArrows = new HashMap<>();
    private static final DauntlessWeapons plugin = DauntlessWeapons.getPlugin(DauntlessWeapons.class);
    private static final NamespacedKey weaponID = new NamespacedKey(plugin, "weaponID");
    private static final NamespacedKey ownerUUIDMost = new NamespacedKey(plugin, "OwnerUUIDMost");
    private static final NamespacedKey ownerUUIDLeast = new NamespacedKey(plugin, "OwnerUUIDLeast");

    public void onItemUse() {

    }

    @EventHandler
    public void onProjectileFired(ProjectileLaunchEvent e) {
        Projectile arrow = e.getEntity();
        if (!(arrow.getShooter() instanceof Player p) || p.getItemInUse() == null) return;
        int bowID = isSpecialBow(p.getItemInUse());
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
                w.setType(loc, Material.WATER);
                for (int i = 0; i < 3; i++) {
                    w.spawn(loc, TNTPrimed.class, tnt -> tnt.setFuseTicks(0));
                }
                w.setType(loc, lastType);
            }
            case 2 -> {
                for (LivingEntity q: w.getNearbyLivingEntities(loc, 5)) {
                    q.setFreezeTicks(q.getMaxFreezeTicks());
                    q.damage(8, e.getEntity());
                    q.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 0));
                }
                for (int x = -5; x < 5; x++) {
                    for (int y = -5; y < 5; y++) {
                        for (int z = -5; z < 5; z++) {
                            Location xyz = loc.add(x, y, z);
                            Material lastType = w.getType(xyz);
                            w.setType(xyz, Material.ICE);
                            plugin.getServer().getScheduler().runTaskLater(plugin, ()-> w.setType(xyz, lastType), 70);
                        }
                    }
                }
            }
        }
        e.getEntity().remove();
    }

    private int isSpecialBow(ItemStack item) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(weaponID, PersistentDataType.INTEGER, -1);
    }
}
