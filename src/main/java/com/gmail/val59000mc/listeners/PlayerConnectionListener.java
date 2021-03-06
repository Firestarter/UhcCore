package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerJoinException;
import com.gmail.val59000mc.exceptions.UhcTeamException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.threads.KillDisconnectedPlayerThread;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

public class PlayerConnectionListener implements Listener{

	private final ViaAPI viaAPI = Via.getAPI();

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event){
		GameManager gm = GameManager.getGameManager();

		// Player is not allowed to join so don't create UhcPlayer. (Server full, whitelist, ban, ...)
		if (event.getResult() != Result.ALLOWED){
			return;
		}
		
		try{
			boolean allowedToJoin = gm.getPlayersManager().isPlayerAllowedToJoin(event.getPlayer());

			if (allowedToJoin){
				// Create player if not existent.
				// gm.getPlayersManager().getOrCreateUhcPlayer(event.getPlayer()); // Firestarter :: move down player creation
			}else{
				throw new UhcPlayerJoinException("An unexpected error as occured.");
			}
		}catch(final UhcPlayerJoinException e){
			event.setKickMessage(e.getMessage());
			event.setResult(Result.KICK_OTHER);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent event){
		event.setJoinMessage(null); // Firestarter :: no join messages
		Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), new Runnable() {

			@Override
			public void run() {
				// Firestarter start :: yell at players on non-1.8 versions
				if (viaAPI.getPlayerVersion(event.getPlayer()) != 47) {
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lIMPORTANT! &cPlaying on NON-1.8 CLIENTS will result in a &e&lSIGNIFICANTLY &cworse experience for you. PLEASE &e&lSWITCH TO 1.8.8 &cIF POSSIBLE!"));
				}
				// Firestarter end

				// Firestarter start :: send to lobby world if the world is still loading
				if (GameManager.getGameManager().getGameState() == GameState.LOADING) {
					Player player = event.getPlayer();
					player.teleport(GameManager.getGameManager().getLobbyLocation());
					player.setGameMode(GameMode.SPECTATOR);
					player.setMaxHealth(20);
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 0, true, false));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lUHC: &7A new round is currently being loaded."));
					player.playSound(player.getLocation(), Sound.valueOf("NOTE_BASS_DRUM"), 1.0f, 1.0f);
					player.playEffect(player.getLocation(), Effect.RECORD_PLAY, 7);
					return;
				}

				GameManager.getGameManager().getPlayersManager().playerJoinsTheGame(event.getPlayer());
				// Firestarter end
			}
		}, 1);
		
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerQuitEvent event){
		event.setQuitMessage(null); // Firestarter :: no quit messages

		GameManager gm = GameManager.getGameManager();
		// Firestarter start :: custom quit messages in lobby
		if (gm.getGameState() == GameState.WAITING) {
			Bukkit.broadcastMessage(ChatColor.AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " left the lobby.");
		}
		// Firestarter end
		if(gm.getGameState().equals(GameState.WAITING) || gm.getGameState().equals(GameState.STARTING)){
			UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(event.getPlayer());

			if(gm.getGameState().equals(GameState.STARTING)){
				gm.getPlayersManager().setPlayerSpectateAtLobby(uhcPlayer);
				// gm.broadcastInfoMessage(uhcPlayer.getName()+" has left while the game was starting and has been killed."); // Firestarter :: no broadcasts
				gm.getPlayersManager().strikeLightning(uhcPlayer);
			}

			try{
				uhcPlayer.getTeam().leave(uhcPlayer);
			}catch (UhcTeamException e){
				// Nothing
			}

			gm.getPlayersManager().getPlayersList().remove(uhcPlayer);
		}

		if(gm.getGameState().equals(GameState.PLAYING) || gm.getGameState().equals(GameState.DEATHMATCH)){
			UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(event.getPlayer());
			if(gm.getConfiguration().getEnableKillDisconnectedPlayers() && uhcPlayer.getState().equals(PlayerState.PLAYING)){
				Bukkit.getScheduler().runTaskLaterAsynchronously(UhcCore.getPlugin(), new KillDisconnectedPlayerThread(event.getPlayer().getUniqueId()),1);
			}
			if(gm.getConfiguration().getSpawnOfflinePlayers() && uhcPlayer.getState().equals(PlayerState.PLAYING)){
				gm.getPlayersManager().spawnOfflineZombieFor(event.getPlayer());
			}
			gm.getPlayersManager().checkIfRemainingPlayers();
		}
	}

}