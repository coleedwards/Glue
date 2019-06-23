package me.colejedwards.glue.config;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

@Getter @Setter
public class Config {

    /* File */
    private File file;
    /* Strings */
    private String name, directory;
    /* Configuration */
    private Configuration configuration;

    /**
     * Bungee Configuration Class
     *
     * @param name      - Is the identifier for the configuration file and object.
     * @param directory - Directory.
     */
    public Config(Plugin plugin, String name, String directory) {
        /* Set the Name */
        setName(name);
        /* Set the Directory */
        setDirectory(directory);
        /* Set File */
        file = new File(directory, name + ".yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        /* If file does not already exist, then grab it internally from the resources folder */
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (
                        InputStream is = plugin.getResourceAsStream(name + ".yml");
                        OutputStream os = new FileOutputStream(file)
                ) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }

        }
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        save();
    }


    /**
     * Saves the configuration file from memory to storage
     */
    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
