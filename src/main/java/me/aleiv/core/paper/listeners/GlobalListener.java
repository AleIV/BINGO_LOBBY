package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.objects.Frame;

public class GlobalListener implements Listener{
    
    Core instance;

    public GlobalListener(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onGameTick(GameTickEvent e) {
        Bukkit.getScheduler().runTask(instance, () -> {
            
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        var game = instance.getGame();
        var recording = game.getRecording();
        var uuid = e.getPlayer().getUniqueId();
        if(recording.containsKey(uuid)){
            recording.remove(uuid);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent e){
        var action = e.getAction();
        var game = instance.getGame();
        var recording = game.getRecording();
        var uuid = e.getPlayer().getUniqueId();
        if(recording.containsKey(uuid) && action == Action.RIGHT_CLICK_AIR){
            var player = e.getPlayer();
            player.setVelocity(player.getLocation().getDirection());
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e){
        var game = instance.getGame();
        var recording = game.getRecording();
        var uuid = e.getPlayer().getUniqueId();
        if(recording.containsKey(uuid)){
            var cinematic = recording.get(uuid);
            var frames = cinematic.getFrames();
            var loc = e.getPlayer().getLocation().clone();
            var frame = new Frame(loc.getWorld().getName().toString(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            frames.add(frame);
        }
    }
}
