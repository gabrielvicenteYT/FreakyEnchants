package org.mswsplex.enchants.checkers.armor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Cuboid;

public class NetherWalkerCheck implements Listener {
	private FreakyEnchants plugin;

	private List<Block> handleBlocks;

	public NetherWalkerCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
		handleBlocks = new ArrayList<>();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getEquipment() == null || player.getEquipment().getBoots() == null)
			return;

		ItemStack armor = player.getEquipment().getBoots();
		if (!plugin.getEnchManager().containsEnchantment(armor, "netherwalker") || player.getLocation().getY() % 1 != 0
				|| player.isFlying())
			return;
		if (event.getTo().getBlock().equals(event.getFrom().getBlock()))
			return;
		float radius = (float) plugin.getEnchManager().getBonusAmount("netherwalker",
				armor.getEnchantmentLevel(plugin.getEnchant("netherwalker")));
		Location loc = player.getLocation();
		Cuboid cube = new Cuboid(loc.clone().add(radius, -1, radius), loc.clone().subtract(radius, 1, radius));
		List<Block> blocks = new ArrayList<Block>();
		double mL = Math.pow(radius, 2);
		for (Block b : cube) {
			if (!b.getType().toString().contains("LAVA"))
				continue;
			if (b.getLocation().distanceSquared(player.getLocation()) > mL)
				continue;
			if (b.getRelative(BlockFace.UP).getType() != Material.AIR)
				continue;
			blocks.add(b);
			handleBlocks.add(b);
		}

		List<String> types = plugin.config.getStringList("NetherWalker.BlockTypes");

		Material[] progBlocks = new Material[types.size()];
		short[] dmg = new short[progBlocks.length];

		for (int i = 0; i < types.size(); i++) {
			progBlocks[i] = Material.valueOf(types.get(i).split(":")[0]);
			if (types.get(i).contains(":"))
				dmg[i] = Short.valueOf(types.get(i).split(":")[1]);
		}

		for (int i = 0; i < progBlocks.length; i++) {
			final int a = i;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
				for (Block block : blocks) {
					if (block.getType() == Material.AIR)
						continue;
					block.setType(progBlocks[a]);
					if (dmg[a] != 0)
						block.setData((byte) dmg[a]);
					if (a == progBlocks.length) {
						handleBlocks.remove(block);
					}
				}
			}, ((int) plugin.getEnchManager().getDouble("netherwalker", "Lifetime",
					armor.getEnchantmentLevel(plugin.getEnchant("netherwalker"))) / progBlocks.length) * i);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (handleBlocks.contains(block))
			event.getBlock().setType(Material.AIR);
	}
}
