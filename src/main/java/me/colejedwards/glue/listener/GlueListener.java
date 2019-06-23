package me.colejedwards.glue.listener;

import com.google.common.base.Joiner;
import me.colejedwards.glue.Glue;
import me.colejedwards.glue.profile.GlueProfile;
import me.colejedwards.glue.server.GlueServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GlueListener implements Listener {

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        Glue.getInstance().getProxy().getScheduler().schedule(Glue.getInstance(), () -> Glue.getInstance().getProfiles().add(new GlueProfile(p.getUniqueId(), p.getServer().getInfo().getName(), Glue.getInstance().getConfig().getConfiguration().getString("server.proxy"))), 0, 500, TimeUnit.MILLISECONDS);

        if (p.hasPermission("glue.important")) {
            if (!Glue.getInstance().getImportant().contains(p.getUniqueId())) {
                Glue.getInstance().getImportant().add(p.getUniqueId());
            }
        } else {
            if (Glue.getInstance().getImportant().contains(p.getUniqueId())) {
                Glue.getInstance().getImportant().remove(p.getUniqueId());
            }
        }

        if ((boolean) Glue.getInstance().getProxyData().get("maintenance") && !Glue.getInstance().getImportant().contains(p.getUniqueId())) {
            e.getPlayer().disconnect("§cThe network is currently under maintenance.\nFor more information, follow our twitter @CavePVPcom");
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        Glue.getInstance().getProfiles().remove(GlueProfile.getGlueProfileFromUUID(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        GlueProfile.getGlueProfileFromUUID(e.getPlayer().getUniqueId()).setServer(e.getPlayer().getServer().getInfo().getName());
    }

    @EventHandler
    public void onProxyPingEvent(ProxyPingEvent e) {
        ServerPing serverPing = e.getResponse();

        serverPing.getPlayers().setOnline(Glue.getInstance().getProfiles().size());

        if ((boolean)Glue.getInstance().getProxyData().get("maintenance")) {
            serverPing.getVersion().setProtocol(1);
            serverPing.getVersion().setName("§4Maintenance");
        }
    }

    @EventHandler
    public void onMessageReceived(PluginMessageEvent e) {
        String channel = e.getTag();
        byte[] data = e.getData();

        if (channel.equalsIgnoreCase("BungeeCord") && e.getSender() instanceof Server) {
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));
            Glue.getInstance().getProxy().getScheduler().runAsync(Glue.getInstance(), () -> {
                try {
                    String sub = dataInputStream.readUTF();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    switch (sub) {
                        case "PlayerCount":
                            String server = dataInputStream.readUTF();

                            if (server.equalsIgnoreCase("PROXIES")) {
                                out.writeUTF("PlayerCount");
                                out.writeUTF("PROXIES");
                                out.writeInt(Glue.getInstance().getProfiles().size());
                                ((Server) e.getSender()).sendData("BungeeCord", stream.toByteArray());
                            } else {
                                out.writeUTF("PlayerCount");
                                out.writeUTF(server);
                                out.writeInt(new GlueServer(server).getOnline().size());
                            }
                        case "PlayerList":
                            String server1 = dataInputStream.readUTF();

                            if (server1.equalsIgnoreCase("PROXIES")) {
                                out.writeUTF("PlayerList");
                                out.writeUTF("PROXIES");

                                Set<String> usernames = new HashSet<>();

                                Glue.getInstance().getProfiles().forEach(profile -> usernames.add(profile.getUsername()));

                                out.writeUTF(Joiner.on(',').join(usernames));
                                ((Server) e.getSender()).sendData("BungeeCord", stream.toByteArray());
                            } else {
                                out.writeUTF("PlayerList");
                                out.writeUTF(server1);

                                Set<String> usernames = new HashSet<>();

                                GlueServer glueServer = new GlueServer(server1);

                                glueServer.getOnline().forEach(profile -> usernames.add(profile.getUsername()));

                                out.writeUTF(Joiner.on(',').join(usernames));
                                ((Server) e.getSender()).sendData("BungeeCord", stream.toByteArray());
                            }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        }
    }
}
