package com.bigbade.silktouchspawners;

import org.bukkit.plugin.java.JavaPlugin;

public class SilkTouchSpawners extends JavaPlugin {
	@Override
	public void onEnable() {
		//Register events
		getServer().getPluginManager().registerEvents(new BlockBreak(this), this);
	}
	
	@Override
	public void onDisable() {
		
	}
}
