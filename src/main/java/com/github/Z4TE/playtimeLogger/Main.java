package com.github.Z4TE.playtimeLogger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {

    private Map<UUID, Long> joinTimeMap;
    private Map<UUID, Long> totalPlayTimeMap;
    private final File data = new File(getDataFolder(), "playtime.dat");

    @Override
    public void onEnable() {
        // Plugin startup logic
        joinTimeMap = new HashMap<>();
        totalPlayTimeMap = new HashMap<>();

        loadPlaytime();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        savePlaytime();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        joinTimeMap.put(playerId, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        Long joinTime = joinTimeMap.get(playerId);

        if (joinTime != null) {
            long playtime = System.currentTimeMillis() - joinTime;
            totalPlayTimeMap.put(playerId, totalPlayTimeMap.getOrDefault(playerId, 0L) + playtime);
            joinTimeMap.remove(playerId);
        }
    }

    private void loadPlaytime() {
        if (!data.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(data))) {
            totalPlayTimeMap = (Map<UUID, Long>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            getLogger().severe("Failed to load playtime: " + e.getMessage());
        }
    }

    private void savePlaytime(){
        getDataFolder().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(data))) {
            oos.writeObject(totalPlayTimeMap);
        } catch (IOException e) {
            getLogger().severe("Failed to save playtime: " + e.getMessage());
        }
    }
}
