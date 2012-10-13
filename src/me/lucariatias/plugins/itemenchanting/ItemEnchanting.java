package me.lucariatias.plugins.itemenchanting;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemEnchanting extends JavaPlugin {
	public static ItemEnchanting plugin;
	public void EnchantmentListener(ItemEnchanting plugin){
        ItemEnchanting.plugin = plugin;
    }
	
	public void onEnable(){
		getLogger().info("ItemEnchanting has been enabled!");
		getServer().getPluginManager().registerEvents(new EnchantmentListener(this), this);
		getConfig().options().copyDefaults(true);
		saveConfig();
		getLogger().info("Item ID has been set to " + this.getConfig().getInt("ItemID"));
		getLogger().info("Item Multiplier has been set to " + this.getConfig().getInt("ItemMultiplier"));
		if (this.getConfig().getInt("ItemMultiplier") < 0) {
			getLogger().info("Item Multiplier is less than zero! Using flat rates!");
			getLogger().info("Flat Rate has been set to " + this.getConfig().getInt("FlatRate"));
		}
	}
	
	public void onDisable(){
		getLogger().info("ItemEnchanting has been disabled.");
	}

}
