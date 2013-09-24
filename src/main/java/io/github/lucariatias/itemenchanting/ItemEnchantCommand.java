package io.github.lucariatias.itemenchanting;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
						ItemStack[] itemStacks = new ItemStack[]{};
						String mode = plugin.getConfig().getString("enchantment-command.mode", "multiply");
						for (String section : plugin.getConfig().getConfigurationSection("enchantment-command.items").getKeys(false)) {
							if (mode.equalsIgnoreCase("flat-rate")) {
								itemStacks = Arrays.copyOf(itemStacks, itemStacks.length + 1);
								itemStacks[itemStacks.length - 1] = new ItemStack(Material.getMaterial(section), plugin.getConfig().getInt("enchantment-command.items." + section + ".amount"), (short) plugin.getConfig().getInt("enchantment-command.items." + section + ".data"));
							} else if (mode.equalsIgnoreCase("multiply")) {
								itemStacks = Arrays.copyOf(itemStacks, itemStacks.length + 1);
								itemStacks[itemStacks.length - 1] = new ItemStack(Material.getMaterial(section), plugin.getConfig().getInt("enchantment-command.items." + section + ".amount") * level, (short) plugin.getConfig().getInt("enchantment-command.items." + section + ".data"));
							}
						}
						
						//Messages
						String successMessage = plugin.getConfig().getString("messages.success", "&aSuccessfully enchanted ??9%enchanted-item% &ausing:").replaceAll("%enchanted-item%", player.getItemInHand().getType().toString()).replaceAll("&", "??");
						String failureMessage = plugin.getConfig().getString("messages.failure", "&cFailed to enchant &9%enchanted-item%&c, it requires:").replaceAll("%enchanted-item%", player.getItemInHand().getType().toString()).replaceAll("&", "??");
						String[] itemMessages = new String[]{};
						for (ItemStack itemStack : itemStacks) {
							itemMessages = Arrays.copyOf(itemMessages, itemMessages.length + 1);
							itemMessages[itemMessages.length - 1] = plugin.getConfig().getString("messages.item-format", "&9%payment-amount% x %payment-item%").replaceAll("%payment-amount%", Integer.toString(itemStack.getAmount())).replaceAll("%payment-item%", itemStack.getType().toString()).replaceAll("&", "??");
						}
						
						//Item management
						if (this.inventoryContains(player.getInventory(), itemStacks)) {
							if (sender.hasPermission("itemenchanting.unsafe")) {
								player.getItemInHand().addUnsafeEnchantment(Enchantment.getByName(args[0].toUpperCase()), level);
							} else {
								player.getItemInHand().addEnchantment(Enchantment.getByName(args[0].toUpperCase()), level);
							}
							player.getInventory().removeItem(itemStacks);
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
