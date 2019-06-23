package me.colejedwards.glue;

import lombok.Getter;
import me.colejedwards.glue.commands.AlertCommand;
import me.colejedwards.glue.commands.GListCommand;
import me.colejedwards.glue.commands.MaintenanceCommand;
import me.colejedwards.glue.config.Config;
import me.colejedwards.glue.listener.GlueListener;
import me.colejedwards.glue.profile.GlueProfile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.UUID;

@Getter
public final class Glue extends Plugin {

    @Getter private static Glue instance;

    private RedissonClient redissonClient;

    private Config config;

    private RSet<GlueProfile> profiles;

    private RSet<UUID> important;

    private RMap<String, Object> proxyData;

    private RTopic<String> alertPub;

    @Override
    public void onEnable() {
        instance = this;

        config = new Config(this, "config", getDataFolder().getAbsolutePath());

        setupRedis();
        setupProxyData();

        getProxy().getPluginManager().registerListener(this, new GlueListener());
        getProxy().getPluginManager().registerCommand(this, new GListCommand());
        getProxy().getPluginManager().registerCommand(this, new AlertCommand());
        getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand());
    }

    public void setupRedis() {
        org.redisson.config.Config config = new org.redisson.config.Config();

        config.useSingleServer()
                .setAddress(getConfig().getConfiguration().getString("redis.host") + ":" + getConfig().getConfiguration().getInt("redis.port"))
                .setPassword(getConfig().getConfiguration().getString("redis.password"));

        redissonClient = Redisson.create(config);

        profiles = getRedissonClient().getSet("GLUE:profiles");
        proxyData = getRedissonClient().getMap("GLUE:proxyData");
        important = getRedissonClient().getSet("GLUE:important");
        alertPub = getRedissonClient().getTopic("GLUE:alertPubSub");

        alertPub.addListener((channel, msg) -> {
            for (ProxiedPlayer proxiedPlayer : getProxy().getPlayers()) {
                proxiedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        });
    }

    public void setupProxyData() {
        if (getProxyData().get("maintenance") == null) {
            getProxyData().put("maintenance", false);
        }
    }

}
