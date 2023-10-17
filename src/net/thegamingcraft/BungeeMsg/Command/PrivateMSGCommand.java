package net.thegamingcraft.BungeeMsg.Command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thegamingcraft.BungeeMsg.BungeeMsg;

public class PrivateMSGCommand extends Command {

    public PrivateMSGCommand() {
        super("msg", "", "pm", "whisper", "m", "t", "message","tell");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.msg.usage"))));
        } else if (!(sender instanceof ProxiedPlayer)) {
            processProxyCommand(sender, args);
        } else {
            processPlayerCommand(sender, args);
        }
    }

    public void processPlayerCommand(CommandSender sender, String[] args) {
        if (args[0].equals(sender.getName())) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.not-to-you"))));
            return;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            str.append(args[i] + " ");
        }
        String message = str.toString();
        ProxiedPlayer playerTarget = BungeeMsg.instance.getProxy().getPlayer(args[0]);
        ProxiedPlayer playerSender = (ProxiedPlayer) sender;
        if (playerTarget != null) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.message-send").replace("<from>", sender.getName())
                            .replace("<target>", args[0]))
                    .replace("<msg>", message)));
            boolean doMessage = true;
            if (BungeeMsg.instance.mutedPlayerByPlayer.containsKey(playerSender.getUniqueId())) {
                if (BungeeMsg.instance.mutedPlayerByPlayer.get(playerSender.getUniqueId())
                        .contains(playerTarget.getUniqueId())) {
                    doMessage = false;
                }
            }
            if (doMessage) {
                playerTarget.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        BungeeMsg.instance.config.getString("Message.message-recieve")
                                .replace("<from>", sender.getName())
                                .replace("<target>", args[0]))
                        .replace("<msg>", message)));
                BungeeMsg.instance.messagers.put(playerTarget.getName(), sender.getName());
                BungeeMsg.instance.messagers.put(sender.getName(), playerTarget.getName());
                BungeeMsg.instance.logToConsole((ProxiedPlayer) sender, playerTarget, message);
            }else{
                sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', BungeeMsg.instance.config.getString("Message.r.blocked").replace("<player>", playerTarget.getDisplayName()))));
                BungeeMsg.instance.logToConsole((ProxiedPlayer) sender, playerTarget, "[BLOCKED] ".concat(message));
            }
        } else {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.unknown-player").replace("<player>", args[0]))));
        }
    }

    public void processProxyCommand(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("proxy")) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.not-to-you"))));
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                str.append(args[i] + " ");
            }
            String message = str.toString();
            ProxiedPlayer playerSender = (ProxiedPlayer) sender;
            ProxiedPlayer playerTarget = BungeeMsg.instance.getProxy().getPlayer(args[0]);
            if (playerTarget != null) {
                boolean doMessage = true;
                if (BungeeMsg.instance.mutedPlayerByPlayer.containsKey(playerTarget.getUniqueId())) {
                    if (BungeeMsg.instance.mutedPlayerByPlayer.get(playerSender.getUniqueId())
                            .contains(playerTarget.getUniqueId())) {
                        doMessage = false;
                    }
                }
                // Nachricht an Sender
                sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        BungeeMsg.instance.config.getString("Message.message-send")
                                .replace("<from>", sender.getName())
                                .replace("<target>", args[0]))
                        .replace("<msg>", message)));

                if (doMessage) {
                    // Nachricht an Ziel (kann geblockt werden)
                    playerTarget.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                            BungeeMsg.instance.config.getString("Message.message-recieve")
                                    .replace("<from>", sender.getName())
                                    .replace("<target>", args[0]))
                            .replace("<msg>", message)));
                    BungeeMsg.instance.messagers.put(playerTarget.getName(), "proxy");
                    BungeeMsg.instance.messagers.put("proxy", playerTarget.getName());
                }
            } else {
                sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        BungeeMsg.instance.config.getString("unknown-player").replace("<player>", args[0]))));
            }
        }
    }
}
