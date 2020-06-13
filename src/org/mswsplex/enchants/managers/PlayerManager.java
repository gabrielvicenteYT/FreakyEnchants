package org.mswsplex.enchants.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;

public class PlayerManager {
	private FreakyEnchants plugin;

	private HashMap<OfflinePlayer, CPlayer> players;

	public PlayerManager(FreakyEnchants plugin) {
		this.plugin = plugin;
		players = new HashMap<>();
	}

	public CPlayer getPlayer(OfflinePlayer player) {
		if (!players.containsKey(player))
			players.put(player, new CPlayer(player, plugin));
		return players.get(player);
	}

	public ArrayList<OfflinePlayer> getLoadedPlayers() {
		return new ArrayList<OfflinePlayer>(players.keySet());
	}

	public void removePlayer(OfflinePlayer player) {
		if (players.containsKey(player))
			players.get(player).saveData();
		players.remove(player);
	}

	public void clearPlayers() {
		for (OfflinePlayer player : players.keySet())
			removePlayer(player);
	}

	public void loadData(OfflinePlayer player) {
		if (players.containsKey(player))
			throw new IllegalArgumentException("Player data already loaded");
		players.put(player, new CPlayer(player, plugin));
	}
}
