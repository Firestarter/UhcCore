package com.gmail.val59000mc.commands;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommandExecutor implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("Only players can use this command!");
			return true;
		}

		Player player = (Player) sender;
		GameManager gm = GameManager.getGameManager();

		// Firestarter start :: don't allow command usage during round reset
		if (gm.getGameState() == GameState.LOADING) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lUHC: &7Chat can only be used during a game."));
			return true;
		}
		// Firestarter end

		UhcPlayer uhcPlayer = gm.getPlayersManager().getUhcPlayer(player);

		if(!uhcPlayer.getState().equals(PlayerState.PLAYING)){
			player.sendMessage(Lang.COMMAND_CHAT_ERROR);
			return true;
		}

		if(args.length == 0){
			if(uhcPlayer.isGlobalChat()){
				uhcPlayer.setGlobalChat(false);
				uhcPlayer.sendMessage(Lang.COMMAND_CHAT_TEAM);
			}else{
				uhcPlayer.setGlobalChat(true);
				uhcPlayer.sendMessage(Lang.COMMAND_CHAT_GLOBAL);
			}
			return true;
		}else{
			player.sendMessage(Lang.COMMAND_CHAT_HELP);
			return true;
		}
	}

}