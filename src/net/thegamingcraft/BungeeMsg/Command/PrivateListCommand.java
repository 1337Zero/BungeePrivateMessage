package net.thegamingcraft.BungeeMsg.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.chat.hover.content.Text;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thegamingcraft.BungeeMsg.BungeeMsg;

public class PrivateListCommand extends Command {

    public PrivateListCommand() {
        super("list", "", "who", "online");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', BungeeMsg.instance.config.getString("Message.list.usage"))));
        } else {
            Collection<ProxiedPlayer> onlineplayer = ProxyServer.getInstance().getPlayers();
            
            LuckPerms api = LuckPermsProvider.get();
            if(api != null){
                HashMap<String,List<String>> groupToPlayer = new HashMap<>();
                for(ProxiedPlayer player : onlineplayer){
                    //User luckPermsPlayer = api.getPlayerAdapter(ProxiedPlayer.class).getUser(player);
                    CachedMetaData metaData = api.getPlayerAdapter(ProxiedPlayer.class).getMetaData(player);
                    
                    if(!groupToPlayer.containsKey(metaData.getPrefix())){
                        groupToPlayer.put(metaData.getPrefix(), new ArrayList<>());
                    }
                    if(metaData.getPrefix() == null){
                        groupToPlayer.get("Member").add(player.getDisplayName());
                    }else{
                        groupToPlayer.get(metaData.getPrefix()).add(player.getDisplayName());
                    }                    
                }
                generateClickAblePlayerList(groupToPlayer,sender,onlineplayer.size());
            }else{
                sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',BungeeMsg.instance.config.getString("Message.list.syntax"))));
                sender.sendMessage(new TextComponent(onlineplayer.toString().replace("[", "").replace("]", "")));
            }
        }
    }

    /**
     * Sends the sender a clickable list of Players including their Groups
     * On click and hover the player gets the /tell player command suggested 
     * @param groupToPlayer (HashMap<String,List<String>> Hashmap of group to List of players)
     * @param sender        (The sender who should get the list)
     */
    private void generateClickAblePlayerList(HashMap<String,List<String>> groupToPlayer,CommandSender sender,int onlinePlayersAmount){
        TextComponent base = new TextComponent(ChatColor.translateAlternateColorCodes('&',BungeeMsg.instance.config.getString("Message.list.syntax").replace("<playerAmount>", onlinePlayersAmount+"")));
        sender.sendMessage(base);
        for(String group : groupToPlayer.keySet()){
            TextComponent groupComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', group + ": "));
            for(int i = 0; i < groupToPlayer.get(group).size();i++){
                String player = groupToPlayer.get(group).get(i);
                TextComponent playerComponent = new TextComponent(player);
                if((i+1) < groupToPlayer.get(group).size()){
                    TextComponent spacerComponent = new TextComponent(", ");
                    playerComponent.addExtra(spacerComponent);
                }
                playerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + player));
                playerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/tell " + player)));
                groupComponent.addExtra(playerComponent);
            }
            sender.sendMessage(groupComponent);
        }   
        
    }
}
