package me.aleiv.core.paper.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.objects.Cinematic;
import me.aleiv.core.paper.objects.Frame;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("cinematic|c")
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


    @Subcommand("perfect")
    public void perfect(CommandSender sender, String name){
        var game = instance.getGame();
        var cinematics = game.getCinematics();

        if(cinematics.containsKey(name)){
            //var cinematic = cinematics.get(name);
            
            
        }
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
                    public void run(){
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

    @Subcommand("play-all")
    public void playALL(CommandSender sender, String... cinematic){

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var task = new BukkitTCT();
        hide(true);

        List<Integer> list = new ArrayList<>();

        sendBlack();
        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                
            }

        }, 50*110);

        var players = Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList();
        players.forEach(p -> p.setGameMode(GameMode.SPECTATOR));

        for (var str : cinematic) {
            var cine = cinematics.get(str);
            var frames = cine.getProlongedFrames();

            var c = 0;
            for (var frame : frames) {
                var world = Bukkit.getWorld(frame.getWorld());
                var loc = new Location(world, frame.getX(), frame.getY(), frame.getZ(), frame.getYaw(), frame.getPitch());

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        players.forEach(p ->{
                            p.teleport(loc);
                        });
                    }
    
                }, 50);
                c++;
            }
            list.add(c);
            
        }

        var task2 = new BukkitTCT();

        for (var integer : list) {
            task2.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    sendBlack();
                }

            }, ((50*integer) + 50*110)-(50*110));
        }

        var last = instance.getGame().getCinematics().get("41");
        var frames = last.getFrames();
        var frame = frames.get(frames.size()-1);
        var loc = new Location(Bukkit.getWorld(frame.getWorld()), frame.getX(), frame.getY(), frame.getZ(), frame.getYaw(), frame.getPitch());

        for (int i = 0; i < 800; i++) {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    players.forEach(p ->{
                        p.teleport(loc);
                    });
                }

            }, 50);
        }

        task.execute();
        task2.execute();

    }

    @Subcommand("play")
    public void playCinematic(CommandSender sender, String cinematic){

        var game = instance.getGame();
        var cinematics = game.getCinematics();

        if(!cinematics.containsKey(cinematic)){
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        }else{
            var cine = cinematics.get(cinematic);
            var frames = cine.getProlongedFrames();
            var players = Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList();
            players.forEach(p -> p.setGameMode(GameMode.SPECTATOR));
            play(players, frames).execute();

            
        }

    }

    public BukkitTCT play(List<Player> players, List<Frame> frames){
        var task = new BukkitTCT();

            hide(true);
            frames.forEach(frame ->{
                var world = Bukkit.getWorld(frame.getWorld());
                var loc = new Location(world, frame.getX(), frame.getY(), frame.getZ(), frame.getYaw(), frame.getPitch());

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        players.forEach(p ->{
                            
                            p.teleport(loc);
                        });
                    }
    
                }, 50);
            });

            return task;

    }

    public void sendBlack(){
        String black = Character.toString('\u3400');
        Bukkit.getOnlinePlayers().forEach(p ->{
            instance.showTitle(p, black, "", 100, 20, 100);
        });
    }

    public void hide(boolean bool){

        if(bool){
            Bukkit.getOnlinePlayers().forEach(p1 ->{
                Bukkit.getOnlinePlayers().forEach(p2 ->{
                    p1.hidePlayer(instance, p2);
                });
            });
        }else{
            Bukkit.getOnlinePlayers().forEach(p1 ->{
                Bukkit.getOnlinePlayers().forEach(p2 ->{
                    p1.showPlayer(instance, p2);
                });
            });
        }

    }


}
