package com.gmail.necnionch.myplugin.testbedrockserverselector.bukkit.config;

import com.gmail.necnionch.myplugin.testbedrockserverselector.common.BukkitConfigDriver;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServerConfiguration extends BukkitConfigDriver {
    private final List<Entry> entries = Lists.newArrayList();

    public ServerConfiguration(JavaPlugin plugin) {
        super(plugin, "servers.yml", "servers.yml");
    }

    @Override
    public boolean onLoaded(FileConfiguration config) {
        entries.clear();
        if (super.onLoaded(config)) {
            entries.addAll(config.getKeys(false).stream().map(name -> {
                String displayName = Optional.ofNullable(config.getString(name + ".display")).orElse(name);
                displayName = ChatColor.translateAlternateColorCodes('&', displayName);
                String imageUrl = config.getString(name + ".image_url");
                int index = config.getInt(name + ".index");
                return new Entry(name, displayName, imageUrl, index);
            })
                    .sorted(Comparator.comparingInt(Entry::getIndex))
                    .collect(Collectors.toList()));
            return true;
        }
        return false;
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }


    public static final class Entry {

        private final String name;
        private final String displayName;
        private @Nullable
        final String imageUrl;
        private final int index;

        public Entry(String name, String displayName, @Nullable String imageUrl, int index) {
            this.name = name;
            this.displayName = displayName;
            this.imageUrl = imageUrl;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Nullable
        public String getImageUrl() {
            return imageUrl;
        }

        public int getIndex() {
            return index;
        }
    }
}
