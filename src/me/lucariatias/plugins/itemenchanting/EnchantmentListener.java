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
				
				if (inventory.contains(cost)) {
					inventory.removeItem(cost);
					event.setExpLevelCost(0);
				} else {
					player.sendMessage(failMsg.replaceAll("&", "¤").replaceAll("%item-amount%", numItems.toString()).replaceAll("%item-name%", item.toString().toLowerCase()).replaceAll("%player%", player.getName()).replaceAll("%enchantment-name%", enchantmentName).replaceAll("%enchantment-level%", enchantmentLevel.toString()));
					event.getEnchantsToAdd().put(enchantmentType, 0);
				}
			}
		}
    }
}
