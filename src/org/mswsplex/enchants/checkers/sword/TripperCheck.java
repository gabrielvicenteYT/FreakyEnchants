package org.mswsplex.enchants.checkers.sword;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class TripperCheck implements Listener {

	private FreakyEnchants plugin;

	public TripperCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof LivingEntity) || !(event.getDamager() instanceof LivingEntity))
			return;
		if (!Utils.allowEnchant(ent.getWorld(), "tripper"))
			return;
		LivingEntity living = (LivingEntity) event.getDamager();
		ItemStack hand = living.getEquipment().getItemInHand();
		Enchantment ench = plugin.getEnchant("tripper");
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!plugin.getEnchManager().containsEnchantment(hand, ench))
			return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
			Vector vel = new Vector(0, Math.abs(Math.abs(ent.getVelocity().getX()))
					+ Math.abs(Math.abs(ent.getVelocity().getY())) + Math.abs(Math.abs(ent.getVelocity().getX())), 0);
			ent.setVelocity(
					vel.multiply(plugin.getEnchManager().getBonusAmount("tripper", hand.getEnchantmentLevel(ench))));
		}, 1);
	}

}
