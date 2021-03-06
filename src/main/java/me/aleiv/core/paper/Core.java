package me.aleiv.core.paper;

import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.SpigotPlugin;
import lombok.Getter;
import me.aleiv.core.paper.commands.GlobalCMD;
import me.aleiv.core.paper.listeners.GlobalListener;
import me.aleiv.core.paper.objects.Cinematic;
import me.aleiv.core.paper.tablist.Tablist;
import me.aleiv.core.paper.teams.bukkit.BTeamManager;
import me.aleiv.core.paper.utilities.JsonConfig;
import me.aleiv.core.paper.utilities.NegativeSpaces;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import us.jcedeno.libs.rapidinv.RapidInvManager;

@SpigotPlugin
public class Core extends JavaPlugin {

    private static @Getter Core instance;
    private @Getter Game game;
    private @Getter PaperCommandManager commandManager;
    private @Getter static MiniMessage miniMessage = MiniMessage.get();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private JsonConfig redisJsonConfig;
    private @Getter BTeamManager teamManager;
    private @Getter Tablist tablist;

    @Override
    public void onEnable() {
        instance = this;

        RapidInvManager.register(this);
        NegativeSpaces.registerCodes();
        BukkitTCT.registerPlugin(this);

        // Obtain the secret connection string
        try {
            this.redisJsonConfig = new JsonConfig("secret.json");
        } catch (Exception e) {
            e.printStackTrace();
        }

        var redisUri = redisJsonConfig != null ? redisJsonConfig.getRedisUri() : null;
        // Hook the team , ensure no nulls
        teamManager = new BTeamManager(this, redisUri != null ? redisUri : "redis://147.182.135.68");

        game = new Game(this);
        game.runTaskTimerAsynchronously(this, 0L, 20L);

        // LISTENERS

        Bukkit.getPluginManager().registerEvents(new GlobalListener(this), this);

        // COMMANDS

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new GlobalCMD(this));
        //commandManager.registerCommand(new CinematicCMD(this));

        try {
            var jsonConfig = new JsonConfig("cinematics.json");
            var list = jsonConfig.getJsonObject();
            var iter = list.entrySet().iterator();
            var map = game.getCinematics();

            while (iter.hasNext()) {
                var entry = iter.next();
                var name = entry.getKey();
                var value = entry.getValue();
                var cinematic = gson.fromJson(value, Cinematic.class);
                map.put(name, cinematic);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskLater(this, task -> {
            WorldCreator worldCreator = new WorldCreator("lobby");
            worldCreator.environment(Environment.THE_END);
            worldCreator.createWorld();

        }, 20);
        // WIP Tablist
        tablist = new Tablist(this);
        tablist.onEnable();

    }

    @Override
    public void onDisable() {

        var list = game.getCinematics();

        try {
            var jsonConfig = new JsonConfig("cinematics.json");
            var json = gson.toJson(list);
            var obj = gson.fromJson(json, JsonObject.class);
            jsonConfig.setJsonObject(obj);
            jsonConfig.save();

        } catch (Exception e) {

            e.printStackTrace();
        }

        tablist.onDisable();

    }

    public void adminMessage(String text) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("admin.perm"))
                player.sendMessage(text);
        });
    }

    public Component componentToString(String str) {
        return miniMessage.parse(str);
    }

    public void broadcastMessage(String text) {
        Bukkit.broadcast(miniMessage.parse(text));
    }

    public void sendActionBar(Player player, String text) {
        player.sendActionBar(miniMessage.parse(text));
    }

    public void showTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(Title.title(miniMessage.parse(title), miniMessage.parse(subtitle), Times
                .of(Duration.ofMillis(50 * fadeIn), Duration.ofMillis(50 * stay), Duration.ofMillis(50 * fadeIn))));
    }

    public void sendHeader(Player player, String text) {
        player.sendPlayerListHeader(miniMessage.parse(text));
    }

    public void sendFooter(Player player, String text) {
        player.sendPlayerListFooter(miniMessage.parse(text));
    }

}