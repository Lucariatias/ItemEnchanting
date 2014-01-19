package io.github.lucariatias.itemenchanting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

public class ItemEnchanting extends JavaPlugin {
	
	public void onEnable() {
		if (!getDataFolder().exists()) {
			createConfig();
		}
		YamlConfiguration lastUpdateConfig = new YamlConfiguration();
		File lastUpdateFile = new File(getDataFolder(), "last-update.yml");
		if (!lastUpdateFile.exists()) {
			createConfig();
			lastUpdateConfig.set("version", getDescription().getVersion());
			try {
				lastUpdateConfig.save(lastUpdateFile);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		} else {
			try {
				lastUpdateConfig.load(lastUpdateFile);
			} catch (FileNotFoundException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			} catch (InvalidConfigurationException exception) {
				exception.printStackTrace();
			}
		}
		if (lastUpdateConfig.getString("version").equals("2.2.0")) {
			lastUpdateConfig.set("version", getDescription().getVersion());
			try {
				lastUpdateConfig.save(lastUpdateFile);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			getLogger().info("It seems you're upgrading from 2.2.0, your config is compatible, and the last-update.yml file has been updated.");
		} else if (!lastUpdateConfig.getString("version").equals(getDescription().getVersion())) {
			lastUpdateFile.renameTo(new File(lastUpdateFile.getParentFile(), "config-backup.yml"));
			createConfig();
			getLogger().warning("It seems you're attempting to downgrade from a future version.");
			getLogger().warning("We've backed up you're config to config-backup.yml and wiped the config, in case of any future changes that might break the plugin.");
			getLogger().warning("You may have to reconfigure the plugin.");
		}
		try {
			Metrics metrics = new Metrics(this);
			createGraphs(metrics);
			metrics.start();
		} catch (IOException exception) {
			getLogger().warning("Failed to submit stats.");
		}
		getServer().getPluginManager().registerEvents(new EnchantItemListener(this), this);
		getCommand("itemenchant").setExecutor(new ItemEnchantCommand(this));
	}
	
	public void createConfig() {
		new File(getDataFolder(), "config.yml").delete();
		getConfig().set("enchantment-table.mode", "flat-rate");
		List<ItemStack> tableItems = new ArrayList<ItemStack>();
		tableItems.add(new ItemStack(Material.EMERALD, 3));
		tableItems.add(new ItemStack(Material.DIAMOND, 1));
		getConfig().set("enchantment-table.items", tableItems);
		getConfig().set("enchantment-command.mode", "multiply");
		List<ItemStack> commandItems = new ArrayList<ItemStack>();
		commandItems.add(new ItemStack(Material.EMERALD, 5));
		commandItems.add(new ItemStack(Material.DIAMOND, 3));
		getConfig().set("enchantment-command.items", commandItems);
		getConfig().set("messages.success", "&aSuccessfully enchanted &9%enchanted-item% &ausing:");
		getConfig().set("messages.failure", "&cFailed to enchant &9%enchanted-item%&c, it requires:");
		getConfig().set("messages.show-items", true);
		getConfig().set("messages.item-format", "&9%payment-amount% x %payment-item%");
		saveConfig();
	}
	
	@SuppressWarnings("unchecked")
	public void createGraphs(Metrics metrics) {
		Graph modeGraph = metrics.createGraph("Mode");
		modeGraph.addPlotter(new Metrics.Plotter("Flat rate") {
			 @Override
			 public int getValue() {
				 int value = 0;
				 if (getConfig().getString("enchantment-table.mode").equals("flat-rate")) {
					 value++;
				 }
				 if (getConfig().getString("enchantment-command.mode").equals("flat-rate")) {
					 value++;
				 }
				 return value;
			 }
		});
		modeGraph.addPlotter(new Metrics.Plotter("Multiply") {
			@Override
			 public int getValue() {
				 int value = 0;
				 if (getConfig().getString("enchantment-table.mode").equals("multiply")) {
					 value++;
				 }
				 if (getConfig().getString("enchantment-command.mode").equals("multiply")) {
					 value++;
				 }
				 return value;
			 }
		});
		Graph itemsGraph = metrics.createGraph("Items");
		Set<Material> items = new HashSet<Material>();
		for (ItemStack item : (List<ItemStack>) this.getConfig().getList("enchantment-table.items")) {
			items.add(item.getType());
		}
		for (ItemStack item : (List<ItemStack>) this.getConfig().getList("enchantment-command.items")) {
			items.add(item.getType());
		}
		for (Material item : items) {
			itemsGraph.addPlotter(new Metrics.Plotter(item.toString()) {
				@Override
				public int getValue() {
					return 1;
				}
			});
		}
	}

}
