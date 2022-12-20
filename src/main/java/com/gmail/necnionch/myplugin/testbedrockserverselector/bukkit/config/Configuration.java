package com.gmail.necnionch.myplugin.testbedrockserverselector.bukkit.config;

import com.gmail.necnionch.myplugin.testbedrockserverselector.common.BukkitConfigDriver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Configuration extends BukkitConfigDriver {

    private String serverCommand;
    private boolean serverCommandConsole;
    private String nonBedrockCommandFallback;

    public Configuration(JavaPlugin plugin) {
        super(plugin, "config.yml", "config.yml");
    }

    @Override
    public boolean onLoaded(FileConfiguration config) {
        if (super.onLoaded(config)) {
            serverCommand = config.getString("server-command", "server {server}");
            serverCommandConsole = config.getBoolean("server-command-in-console", false);
            nonBedrockCommandFallback = config.getString("non-bedrock-fallback-command");
            return true;
        }
        return false;
    }

    @Nullable
    public String getServerCommand() {
        return serverCommand;
    }

    @NotNull
    public String formatServerCommand(String serverName, String playerName) {
        String command = Optional.ofNullable(serverCommand).orElse("server {server}");
        return command.replace("{server}", serverName).replace("{player}", playerName);
    }

    public boolean isServerCommandConsole() {
        return serverCommandConsole;
    }

    @NotNull
    public String getNonBedrockCommandFallback() {
        return Optional.ofNullable(nonBedrockCommandFallback).orElse("gui");
    }

}
