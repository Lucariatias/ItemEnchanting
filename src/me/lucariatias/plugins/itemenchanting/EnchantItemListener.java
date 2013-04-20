package me.lucariatias.plugins.itemenchanting;

import java.util.Arrays;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantItemListener implements Listener {
	
	private ItemEnchanting plugin;
	
	public EnchantItemListener(ItemEnchanting plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEnchantItem(EnchantItemEvent event) {
		if (!event.isCancelled()) {
			Player player = event.getEnchanter();
			
			if (player.getGameMode() != GameMode.CREATIVE) {
				//Items
				ItemStack[] itemStacks = new ItemStack[]{};
				String mode = plugin.getConfig().getString("enchantment-table.mode", "flat-rate");
				for (String section : plugin.getConfig().getConfigurationSection("enchantment-table.items").getKeys(false)) {
					if (mode.equalsIgnoreCase("flat-rate")) {
						itemStacks = Arrays.copyOf(itemStacks, itemStacks.length + 1);
						itemStacks[itemStacks.length - 1] = new ItemStack(Material.getMaterial(section), plugin.getConfig().getInt("enchantment-table.items." + section + ".amount"), (short) plugin.getConfig().getInt("enchantment-table.items." + section + ".data"));
					} else if (mode.equalsIgnoreCase("multiply")) {
						itemStacks = Arrays.copyOf(itemStacks, itemStacks.length + 1);
						itemStacks[itemStacks.length - 1] = new ItemStack(Material.getMaterial(section), plugin.getConfig().getInt("enchantment-table.items." + section) * event.getExpLevelCost(), (short) plugin.getConfig().getInt("enchantment-table.items." + section + ".data"));
					}
				}
				
				//Messages
				String successMessage = plugin.getConfig().getString("messages.success", "¤aSuccessfully enchanted ¤9%enchanted-item% ¤ausing:").replaceAll("%enchanted-item%", event.getItem().getType().toString()).replaceAll("&", "¤");
				String failureMessage = plugin.getConfig().getString("messages.failure", "¤cFailed to enchant ¤9%enchanted-item%¤c, it requires:").replaceAll("%enchanted-item%", event.getItem().getType().toString()).replaceAll("&", "¤");
				String[] itemMessages = new String[]{};
				for (ItemStack itemStack : itemStacks) {
					itemMessages = Arrays.copyOf(itemMessages, itemMessages.length + 1);
					itemMessages[itemMessages.length - 1] = plugin.getConfig().getString("messages.item-format", "¤9%payment-amount% x %payment-item%").replaceAll("%payment-amount%", Integer.toString(itemStack.getAmount())).replaceAll("%payment-item%", itemStack.getType().toString()).replaceAll("&", "¤");
				}
				
				//Item management
				if (this.inventoryContains(player.getInventory(), itemStacks)) {
					player.getInventory().removeItem(itemStacks);
					player.sendMessage(successMessage);
					if (plugin.getConfig().getBoolean("messages.show-items", true)) {
						for (String itemMessage : itemMessages) {
							player.sendMessage(itemMessage);
						}
					}
					event.setExpLevelCost(0);
				} else {
					event.setCancelled(true);
					player.sendMessage(failureMessage);
					if (plugin.getConfig().getBoolean("messages.show-items", true)) {
						for (String itemMessage : itemMessages) {
							player.sendMessage(itemMessage);
						}
					}
				}
			}
		}
	}
	
	private Boolean inventoryContains(Inventory inventory, ItemStack... itemStacks) {
		Boolean contains = true;
		for (ItemStack itemStack : itemStacks) {
			if (!inventory.containsAtLeast(itemStack, itemStack.getAmount())) {
				contains = false;
			}
		}
		return contains;
	}

}
