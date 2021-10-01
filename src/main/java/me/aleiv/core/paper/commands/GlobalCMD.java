package me.aleiv.core.paper.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
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
import me.aleiv.core.paper.teams.exceptions.TeamAlreadyExistsException;
import me.aleiv.core.paper.utilities.Frames;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("global")
@CommandPermission("admin.perm")
public class GlobalCMD extends BaseCommand {

    private @NonNull Core instance;
    Entity current = null;

    public GlobalCMD(Core instance) {
        this.instance = instance;

    }

    @CommandPermission("admin.perm")
    @Subcommand("tpworld")
    @CommandAlias("tpworld")
    @CommandCompletion("@worlds")
    public void tpWorld(Player player, World world) {
        player.teleport(world.getSpawnLocation());
        player.sendMessage(ChatColor.GRAY + "Teleported to world " + world.getName());
    }

    @Subcommand("team-list")
    public void infoTeams(CommandSender sender) {
        var man = instance.getTeamManager();
        var map = man.getTeamsMap();
        var str = new StringBuilder();
        for (var team : map.values()) {
            List<String> list = new ArrayList<>();
            for (var member : team.getMembers()) {
                var player = Bukkit.getPlayer(member);
                if(player != null){
                    list.add(player.getName());
                }
            }
            str.append(ChatColor.WHITE + team.getTeamName() + ": " + ChatColor.GOLD + team.getPoints() + " " + list.toString());
        }

        sender.sendMessage(str.toString());
    }

    @Subcommand("info")
    public void infoPlayer(CommandSender sender, @Flags("other") Player player) {
        var man = instance.getTeamManager();
        var uuid = player.getUniqueId();
        var team = man.getPlayerTeam(uuid);

        if(team != null){
            sender.sendMessage(ChatColor.GREEN + player.getName() + " IS " + team.getTeamName() + ": " + "POINTS: " + team.getPoints());   
        }else{
            sender.sendMessage(ChatColor.DARK_RED + "Team is null");
        }
    }

    @Subcommand("create-team")
    @CommandCompletion("@players")
    public void createTeam(CommandSender sender, String... str) {
        var manager = instance.getTeamManager();

        CompletableFuture.supplyAsync(() -> {
            List<UUID> playerUuids = new ArrayList<>();

            for (var s : str) {
                var player = Bukkit.getPlayer(s);
                if (player != null) {
                    playerUuids.add(player.getUniqueId());
                }
            }

            var arr = playerUuids.toArray(new UUID[] {});
            var map = manager.getTeamsMap();
            var name = "#" + (map.values().size() + 1);

            try {
                manager.createTeam(name, arr);

            } catch (TeamAlreadyExistsException e) {

                e.printStackTrace();
            }

            return name;
        }).thenAccept(name -> {
            sender.sendMessage(ChatColor.DARK_RED + "Team " + name + " created.");

        });

    }

    @Subcommand("destroy-team")
    @CommandCompletion("@players")
    public void destroyTeam(CommandSender sender, @Flags("other") Player player) {
        var manager = instance.getTeamManager();

        var team = manager.getPlayerTeam(player.getUniqueId());
        if (team != null) {
            CompletableFuture.supplyAsync(() -> {

                manager.destroyTeam(team);

                return true;
            }).thenAccept(bool -> {
                sender.sendMessage(ChatColor.DARK_RED + "Team destroyed.");

            });
        }

    }

    @Subcommand("destroy-all-teams")
    public void destroyTeamAll(CommandSender sender) {
        var manager = instance.getTeamManager();
        var map = manager.getTeamsMap();

        CompletableFuture.supplyAsync(() -> {

            map.values().forEach(team ->{
                manager.destroyTeam(team);
            });

            return true;
        }).thenAccept(bool -> {
            sender.sendMessage(ChatColor.DARK_RED + "Teams destroyed.");

        });

    }

    @Subcommand("create-global-ffa-teams")
    public void createAllTeams(CommandSender sender) {
        var manager = instance.getTeamManager();
        var map = manager.getTeamsMap();

        CompletableFuture.supplyAsync(() -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                var uuid = player.getUniqueId();
                if (manager.getPlayerTeam(uuid) == null) {
                    try {

                        var name = "#" + (map.values().size() + 1);
                        var list = List.of(uuid);
                        var arr = list.toArray(new UUID[] {});
                        manager.createTeam(name, arr);

                    } catch (TeamAlreadyExistsException e) {

                        e.printStackTrace();
                    }
                }
            });

            return true;
        }).thenAccept(added -> {
            sender.sendMessage(ChatColor.DARK_RED + "Created a team for all players.");

        });
    }

    @Subcommand("dataset")
    public void changeSet(CommandSender sender, String string, boolean bool) {
        var manager = instance.getTeamManager();

        CompletableFuture.supplyAsync(() -> {
            manager.changeDataset(string, bool);

            return true;
        }).thenAccept(added -> {
            sender.sendMessage(ChatColor.DARK_RED + "Changed dataset to " + string);

        });

    }

    @Subcommand("points")
    @CommandCompletion("@players")
    public void changeSet(CommandSender sender, @Flags("other") Player player, Integer i) {
        var manager = instance.getTeamManager();
        var uuid = player.getUniqueId();
        var team = manager.getPlayerTeam(uuid);
        if(team != null){
            CompletableFuture.supplyAsync(() -> {
                manager.addPoints(team, i);
                return true;
            }).thenAccept(added -> {
                if(added){
                    sender.sendMessage(ChatColor.DARK_RED + "Points " + player.getName() + " to " + i);
                }
    
            });
        }
            

        

    }

    @Subcommand("play-animation")
    public void playAnimation(CommandSender sender, Integer from, Integer until, String... text) {
        var task = new BukkitTCT();
        var animation = Frames.getFramesCharsIntegersAll(from, until);

        var newText = new StringBuilder();
        for (var charac : text) {
            newText.append(charac);
        }

        animation.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player ->{
                        instance.showTitle(player, frame + "", ChatColor.GOLD + newText.toString(), 0, 20, 0);
                    });

                }

            }, 50);
        });

        task.execute();

    }
}
