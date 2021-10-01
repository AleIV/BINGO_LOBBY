package me.aleiv.core.paper.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.objects.Frame;

public class GlobalListener implements Listener{
    
    Core instance;

    String black = Character.toString('\u3400');

    List<String> list = new ArrayList<>();

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
    public void onMove(PlayerMoveEvent e){
        var to = e.getTo();
        if(to.getY() > 110) return;

        var player = e.getPlayer();
        if(to.getWorld().getName().toString().contains("lobby") && !list.contains(player.getName())){   
            list.add(player.getName());
            var lobby = Bukkit.getWorld("lobby");
            var loc = new Location(lobby, 0.5, 126, 0.5, 90, -0);

            instance.showTitle(player, black, "", 10, 10, 10);
            Bukkit.getScheduler().runTaskLater(instance, task ->{
                player.teleport(loc);
                list.remove(player.getName());
            }, 15);
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

    //@EventHandler
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

    public boolean shouldInteract(Player player){
        var lobby = Bukkit.getWorld("lobby");

        if(player.getWorld() == lobby && !player.hasPermission("admin.perm")){
            return false;
        }

        return true;
    }

    @EventHandler
    public void sit(PlayerInteractAtEntityEvent e){
        var entity = e.getRightClicked();
        var player = e.getPlayer();
        
        if(entity != null && entity instanceof ArmorStand stand && !stand.hasBasePlate() && stand.getPassengers().isEmpty()){
            stand.addPassenger(player);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        var player = e.getPlayer();
        if (!shouldInteract(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e){
        var world = Bukkit.getWorld("lobby");
        var player = (Player) e.getEntity();
        if (player.getWorld() == world) {
            player.setFoodLevel(20);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        var player = e.getPlayer();
        if (!shouldInteract(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent e) {
        var player = (Player) e.getPlayer();
        if (!shouldInteract(player) && e.getInventory().getType() != InventoryType.CHEST) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        var world = Bukkit.getWorld("lobby");
        var entity = e.getEntity();
        if (entity instanceof Player) {
            var player = (Player) entity;
            if (player.getWorld() == world) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent e) {
        var damager = e.getDamager();
        if (damager instanceof Player) {
            var player = (Player) damager;
            if (!shouldInteract(player)) {
                e.setCancelled(true);
            }
        }

    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        var player = e.getPlayer();
        var lobby = Bukkit.getWorld("lobby");
        var loc = new Location(lobby, 0.5, 126, 0.5, 90, -0);

        player.teleport(loc);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player player) {
            if(!shouldInteract(player)){
                e.setCancelled(true);
            }

        }
    }




}
