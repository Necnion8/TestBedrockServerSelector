package com.gmail.necnionch.myplugin.testbedrockserverselector.bukkit;

import com.gmail.necnionch.myplugin.testbedrockserverselector.bukkit.config.Configuration;
import com.gmail.necnionch.myplugin.testbedrockserverselector.bukkit.config.ServerConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public final class TestBedrockServerSelector extends JavaPlugin {
    private final ServerConfiguration serverConfig = new ServerConfiguration(this);
    private final Configuration config = new Configuration(this);

    @Override
    public void onEnable() {
        config.load();
        serverConfig.load();
        Optional.ofNullable(getCommand("begui")).ifPresent(cmd -> cmd.setExecutor(this::execute));
    }

    @NotNull
    public Configuration getMainConfig() {
        return config;
    }

    public ServerConfiguration getServerConfig() {
        return serverConfig;
    }

    public List<ServerConfiguration.Entry> getServers() {
        return serverConfig.getEntries();
    }


    private boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        if (!openSelector(((Player) sender))) {
            Bukkit.dispatchCommand(sender, config.getNonBedrockCommandFallback());
        }
        return true;
    }

    public boolean openSelector(Player player) {
        FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        if (fPlayer == null)
            return false;

        fPlayer.sendForm(createSelector(player));
        return true;
    }

    public SimpleForm createSelector(Player player) {
        SimpleForm.Builder b = SimpleForm.builder()
                .title("サーバー一覧 " + ChatColor.DARK_GRAY + "(試験的)")
                .content("移動したいサーバーを選んでください");

        List<ServerConfiguration.Entry> servers = getServers();
        servers.forEach(s -> {
            if (s.getImageUrl() != null) {
                b.button(s.getDisplayName(), FormImage.Type.URL, s.getImageUrl());
            } else {
                b.button(s.getDisplayName());
            }
        });

        b.validResultHandler(response -> {
            ServerConfiguration.Entry server = servers.get(response.clickedButtonId());
            try {
                if (!executeServerCommand(player, server.getName()))
                    player.sendMessage(ChatColor.RED + "移動できませんでした");

            } catch (CommandException e) {
                player.sendMessage(ChatColor.RED + "内部エラーが発生しました");
                e.printStackTrace();
            }
        });
        return b.build();
    }

    public boolean executeServerCommand(Player player, String serverName) throws CommandException {
        String command = config.formatServerCommand(serverName, player.getName());
        CommandSender sender = config.isServerCommandConsole() ? Bukkit.getConsoleSender() : player;
        return Bukkit.dispatchCommand(sender, command);
    }

}
