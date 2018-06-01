package com.bigbade.silktouchspawners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftCreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockBreak implements Listener {

	SilkTouchSpawners plugin;

	public BlockBreak(SilkTouchSpawners plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public String getCreatureName(short entityID) {
		//Turns mob's ID into a mob's name
		String displayName;
		EntityType ct = EntityType.fromId(entityID);
		if (ct != null) {
			displayName = ct.getName();
		} else {
			displayName = String.valueOf(entityID);
		}
		return displayName;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		//Check if player is breaking mob spawner
		if(event.getBlock().getType() == Material.MOB_SPAWNER) {
			//Get item enchantments
			Map<Enchantment, Integer> enchant = event.getPlayer()
					.getEquipment().getItemInMainHand().getEnchantments();
			//Check for silk touch
			if(enchant.containsKey(Enchantment.SILK_TOUCH)) {
				//Get entity ID
				short entityID = getSpawnerEntityID(event.getBlock());
				String name = getCreatureName(entityID);
				name = name.substring(0, 1).toUpperCase()
						+ name.substring(1);
				event.getPlayer().sendMessage(
						ChatColor.GREEN + "You broke a " + name + " spawner!");
				ItemStack spawner = new ItemStack(Material.MOB_SPAWNER, 1,
						entityID);
				ItemMeta meta = spawner.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				//Save the entity ID in the lore
				lore.add("§|" + entityID);
				meta.setLore(lore);
				meta.setDisplayName(ChatColor.RESET + name + " Spawner");
				spawner.setItemMeta(meta);
				org.bukkit.World world = event.getPlayer().getWorld();
				//Drop the item
				world.dropItem(event.getBlock().getLocation(), spawner);
				//Set the block to air (Cause the event is canceled)
				event.getBlock().setType(Material.AIR);
				//Cancel the event to prevent XP, doing it after so I can still
				//use the event object.
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		Block blockPlaced = event.getBlockPlaced();
		
		if(blockPlaced.getType() != Material.MOB_SPAWNER) {
			return;
		}
		//Get the lore
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		int entityID;
		//Check to make sure its a spawner mined
		if(lore.size() == 1) {
			entityID = Integer.parseInt(lore.get(0).toString()
					.replace("§|", ""));
		} else {
			return;
		}
		//Since the event is called before the block is placed,
		//I have to set a delayed event to take the block
		//and set the entity.
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new SpawnerPlace(entityID, blockPlaced), 0);
	}

	@SuppressWarnings("deprecation")
	public short getSpawnerEntityID(Block block) {
		//Get the spawner's entity's ID from the block state.
		BlockState blockState = block.getState();
		CraftCreatureSpawner spawner = ((CraftCreatureSpawner) blockState);
		return spawner.getSpawnedType().getTypeId();
	}
}