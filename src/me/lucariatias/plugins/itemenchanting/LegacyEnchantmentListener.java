package me.lucariatias.plugins.itemenchanting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LegacyEnchantmentListener implements Listener {
	
	private ItemEnchanting plugin;
    
	public LegacyEnchantmentListener(ItemEnchanting plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerEnchant(EnchantItemEvent event) {
    	if (!event.isCancelled()) {
    		Player player = event.getEnchanter();
    		Inventory inventory = player.getInventory();
    		if (plugin.getConfig().getBoolean("legacy.use-flat-rate")) {
    			ItemStack itemstack = new ItemStack(Material.getMaterial(plugin.getConfig().getString("legacy.item-name")), plugin.getConfig().getInt("legacy.amount"));
    			if (inventory.contains(Material.getMaterial(plugin.getConfig().getString("legacy.item-name")), plugin.getConfig().getInt("legacy.amount"))) {
    				inventory.removeItem(itemstack);
    				event.setExpLevelCost(0);
    			} else {
    				Integer itemAmount = plugin.getConfig().getInt("legacy.amount");
    				player.sendMessage(plugin.getConfig().getString("legacy.fail-message").replaceAll("&", "¤").replaceAll("%item-amount%", itemAmount.toString()).replaceAll("%item-name%", Material.getMaterial(plugin.getConfig().getString("legacy.item-name")).toString()));
    				event.setCancelled(true);
    			}
    		} else {
    			ItemStack itemstack = new ItemStack(Material.getMaterial(plugin.getConfig().getString("legacy.item-name")), event.getExpLevelCost() * plugin.getConfig().getInt("legacy.amount"));
    			if (inventory.contains(Material.getMaterial(plugin.getConfig().getString("legacy.item-name")), event.getExpLevelCost() * plugin.getConfig().getInt("legacy.amount"))) {
    				inventory.removeItem(itemstack);
    				event.setExpLevelCost(0);
    			} else {
    				Integer itemAmount = plugin.getConfig().getInt("legacy.amount") * event.getExpLevelCost();
    				player.sendMessage(plugin.getConfig().getString("legacy.fail-message").replaceAll("&", "¤").replaceAll("%item-amount%", itemAmount.toString()).replaceAll("%item-name%", Material.getMaterial(plugin.getConfig().getString("legacy.item-name")).toString()));
    				event.setCancelled(true);
    			}
    		}
    	}
    }
}
