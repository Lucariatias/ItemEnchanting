package io.github.lucariatias.itemenchanting;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemEnchanting extends JavaPlugin {
	
	public void onEnable() {
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException exception) {
			this.getLogger().warning("Failed to submit stats.");
		}
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(new EnchantItemListener(this), this);
		this.getCommand("itemenchant").setExecutor(new ItemEnchantCommand(this));
	}

}
