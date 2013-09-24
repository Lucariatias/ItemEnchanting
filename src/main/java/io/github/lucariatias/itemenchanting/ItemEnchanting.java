package io.github.lucariatias.itemenchanting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemEnchanting extends JavaPlugin {
	
	public void onEnable() {
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException exception) {
			this.getLogger().warning("Failed to submit stats.");
		}
		if (!this.getDataFolder().exists()) {
			this.createConfig();
		}
		YamlConfiguration lastUpdateConfig = new YamlConfiguration();
		File lastUpdateFile = new File(this.getDataFolder().getPath() + File.separator + "last-update");
		if (!lastUpdateFile.exists()) {
			this.createConfig();
			lastUpdateConfig.set("version", this.getDescription().getVersion());
			try {
				lastUpdateConfig.save(lastUpdateFile);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		this.getServer().getPluginManager().registerEvents(new EnchantItemListener(this), this);
		this.getCommand("itemenchant").setExecutor(new ItemEnchantCommand(this));
	}
	
	public void createConfig() {
		this.getDataFolder().delete();
		this.getConfig().set("enchantment-table.mode", "flat-rate");
		List<ItemStack> tableItems = new ArrayList<ItemStack>();
		tableItems.add(new ItemStack(Material.EMERALD, 3));
		tableItems.add(new ItemStack(Material.DIAMOND, 1));
		this.getConfig().set("enchantment-table.items", tableItems);
		this.getConfig().set("enchantment-command.mode", "multiply");
		List<ItemStack> commandItems = new ArrayList<ItemStack>();
		commandItems.add(new ItemStack(Material.EMERALD, 5));
		commandItems.add(new ItemStack(Material.DIAMOND, 3));
		this.getConfig().set("enchantment-command.items", commandItems);
		this.getConfig().set("messages.success", "&aSuccessfully enchanted &9%enchanted-item% &ausing:");
		this.getConfig().set("messages.failure", "&cFailed to enchant &9%enchanted-item%&c, it requires:");
		this.getConfig().set("messages.show-items", true);
		this.getConfig().set("messages.item-format", "&9%payment-amount% x %payment-item%");
		this.saveConfig();
	}

}
