package io.github.lucariatias.itemenchanting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemEnchantCommand implements CommandExecutor {
	
	private ItemEnchanting plugin;

	public ItemEnchantCommand(ItemEnchanting plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("itemenchanting.command.itemenchant")) {
			//Parse command parameters
			if (args.length >= 2) {
				if (Enchantment.getByName(args[0].toUpperCase()) != null) {
					try {
						Integer level = Integer.parseInt(args[1]);
						Player player = (Player) sender;
						//Items
						List<ItemStack> items = new ArrayList<ItemStack>();
						String mode = plugin.getConfig().getString("enchantment-command.mode", "flat-rate");
						items.addAll((List<ItemStack>) plugin.getConfig().getList("enchantment-command.items"));
						if (mode.equalsIgnoreCase("multiply")) {
							for (ItemStack item : items) {
								item.setAmount(item.getAmount() * level);
							}
						}
						
						//Messages
						String successMessage = plugin.getConfig().getString("messages.success", "&aSuccessfully enchanted &9%enchanted-item% &ausing:").replace("%enchanted-item%", player.getItemInHand().getType().toString()).replace('&', ChatColor.COLOR_CHAR);
						String failureMessage = plugin.getConfig().getString("messages.failure", "&cFailed to enchant &9%enchanted-item%&c, it requires:").replace("%enchanted-item%", player.getItemInHand().getType().toString()).replace('&', ChatColor.COLOR_CHAR);
						List<String> itemMessages = new ArrayList<String>();
						for (ItemStack item : items) {
							itemMessages.add(plugin.getConfig().getString("messages.item-format", "&9%payment-amount% x %payment-item%").replace("%payment-amount%", Integer.toString(item.getAmount())).replace("%payment-item%", item.getType().toString()).replace('&', ChatColor.COLOR_CHAR));
						}
						
						//Item management
						if (this.inventoryContains(player.getInventory(), items)) {
							if (sender.hasPermission("itemenchanting.unsafe")) {
								player.getItemInHand().addUnsafeEnchantment(Enchantment.getByName(args[0].toUpperCase()), level);
							} else {
								player.getItemInHand().addEnchantment(Enchantment.getByName(args[0].toUpperCase()), level);
							}
							for (ItemStack item : items) {
								player.getInventory().removeItem(item);
							}
							player.sendMessage(successMessage);
							if (plugin.getConfig().getBoolean("messages.show-items", true)) {
								for (String itemMessage : itemMessages) {
									player.sendMessage(itemMessage);
								}
							}
						} else {
							player.sendMessage(failureMessage);
							if (plugin.getConfig().getBoolean("messages.show-items", true)) {
								for (String itemMessage : itemMessages) {
									player.sendMessage(itemMessage);
								}
							}
						}
					} catch (NumberFormatException exception) {
						sender.sendMessage(ChatColor.RED + "You need to specify a number for the level.");
					} catch (IllegalArgumentException exception) {
						sender.sendMessage(ChatColor.RED + "That's not a valid level for this enchant!");
					}
				} else {
					sender.sendMessage(new String[] {ChatColor.RED + "Could not find that enchantment!", ChatColor.GREEN + "You may use these enchantments:"});
					for (Enchantment enchantment : Enchantment.values()) {
						sender.sendMessage(ChatColor.YELLOW + enchantment.getName());
					}
				}
			} else {
				sender.sendMessage(new String[] {ChatColor.RED + "Too few arguments!", ChatColor.GREEN + "Usage: /itemenchant [enchantment] [level]"});
			}
		} else {
			sender.sendMessage(new String[] {ChatColor.RED + "You do not have permission!", ChatColor.GREEN + "Permission node: itemenchanting.command.itemenchant"});
		}
		return true;
	}
	
	private boolean inventoryContains(Inventory inventory, List<ItemStack> items) {
		for (ItemStack itemStack : items) {
			if (!inventory.containsAtLeast(itemStack, itemStack.getAmount())) {
				return false;
			}
		}
		return true;
	}

}
