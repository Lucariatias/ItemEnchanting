package me.lucariatias.plugins.itemenchanting;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
			Set<Enchantment> enchants = event.getEnchantsToAdd().keySet();
			Iterator<Enchantment> enchantmentIterator = enchants.iterator();
			Boolean successful = false;
			
			while (enchantmentIterator.hasNext()) {
				Enchantment enchantmentType = enchantmentIterator.next();
				String enchantmentName = enchantmentType.getName().toLowerCase();
				Integer enchantmentLevel = event.getEnchantsToAdd().get(enchantmentType);
				Material item = Material.getMaterial(plugin.getConfig().getString("enchantments." + enchantmentName + ".item-name"));
				Integer numItems = 0;
				String failMsg = plugin.getConfig().getString("enchantments." + enchantmentName + ".fail-message");
				
				switch (plugin.getConfig().getInt("enchantments." + enchantmentName + ".mode")) {
					case 0:
						//Flatrates
						numItems = plugin.getConfig().getInt("enchantments." + enchantmentName + ".amount");
						break;
					case 1:
						//Exp level multiplier
						numItems = plugin.getConfig().getInt("enchantments." + enchantmentName + ".amount") * event.getExpLevelCost();
						break;
					case 2:
						//Enchant level multiplier
						numItems = plugin.getConfig().getInt("enchantments." + enchantmentName + ".amount") * event.getEnchantsToAdd().get(enchantmentType);
						break;
					default:
						//When anything else is given
						numItems = 0;
				}
				
				ItemStack cost = new ItemStack(item, numItems);
				
				if (enchantmentLevel > 0) {
					if (inventory.contains(cost.getType(), cost.getAmount())) {
						inventory.removeItem(cost);
						player.sendMessage(plugin.getConfig().getString("messages.successful-enchant").replaceAll("&", "¤").replaceAll("%item-amount%", numItems.toString()).replaceAll("%item-name%", item.toString().toLowerCase()).replaceAll("%player%", player.getName()).replaceAll("%enchantment-name%", enchantmentName).replaceAll("%enchantment-level%", enchantmentLevel.toString()));
						successful = true;
					} else {
						player.sendMessage(failMsg.replaceAll("&", "¤").replaceAll("%item-amount%", numItems.toString()).replaceAll("%item-name%", item.toString().toLowerCase()).replaceAll("%player%", player.getName()).replaceAll("%enchantment-name%", enchantmentName).replaceAll("%enchantment-level%", enchantmentLevel.toString()));
						event.getEnchantsToAdd().put(enchantmentType, 0);
					}
				}
			}
			
			if (!successful) {
				event.setCancelled(true);
			} else {
				event.setExpLevelCost(0);
			}
		}
    }
}
