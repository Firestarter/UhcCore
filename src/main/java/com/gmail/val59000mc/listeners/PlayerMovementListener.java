package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.game.GameState;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener{

    private PlayersManager playersManager;

    public PlayerMovementListener(PlayersManager playersManager){
        this.playersManager = playersManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        // Firestarter start :: don't handle events while the game is loading
        if (GameManager.getGameManager().getGameState() == GameState.LOADING) {
            return;
        }
        // Firestarter end
        handleFrozenPlayers(event);
    }

    private void handleFrozenPlayers(PlayerMoveEvent e){
        UhcPlayer uhcPlayer = GameManager.getGameManager().getPlayersManager().getUhcPlayer(e.getPlayer()); // Firestarter :: always use correct player manager instance
        if (uhcPlayer.isFrozen()){
            Location freezeLoc = uhcPlayer.getFreezeLocation();
            Location toLoc = e.getTo();

            if (toLoc.getBlockX() != freezeLoc.getBlockX() || toLoc.getBlockZ() != freezeLoc.getBlockZ()){
                Location newLoc = toLoc.clone();
                newLoc.setX(freezeLoc.getBlockX() + .5);
                newLoc.setZ(freezeLoc.getBlockZ() + .5);

                e.getPlayer().teleport(newLoc);
            }
        }
    }

}