package me.lucariatias.plugins.itemenchanting;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemEnchanting extends JavaPlugin {
	
	public void onEnable() {
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(new EnchantItemListener(this), this);
		this.getCommand("itemenchant").setExecutor(new ItemEnchantCommand(this));
	}

}
