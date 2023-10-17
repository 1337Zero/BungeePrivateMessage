package net.thegamingcraft.BungeeMsg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.luckperms.api.LuckPerms;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.thegamingcraft.BungeeMsg.Command.PrivateListCommand;
import net.thegamingcraft.BungeeMsg.Command.PrivateMSGCommand;
import net.thegamingcraft.BungeeMsg.Command.PrivateMuteCommand;
import net.thegamingcraft.BungeeMsg.Command.PrivateRCommand;
import net.thegamingcraft.BungeeMsg.Command.PrivateSocialSpyCommand;

@SuppressWarnings("all")
public class BungeeMsg extends Plugin implements Listener {

	public static BungeeMsg instance;
	public Configuration config;

	public Map<String, String> messagers = new HashMap();
	public ArrayList<String> spylist = new ArrayList<String>();
	public HashMap<UUID, List<UUID>> mutedPlayerByPlayer = new HashMap<>();

	public static void log(String message) {
		instance.getLogger().info("[".concat(BungeeMsg.instance.getDescription().getName()).concat(" v. ")
				.concat(BungeeMsg.instance.getDescription().getVersion()).concat("] ").concat(message));
	}

	public void onEnable() {
		BungeeMsg.instance = this;
		loadConfig();
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getPluginManager().registerCommand(this, new PrivateMSGCommand());
		getProxy().getPluginManager().registerCommand(this, new PrivateRCommand());
		getProxy().getPluginManager().registerCommand(this, new PrivateSocialSpyCommand());
		getProxy().getPluginManager().registerCommand(this, new PrivateMuteCommand());
		if (Boolean.valueOf(config.getBoolean("Settings.enable-listcmd"))) {
			getProxy().getPluginManager().registerCommand(this, new PrivateListCommand());
		}
	}

	public void logToConsole(ProxiedPlayer player, ProxiedPlayer target, String message) {
		if (Boolean.valueOf(config.getBoolean("Settings.socialspy-cmd-output"))) {
			log(
					ChatColor.translateAlternateColorCodes('&', config.getString("Message.socialspy.socialspy-format")
							.replace("<from>", player.getName()).replace("<target>", target.getName()))
							.replace("<msg>", message));
		}

		for (int i = 0; i < spylist.size(); i++) {
			ProxiedPlayer spyplayer = ProxyServer.getInstance().getPlayer(spylist.get(i));
			if (spyplayer != null) {
				spyplayer.sendMessage(
						ChatColor.translateAlternateColorCodes('&',
								config.getString("Message.socialspy.socialspy-format").replace("<from>", player.getName())
										.replace("<target>", target.getName()))
								.replace("<msg>", message));
			} else {
				spylist.remove(i);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onTab(TabCompleteEvent event) {
		if (!event.getSuggestions().isEmpty()) {
			return;
		}
		String[] args = event.getCursor().split(" ");

		final String checked = (args.length > 0 ? args[args.length - 1] : event.getCursor()).toLowerCase();
		for (ProxiedPlayer player : this.getProxy().getPlayers()) {
			if (player.getName().toLowerCase().startsWith(checked)) {
				event.getSuggestions().add(player.getName());
			}
		}
	}

	public void loadConfig() {
		try {
			// Create plugin config folder if it doesn't exist
			if (!getDataFolder().exists()) {
				log("Created config folder: " + getDataFolder().mkdir());
			}
			File configFile = new File(getDataFolder(), "config.yml");
			if (!configFile.exists()) {
				FileOutputStream outputStream = new FileOutputStream(configFile);
				InputStream in = getResourceAsStream("config.yml");
				in.transferTo(outputStream);
			}
			config = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(getDataFolder(), "config.yml"));
		} catch (IOException ex) {
			log(null);
		}

	}

}