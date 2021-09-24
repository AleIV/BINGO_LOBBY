package me.aleiv.core.paper.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.objects.Cinematic;
import me.aleiv.core.paper.objects.Frame;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("cinematic")
@CommandPermission("cinematic.cmd")
public class CinematicCMD extends BaseCommand {

    private @NonNull Core instance;

    public CinematicCMD(Core instance) {
        this.instance = instance;

    }

    public void record(Player player, List<Frame> frames, int seconds){
        var task = new BukkitTCT();

        var count = 0;
        for (int i = 0; i < seconds; i++) {
            for (int j = 0; j < 20; j++) {

                var c = (int) count/20;
                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        instance.sendActionBar(player, ChatColor.YELLOW + "" + c + "/" + seconds);
                        var loc = player.getLocation().clone();
                        var frame = new Frame(loc.getWorld().getName().toString(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                        frames.add(frame);
                    }
    
                }, 50);

                count++;
            }
            
        }

        task.execute();
    }

    @Subcommand("stop")
    public void stopRec(Player sender){

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var recording = game.getRecording();
        var uuid = sender.getUniqueId();

        if(!recording.containsKey(uuid)){
            sender.sendMessage(ChatColor.RED + "You are not recording.");

        }else{
            var cinematic = recording.get(uuid);
            recording.remove(uuid);
            cinematics.put(cinematic.getName(), cinematic);
            sender.sendMessage(ChatColor.GREEN + "Cinematic recorded and saved.");
        }
    }

    @Subcommand("rec-add")
    public void recAdd(Player sender, String cinematic){

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        

        if(!cinematics.containsKey(cinematic)){
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        }else{
            
            var task = new BukkitTCT();

            var count = 3;
            while(count >= 0){
                final var c = count;

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(c == 0){
                            sender.sendMessage(ChatColor.DARK_RED + "REC.");
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            var cine = cinematics.get(cinematic);
                            game.getRecording().put(sender.getUniqueId(), cine);
                            
    
                        }else{
                            sender.sendMessage(ChatColor.DARK_RED + "" + c);
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        }
                    }
    
                }, 50*20);

                count--;
            }
    
            task.execute();

        }
    }

    @Subcommand("rec")
    public void rec(Player sender, String cinematic){

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        

        if(cinematics.containsKey(cinematic)){
            sender.sendMessage(ChatColor.RED + "Cinematic already exist.");

        }else{
            
            var task = new BukkitTCT();

            var count = 3;
            while(count >= 0){
                final var c = count;

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(c == 0){
                            sender.sendMessage(ChatColor.DARK_RED + "REC.");
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            game.getRecording().put(sender.getUniqueId(), new Cinematic(cinematic));
                            
    
                        }else{
                            sender.sendMessage(ChatColor.DARK_RED + "" + c);
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        }
                    }
    
                }, 50*20);

                count--;
            }
    
            task.execute();

        }
    }

    @Subcommand("rec-temp")
    public void recTemp(Player sender, String cinematic, int seconds){

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        

        if(cinematics.containsKey(cinematic)){
            sender.sendMessage(ChatColor.RED + "Cinematic already exist.");

        }else{


            var cine = new Cinematic(cinematic);
            cinematics.put(cinematic, cine);
            
            var frames = cine.getFrames();
            
            var task = new BukkitTCT();

            var count = 3;
            while(count >= 0){
                final var c = count;

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(c == 0){
                            sender.sendTitle(ChatColor.DARK_RED + "REC.", "", 0, 20, 20);
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            record(sender, frames, seconds);
    
                        }else{
                            sender.sendTitle(ChatColor.DARK_RED + "" + c, "", 0, 20, 20);
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        }
                    }
    
                }, 50*20);

                count--;
            }
    
            task.execute();

        }
    }

    @Subcommand("play")
    @CommandCompletion("@players")
    public void play(CommandSender sender, @Flags("other") Player player, String cinematic){

        var game = instance.getGame();
        var cinematics = game.getCinematics();

        if(!cinematics.containsKey(cinematic)){
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        }else{
            var cine = cinematics.get(cinematic);
            var frames = cine.getFrames();

            var task = new BukkitTCT();

            frames.forEach(frame ->{

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        var world = Bukkit.getWorld(frame.getWorld());
                        var loc = new Location(world, frame.getX(), frame.getY(), frame.getZ(), frame.getYaw(), frame.getPitch());
                        player.teleport(loc);
                    }
    
                }, 50);
            });
    
            task.execute();
        
        }

    }
}
