package net.thegamingcraft.BungeeMsg.Command;

import java.util.ArrayList;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thegamingcraft.BungeeMsg.BungeeMsg;

public class PrivateMuteCommand extends Command {

    public PrivateMuteCommand() {
        super("msgmute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // msgmute <spieler>
        if (args.length != 1) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.mute.usage"))));
            return;
        }
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer playerSender = (ProxiedPlayer) sender;
        ProxiedPlayer playerToMute = BungeeMsg.instance.getProxy().getPlayer(args[0]);

        if (playerToMute == null) {
            playerSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.unknown-player").replace("<player>", args[0]))));
            return;
        }

        if (BungeeMsg.instance.mutedPlayerByPlayer.containsKey(playerToMute.getUniqueId()) && BungeeMsg.instance.mutedPlayerByPlayer.get(playerToMute.getUniqueId()).contains(playerSender.getUniqueId())) {
            // unmute

            // playerToMute wieder aktivieren

            BungeeMsg.instance.mutedPlayerByPlayer.get(playerToMute.getUniqueId()).remove(playerSender.getUniqueId());
            playerSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.mute.player-unmuted").replace("<player>", args[0]))));

        } else {
            // mute
            if (!BungeeMsg.instance.mutedPlayerByPlayer.containsKey(playerToMute.getUniqueId())) {
                BungeeMsg.instance.mutedPlayerByPlayer.put(playerToMute.getUniqueId(), new ArrayList<>());
            }
            BungeeMsg.instance.mutedPlayerByPlayer.get(playerToMute.getUniqueId()).add(playerSender.getUniqueId());
            playerSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    BungeeMsg.instance.config.getString("Message.mute.player-muted").replace("<player>", args[0]))));
        }
    }

}
