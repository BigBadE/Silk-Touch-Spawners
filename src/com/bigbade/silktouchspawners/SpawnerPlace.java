package com.bigbade.silktouchspawners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftCreatureSpawner;
import org.bukkit.entity.EntityType;

class SpawnerPlace implements Runnable {

	private int entityID;
	private Block block;
	
    public SpawnerPlace(int entityID, Block block) {
        this.entityID = entityID;
        this.block = block;
    }

    public void run() {
        try {
        	setSpawnerEntityID(block, entityID);
        } catch (Exception e) {
        }
    }
    
    @SuppressWarnings("deprecation")
	public void setSpawnerEntityID(Block block, int entityID) {
        BlockState blockState = block.getState();
        if(!(blockState instanceof CreatureSpawner)) {
            throw new IllegalArgumentException("setSpawnerEntityID called on non-spawner block: " + block);
        }
        CraftCreatureSpawner spawner = ((CraftCreatureSpawner)blockState);
        EntityType ct = EntityType.fromId(entityID);
        if(ct == null) {
            throw new IllegalArgumentException("Failed to find creature type for "+entityID);
        }
        spawner.setSpawnedType(ct);
        blockState.update();
   }
}
