package me.colejedwards.glue.commands;

import com.google.common.base.Joiner;
import me.colejedwards.glue.Glue;
import me.colejedwards.glue.server.GlueServer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashSet;
import java.util.Set;

public class GListCommand extends Command {

    public GListCommand() {
        super("glist", "bungeecord.command.list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Glue.getInstance().getProxy().getScheduler().runAsync(Glue.getInstance(), () -> {
           if (args.length == 0) {
               sender.sendMessage("§cThere are currently " + Glue.getInstance().getProfiles().size() + " players on the network.");
               sender.sendMessage("§4To see all players, do /glist showall!");
           } else {
               for (ServerInfo serverInfo : Glue.getInstance().getProxy().getServers().values()) {
                   GlueServer glueServer = new GlueServer(serverInfo.getName());

                   Set<String> usernames = new HashSet<>();

                   glueServer.getOnline().forEach(profile -> usernames.add(profile.getUsername()));

                   sender.sendMessage("§a[" + serverInfo.getName() + "] &e" + glueServer.getOnline().size() + "&f: " + Joiner.on(',').join(usernames));
               }
               sender.sendMessage("§fTotal players online: " + Glue.getInstance().getProfiles().size());
           }
        });
    }
}
