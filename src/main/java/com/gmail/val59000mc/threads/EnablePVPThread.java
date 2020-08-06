package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EnablePVPThread implements Runnable{

	private GameManager gm;
	private int timeBeforePvp;
	
	public EnablePVPThread(){
		gm = GameManager.getGameManager();
		timeBeforePvp = gm.getConfiguration().getTimeBeforePvp();
	}
	
	@Override
	public void run() {
		if(!gm.getGameState().equals(GameState.PLAYING)) {
			return; // Stop thread
		}

		if(timeBeforePvp == 0){
			GameManager.getGameManager().setPvp(true);
			// Firestarter start :: use custom title messages
			//GameManager.getGameManager().broadcastInfoMessage(Lang.PVP_ENABLED);
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "Stay safe out there...", "The grace period is over");
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, true, false));
			}
			GameManager.getGameManager().getPlayersManager().playSoundToAll(UniversalSound.ENDERDRAGON_GROWL);
			// Firestarter end
			return; // Stop thread
		}

		if(timeBeforePvp <= 10 || (timeBeforePvp < 60*5 && timeBeforePvp%60 == 0) || timeBeforePvp%(60*5) == 0){
			// Firestarter start :: use custom messages
			if(timeBeforePvp%60 == 0) {
				// gm.broadcastInfoMessage(Lang.PVP_START_IN + " " + (timeBeforePvp / 60) + "m");
				int minutes = (timeBeforePvp / 60);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lUHC: &fGrace period ends in " + minutes + (minutes == 1 ? " minute." : " minutes.")));
			}else{
				// gm.broadcastInfoMessage(Lang.PVP_START_IN + " " + timeBeforePvp + "s");
				for (Player player : Bukkit.getOnlinePlayers()) {
					ChatColor color = timeBeforePvp <= 3 ? ChatColor.RED : ChatColor.GOLD;
					player.sendTitle(color + ChatColor.BOLD.toString() + timeBeforePvp, "Grace period ending");
				}
			}
			// Firestarter end

			gm.getPlayersManager().playSoundToAll(UniversalSound.CLICK);
		}

		if(timeBeforePvp >= 20){
			timeBeforePvp -= 10;
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this,200);
		}else{
			timeBeforePvp --;
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this,20);
		}

	}

}