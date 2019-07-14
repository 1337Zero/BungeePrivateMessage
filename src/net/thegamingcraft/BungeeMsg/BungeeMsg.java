package net.thegamingcraft.BungeeMsg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.thegamingcraft.BungeeMsg.utils.ChatManager;
import net.thegamingcraft.BungeeMsg.utils.FileConfig;

@SuppressWarnings("all")
public class BungeeMsg extends Plugin implements Listener {
	private Map<String, String> messagers = new HashMap();
	private ArrayList<String> spylist = new ArrayList<String>();
	private FileConfig config;
	private ChatManager ch = new ChatManager();
		
	public void onEnable() {	
		config = new FileConfig();
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getPluginManager().registerCommand(this, new Command("msg","","tell","pm","whisper","m","t","message") {
		
			public void execute(CommandSender sender, String[] args) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED+ "Usage: /msg <player> <message>");
				} else if (args.length == 1) {
					sender.sendMessage(ChatColor.RED+ "Usage: /msg <player> <message>");
				} else if (!(sender instanceof ProxiedPlayer)) {
					if (args[0].equalsIgnoreCase("proxy")) {
						sender.sendMessage(ch.ColorIt(config.getData("Message.not-to-you")));
			//sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cannot send message to yourself");
					} else {						
						StringBuilder str = new StringBuilder();
						for (int i = 1; i < args.length; i++) {
							str.append(args[i] + " ");
						}
						String nmessage = str.toString();
						String message = nmessage.replace("&", "§");
						ProxiedPlayer p1 = BungeeMsg.this.getProxy().getPlayer(args[0]);
						if (p1 != null) {						
							sender.sendMessage(ch.ColorIt(config.getData("Message.message-send").replace("<from>", sender.getName()).replace("<target>", args[0])).replace("<msg>", message));
							p1.sendMessage(ch.ColorIt(config.getData("Message.message-recieve").replace("<from>", sender.getName()).replace("<target>", args[0])).replace("<msg>", message));
							BungeeMsg.this.messagers.put(p1.getName(), "proxy");
							BungeeMsg.this.messagers.put("proxy", p1.getName());
						} else {
							sender.sendMessage(ch.ColorIt(config.getData("unknown-player").replace("<player>", args[0])));
						}
					}
				} else if (args[0].equals(sender.getName())) {
					sender.sendMessage(ch.ColorIt(config.getData("Message.not-to-you")));
				} else {
					StringBuilder str = new StringBuilder();
					for (int i = 1; i < args.length; i++) {
						str.append(args[i] + " ");
					}
					String nmessage = str.toString();
					String message = nmessage.replace("&", "§");
					ProxiedPlayer p1 = BungeeMsg.this.getProxy().getPlayer(args[0]);
					if (p1 != null) {
						sender.sendMessage(ch.ColorIt(config.getData("Message.message-send").replace("<from>", sender.getName()).replace("<target>", args[0])).replace("<msg>", message));
						p1.sendMessage(ch.ColorIt(config.getData("Message.message-recieve").replace("<from>", sender.getName()).replace("<target>", args[0])).replace("<msg>", message));
						BungeeMsg.this.messagers.put(p1.getName(),sender.getName());
						BungeeMsg.this.messagers.put(sender.getName(),p1.getName());
						BungeeMsg.this.logToConsole((ProxiedPlayer) sender, p1,message);
					} else {
						sender.sendMessage(ch.ColorIt(config.getData("unknown-player").replace("<player>", args[0])));
					}
				}
			}
		});
		getProxy().getPluginManager().registerCommand(this, new Command("r") {
			public void execute(CommandSender sender, String[] args) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /r <message>");
				} else if (!(sender instanceof ProxiedPlayer)) {
					if (!BungeeMsg.this.messagers.containsKey("proxy")) {
						sender.sendMessage(ch.ColorIt(config.getData("Message.no-reply")));
						return;
					}
					ProxiedPlayer target = BungeeMsg.this.getProxy().getPlayer((String) BungeeMsg.this.messagers.get("proxy"));
					if (target == null) {
						sender.sendMessage(ch.ColorIt(config.getData("Message.player-offline")));
						return;
					}
					StringBuilder str = new StringBuilder();
					for (int i = 0; i < args.length; i++) {
						str.append(args[i] + " ");
					}
					String nmessage = str.toString();
					String message = nmessage.replace("&", "§");

					sender.sendMessage(ch.ColorIt(config.getData("Message.message-send").replace("<from>", sender.getName()).replace("<target>", target.getName())).replace("<msg>", message));
					target.sendMessage(ch.ColorIt(config.getData("Message.message-recieve").replace("<from>", sender.getName()).replace("<target>", target.getName())).replace("<msg>", message));
					BungeeMsg.this.messagers.put(target.getName(), "proxy");
					BungeeMsg.this.messagers.put("proxy", target.getName());
					BungeeMsg.this.logToConsole((ProxiedPlayer) sender, target,message);
				} else {
					if (!BungeeMsg.this.messagers.containsKey(sender.getName())) {
						sender.sendMessage(ch.ColorIt(config.getData("Message.no-reply")));
						return;
					}
					if (((String) BungeeMsg.this.messagers.get(sender.getName())).equalsIgnoreCase("proxy")) {
						StringBuilder str = new StringBuilder();
						for (int i = 0; i < args.length; i++) {
							str.append(args[i] + " ");
						}
						String nmessage = str.toString();
						String message = nmessage.replace("&", "§");	
						sender.sendMessage(ch.ColorIt(config.getData("Message.message-send").replace("<from>", sender.getName()).replace("<target>", "proxy")).replace("<msg>", message));
						BungeeMsg.this.getProxy().getLogger().log(Level.INFO,ch.ColorIt(config.getData("Message.message-recieve").replace("<from>", sender.getName()).replace("<target>", "proxy")).replace("<msg>", message));
						BungeeMsg.this.messagers.put("proxy", sender.getName());
						BungeeMsg.this.messagers.put(sender.getName(), "proxy");
						return;
					}

					ProxiedPlayer target = BungeeMsg.this.getProxy().getPlayer((String) BungeeMsg.this.messagers.get(sender.getName()));
					if (target == null) {
						sender.sendMessage(ch.ColorIt(config.getData("Message.player-offline")));
					} else {
						StringBuilder str = new StringBuilder();
						for (int i = 0; i < args.length; i++) {
							str.append(args[i] + " ");
						}
						String nmessage = str.toString();
						String message = nmessage.replace("&", "§");

						sender.sendMessage(ch.ColorIt(config.getData("Message.message-send").replace("<from>", sender.getName()).replace("<target>", target.getName())).replace("<msg>", message));
						target.sendMessage(ch.ColorIt(config.getData("Message.message-recieve").replace("<from>", sender.getName()).replace("<target>", target.getName())).replace("<msg>", message));
						BungeeMsg.this.messagers.put(target.getName(),sender.getName());
						BungeeMsg.this.messagers.put(sender.getName(),target.getName());
						BungeeMsg.this.logToConsole((ProxiedPlayer) sender,target, message);
					}
				}
			}
		});
		
		
		/**
		 * @author 1337Zero
		 * Initalising of the Command /socialspy
		 */
		getProxy().getPluginManager().registerCommand(this, new Command("socialspy") {
			public void execute(CommandSender sender, String[] args) {				
				if(sender.hasPermission("bungeemsg.socialspy")){
					if (args.length > 0) {
						sender.sendMessage(ChatColor.RED + "Usage: /socialspy");
					} else if (!(sender instanceof ProxiedPlayer)) {					
						sender.sendMessage(ch.ColorIt(config.getData("Message.proxy-deny-msg")));
						return;
					} else {
						if(!spylist.contains(((ProxiedPlayer)sender).getName())){
							sender.sendMessage(ch.ColorIt(config.getData("Message.socialspy-on")));
							spylist.add(((ProxiedPlayer)sender).getName());
						}else{
							sender.sendMessage(ch.ColorIt(config.getData("Message.socialspy-off")));
							spylist.remove(((ProxiedPlayer)sender).getName());
						}
					}
				}else{
					sender.sendMessage(ChatColor.RED + " You don't have permissions!");
				}
			}
		});	
		if(Boolean.valueOf(config.getData("Settings.enable-listcmd"))){
			getProxy().getPluginManager().registerCommand(this, new Command("list","","who","online") {
				public void execute(CommandSender sender, String[] args) {
					if (args.length > 0) {
						sender.sendMessage(ChatColor.RED + "Usage: /list");
					}else {
						Collection<ProxiedPlayer> onlineplayer = ProxyServer.getInstance().getPlayers();
						
						sender.sendMessage(ch.ColorIt("&6Es sind &4" + onlineplayer.size() + "&6 Spieler online."));
						sender.sendMessage(onlineplayer.toString().replace("[", "").replace("]", ""));
					}				
				}
			});	
		}
		
		
	}
	public void logToConsole(ProxiedPlayer player, ProxiedPlayer target,String message) {
		if(Boolean.valueOf(config.getData("Settings.socialspy-cmd-output"))){
			getProxy().getLogger().log(Level.INFO,ch.ColorIt(config.getData("Message.socialspy-format").replace("<from>", player.getName()).replace("<target>", target.getName())).replace("<msg>", message));
		}
		
		for(int i = 0; i < spylist.size();i++){
			ProxiedPlayer spyplayer = ProxyServer.getInstance().getPlayer(spylist.get(i));
			if(spyplayer != null){
				spyplayer.sendMessage(ch.ColorIt(config.getData("Message.socialspy-format").replace("<from>", player.getName()).replace("<target>", target.getName())).replace("<msg>", message));
			}else{
				spylist.remove(i);
			}
		}
	}
	
	 /**
	  * @author 1337Zero
	  * {@link http://www.spigotmc.org/threads/tabber-tab-complete-playernames-cross-server.21826/} original code by maciekmm
	  * @param TabCompleteEvent e
	  */
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

	
}