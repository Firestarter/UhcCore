package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class WorldBorderThread implements Runnable{

	private long timeBeforeShrink;
	private long timeToShrink;
	private int endSize;
	
	public WorldBorderThread(long timeBeforeShrink,int endSize, long timeToShrink){
		this.timeBeforeShrink = timeBeforeShrink;
		this.endSize = endSize;
		this.timeToShrink = timeToShrink;
	}
	
	@Override
	public void run() {
		// Firestarter start :: don't start shrinking the border if the game hasn't started
		if (GameManager.getGameManager().getGameState() != GameState.PLAYING) {
			return;
		}
		// Firestarter end

		if(timeBeforeShrink <= 0){
			startMoving();
		}else{
			timeBeforeShrink--;
			Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), this, 20);
		}
	}
	
	private void startMoving(){
		//GameManager.getGameManager().broadcastInfoMessage(Lang.GAME_BORDER_START_SHRINKING);
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lUHC: &eThe border is now shrinking.")); // Firestarter :: custom chat message

		World overworld = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getOverworldUuid());
		WorldBorder overworldBorder = overworld.getWorldBorder();
		overworldBorder.setSize(2*endSize, timeToShrink);
		
		World nether = Bukkit.getWorld(GameManager.getGameManager().getConfiguration().getNetherUuid());
		if (nether != null) {
			WorldBorder netherBorder = nether.getWorldBorder();
			netherBorder.setSize(endSize, timeToShrink);
		}
	}

}