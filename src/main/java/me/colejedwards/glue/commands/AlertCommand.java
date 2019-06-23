package me.colejedwards.glue.commands;

import me.colejedwards.glue.Glue;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class AlertCommand extends Command {

    public AlertCommand() {
        super("alert", "glue.important");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("idiot wrong usage!");
        } else {
            String strings = StringUtils.join(args, ' ', 0, args.length);

            Glue.getInstance().getAlertPub().publish("&8[&4Alert&8] &f" + strings);
        }
    }

}
