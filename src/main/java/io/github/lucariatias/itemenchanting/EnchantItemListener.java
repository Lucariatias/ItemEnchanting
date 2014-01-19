package io.github.lucariatias.itemenchanting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
	
	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.HIGH)
	public void onEnchantItem(EnchantItemEvent event) {
		if (!event.isCancelled()) {
			Player player = event.getEnchanter();
			if (player.getGameMode() != GameMode.CREATIVE) {
				//Items
				List<ItemStack> items = new ArrayList<ItemStack>();
				String mode = plugin.getConfig().getString("enchantment-table.mode", "flat-rate");
				items.addAll((List<ItemStack>) plugin.getConfig().getList("enchantment-table.items"));
				if (mode.equalsIgnoreCase("multiply")) {
					for (ItemStack item : items) {
						item.setAmount(item.getAmount() * event.getExpLevelCost());
					}
				}
				
				//Messages
				String successMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.success", "&aSuccessfully enchanted &9%enchanted-item% &ausing:").replace("%enchanted-item%", event.getItem().getType().toString()));
				String failureMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.failure", "&cFailed to enchant &9%enchanted-item%&c, it requires:").replace("%enchanted-item%", event.getItem().getType().toString()));
				List<String> itemMessages = new ArrayList<String>();
				for (ItemStack item : items) {
					itemMessages.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.item-format", "&9%payment-amount% x %payment-item%").replace("%payment-amount%", Integer.toString(item.getAmount())).replace("%payment-item%", item.getType().toString())));
				}
				
				//Item management
				if (this.inventoryContains(player.getInventory(), items)) {
					for (ItemStack item : items) {
						player.getInventory().removeItem(item);
					}
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
	
	private Boolean inventoryContains(Inventory inventory, List<ItemStack> items) {
		for (ItemStack itemStack : items) {
			if (!inventory.containsAtLeast(itemStack, itemStack.getAmount())) {
				return false;
			}
		}
		return true;
	}

}
