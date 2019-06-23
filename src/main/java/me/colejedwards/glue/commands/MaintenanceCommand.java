package me.colejedwards.glue.commands;

import me.colejedwards.glue.Glue;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCommand extends Command {

    public MaintenanceCommand() {
        super("maintenance", "glue.important");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ((boolean)Glue.getInstance().getProxyData().get("maintenance")) {
            Glue.getInstance().getProxyData().put("maintenance", false);
            sender.sendMessage("toggled off.");
        } else {
            Glue.getInstance().getProxyData().put("maintenance", true);
            sender.sendMessage("toggled on.");
        }
    }
}
