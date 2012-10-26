package me.lucariatias.plugins.itemenchanting;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemEnchanting extends JavaPlugin {
	
	public void onEnable() {
		this.getLogger().info("ItemEnchanting has been enabled!");
		this.getServer().getPluginManager().registerEvents(new EnchantmentListener(this), this);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}
	
	public void onDisable(){
		this.getLogger().info("ItemEnchanting has been disabled.");
	}

}
