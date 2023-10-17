package net.thegamingcraft.BungeeMsg.Command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thegamingcraft.BungeeMsg.BungeeMsg;

public class PrivateRCommand extends Command {

    public PrivateRCommand() {
        super("r");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.r.usage"))));
        } else if (!(sender instanceof ProxiedPlayer)) {
            processProxyCommand(sender, args);
        } else {
            processPlayerCommand(sender, args);
        }
    }

    private void processProxyCommand(CommandSender sender, String[] args) {
        if (!BungeeMsg.instance.messagers.containsKey("proxy")) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.r.no-reply"))));
            return;
        }
        ProxiedPlayer playerZiel = BungeeMsg.instance.getProxy()
                .getPlayer((String) BungeeMsg.instance.messagers.get("proxy"));
        ProxiedPlayer playerSender = (ProxiedPlayer) sender;
        if (playerZiel == null) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.r.player-offline"))));
            return;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            str.append(args[i] + " ");
        }
        String message = str.toString();

        boolean doMessage = true;
        if (BungeeMsg.instance.mutedPlayerByPlayer.containsKey(playerSender.getUniqueId())) {
            if (BungeeMsg.instance.mutedPlayerByPlayer.get(playerSender.getUniqueId())
                    .contains(playerZiel.getUniqueId())) {
                doMessage = false;
            }
        }

        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                BungeeMsg.instance.config.getString("Message.message-send").replace("<from>", sender.getName())
                        .replace("<target>", playerZiel.getName()))
                .replace("<msg>", message)));
        if (doMessage) {
            playerZiel.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.message-recieve").replace("<from>", sender.getName())
                            .replace("<target>", playerZiel.getName()))
                    .replace("<msg>", message)));
            BungeeMsg.instance.messagers.put(playerZiel.getName(), "proxy");
            BungeeMsg.instance.messagers.put("proxy", playerZiel.getName());
            BungeeMsg.instance.logToConsole((ProxiedPlayer) sender, playerZiel, message);
        } else {
            BungeeMsg.instance.logToConsole((ProxiedPlayer) sender, playerZiel, "[BLOCKED] ".concat(message));
        }
    }

    private void processPlayerCommand(CommandSender sender, String[] args) {
        if (!BungeeMsg.instance.messagers.containsKey(sender.getName())) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.no-reply"))));
            return;
        }
        if (BungeeMsg.instance.messagers.get(sender.getName()).toString().equalsIgnoreCase("proxy")) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i] + " ");
            }
            String message = str.toString();

            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.message-send").replace("<from>", sender.getName())
                            .replace("<target>", "proxy"))
                    .replace("<msg>", message)));
            BungeeMsg.log(ChatColor
                    .translateAlternateColorCodes('&',
                            BungeeMsg.instance.config.getString("Message.message-recieve")
                                    .replace("<from>", sender.getName()).replace("<target>", "proxy"))
                    .replace("<msg>", message));
            BungeeMsg.instance.messagers.put("proxy", sender.getName());
            BungeeMsg.instance.messagers.put(sender.getName(), "proxy");
            return;
        }

        ProxiedPlayer playerZiel = BungeeMsg.instance.getProxy().getPlayer((String) BungeeMsg.instance.messagers.get(sender.getName()));
        if (playerZiel == null || !playerZiel.isConnected()) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.player-offline"))));
            return;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            str.append(args[i] + " ");
        }
        String message = str.toString();
        ProxiedPlayer playerSender = (ProxiedPlayer)sender;

        boolean doMessage = true;
        if (BungeeMsg.instance.mutedPlayerByPlayer.containsKey(playerSender.getUniqueId())) {
            if (BungeeMsg.instance.mutedPlayerByPlayer.get(playerSender.getUniqueId())
                    .contains(playerZiel.getUniqueId())) {
                doMessage = false;
            }
        }
        //Nachricht nur schicken wenn Ziel nicht geblockt ist von mir...
        if(doMessage){
            sender.sendMessage(
                    new TextComponent(
                            ChatColor
                                    .translateAlternateColorCodes('&',
                                            BungeeMsg.instance.config.getString("Message.message-send")
                                                    .replace("<from>", sender.getName())
                                                    .replace("<target>", playerZiel.getName()))
                                    .replace("<msg>", message)));
            playerZiel.sendMessage(
                    new TextComponent(
                            ChatColor
                                    .translateAlternateColorCodes('&',
                                            BungeeMsg.instance.config.getString("Message.message-recieve")
                                                    .replace("<from>", sender.getName())
                                                    .replace("<target>", playerZiel.getName()))
                                    .replace("<msg>", message)));
            BungeeMsg.instance.messagers.put(playerZiel.getName(), sender.getName());
            BungeeMsg.instance.messagers.put(sender.getName(), playerZiel.getName());
        
            BungeeMsg.instance.logToConsole((ProxiedPlayer) sender, playerZiel, message);
        }else{
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', BungeeMsg.instance.config.getString("Message.r.blocked").replace("<player>", playerZiel.getDisplayName()))));
            BungeeMsg.instance.logToConsole((ProxiedPlayer) sender, playerZiel, "[Blocked] ".concat(message));
        }
    }
}
