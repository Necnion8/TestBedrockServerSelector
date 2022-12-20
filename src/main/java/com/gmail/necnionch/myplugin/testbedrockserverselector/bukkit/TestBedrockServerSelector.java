package com.gmail.necnionch.myplugin.testbedrockserverselector.bukkit;

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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class TestBedrockServerSelector extends JavaPlugin {
    private final ServerConfiguration config = new ServerConfiguration(this);

    @Override
    public void onEnable() {
        config.load();
        Optional.ofNullable(getCommand("begui")).ifPresent(cmd -> cmd.setExecutor(this::execute));
    }

    public List<ServerConfiguration.Entry> getServers() {
        return config.getEntries();
    }


    private boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        if (!openSelector(((Player) sender))) {
            Bukkit.dispatchCommand(sender, "gui");  // fallback
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
                if (!Bukkit.dispatchCommand(player, "server " + server.getName()))
                    player.sendMessage(ChatColor.RED + "移動できませんでした");

            } catch (CommandException e) {
                player.sendMessage(ChatColor.RED + "内部エラーが発生しました");
                e.printStackTrace();
            }
        });
        return b.build();
    }

}
