package me.lucariatias.plugins.itemenchanting;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantmentListener implements Listener {
	private ItemEnchanting plugin;
    public EnchantmentListener(ItemEnchanting plugin){
        this.plugin = plugin;
    }
	
	@EventHandler
    public void onPlayerEnchant(EnchantItemEvent event) {
		if (!event.isCancelled()) {
			Player player = event.getEnchanter();
			Inventory inventory = player.getInventory();
			if (plugin.getConfig().getInt("ItemMultiplier") < 0) {
				ItemStack itemstack = new ItemStack (Material.getMaterial(plugin.getConfig().getInt("ItemID")), plugin.getConfig().getInt("FlatRate"));
				if (inventory.contains(plugin.getConfig().getInt("ItemID"), plugin.getConfig().getInt("FlatRate"))) {
			        inventory.removeItem(itemstack);
					event.setExpLevelCost(0);
				} else {
	                player.sendMessage(ChatColor.RED + "You do not have the required items! You need " + ChatColor.GOLD + plugin.getConfig().getInt("FlatRate") + Material.getMaterial(plugin.getConfig().getInt("ItemID")).toString() + ChatColor.RED + " to add this enchantment.");
				    event.setCancelled(true);
				}
			} else {
				ItemStack itemstack = new ItemStack (Material.getMaterial(plugin.getConfig().getInt("ItemID")), event.getExpLevelCost() * plugin.getConfig().getInt("ItemMultiplier"));
				if (inventory.contains(plugin.getConfig().getInt("ItemID"),event.getExpLevelCost() * plugin.getConfig().getInt("ItemMultiplier"))) {
			        inventory.removeItem(itemstack);
					event.setExpLevelCost(0);
				} else {
				    player.sendMessage(ChatColor.RED + "You do not have the required items! You need " + ChatColor.GOLD + event.getExpLevelCost() * plugin.getConfig().getInt("ItemMultiplier") + " " + Material.getMaterial(plugin.getConfig().getInt("ItemID")).toString() + "(s)" + ChatColor.RED + " to add this enchantment.");
				    event.setCancelled(true);
				}
			}
		}
    }
}
