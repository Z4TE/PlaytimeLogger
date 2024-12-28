package com.github.Z4TE.playtimeLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {

    private final HashMap<UUID, Long> playTimeMap = new HashMap<>();
    private final HashMap<UUID, Long> joinTimeMap = new HashMap<>();


    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (UUID playerId : playTimeMap.keySet()) {
            this.getConfig().set(playerId.toString(), playTimeMap.get(playerId) );
            saveConfig();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        joinTimeMap.put(playerId, System.currentTimeMillis());

        long totalPlaytime = this.getConfig().getLong(playerId.toString());

        String postfix = "(since 12/28)";
        String message = ChatColor.YELLOW + "Your total playtime on this server is " + formatPlayTime(totalPlaytime) + postfix;

        player.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (joinTimeMap.containsKey(playerId)) {
            long joinTime = joinTimeMap.get(playerId);
            long playTime = System.currentTimeMillis() - joinTime;
            playTimeMap.put(playerId, playTimeMap.getOrDefault(playerId, 0L) + playTime);
            joinTimeMap.remove(playerId);

            this.getConfig().set(playerId.toString(), playTimeMap.get(playerId) + playTime);
            saveConfig();
        }
    }

    private String formatPlayTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date resultDate = new Date(milliseconds);
        return sdf.format(resultDate);
    }
}
