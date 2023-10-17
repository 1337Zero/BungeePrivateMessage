package net.thegamingcraft.BungeeMsg.Command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thegamingcraft.BungeeMsg.BungeeMsg;

public class PrivateSocialSpyCommand extends Command {

    public PrivateSocialSpyCommand() {
        super("socialspy", "bungeemsg.socialspy", "sp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("bungeemsg.socialspy")) {
            if (args.length > 0) {
                sender.sendMessage(
                        new TextComponent(ChatColor.translateAlternateColorCodes('&', BungeeMsg.instance.config.getString("Message.socialspy.usage"))));
            } else if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        BungeeMsg.instance.config.getString("Message.proxy-deny-msg"))));
                return;
            } else {
                if (!BungeeMsg.instance.spylist.contains(((ProxiedPlayer) sender).getName())) {
                    sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                            BungeeMsg.instance.config.getString("Message.socialspy.socialspy-on"))));
                    BungeeMsg.instance.spylist.add(((ProxiedPlayer) sender).getName());
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                            BungeeMsg.instance.config.getString("Message.socialspy.socialspy-off"))));
                    BungeeMsg.instance.spylist.remove(((ProxiedPlayer) sender).getName());
                }
            }
        } else {
            sender.sendMessage(
                    new TextComponent(ChatColor.translateAlternateColorCodes('&', "&4You don't have permissions!")));
        }
    }

}
