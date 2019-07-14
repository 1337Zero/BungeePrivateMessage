package net.thegamingcraft.BungeeMsg.utils;

import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author 1337Zero
 *
 */
public class ChatManager
{
  public String ReplaceIt(String Message, String replacethis, String replacewith)
  {
    return Message.replace(replacethis, replacewith);
  }

  public String ColorIt(String Message)
  {
    while (((Message.contains("&0") | Message.contains("&1") | Message.contains("&2") | Message.contains("&3") | Message.contains("&4") | Message.contains("&5") | Message.contains("&6") | Message.contains("&7"))) || 
      (Message.contains("&8")) || (Message.contains("&9")) || (Message.contains("&a")) || (Message.contains("&b")) || (Message.contains("&c")) || (Message.contains("&d")) || (Message.contains("&e")) || (Message.contains("&f")) || 
      (Message.contains("&g")) || (Message.contains("&h")) || (Message.contains("&i")) || (Message.contains("&j")) || (Message.contains("&k")) || (Message.contains("&r")))
    {
      if (Message.contains("&r"))
      {
        Message = Message.replace("&r", ChatColor.RESET + "");
      }
      else if (Message.contains("&g"))
      {
        Message = Message.replace("&g", ChatColor.MAGIC + "");
      }
      else if (Message.contains("&h"))
      {
        Message = Message.replace("&h", ChatColor.BOLD + "");
      }
      else if (Message.contains("&i"))
      {
        Message = Message.replace("&i", ChatColor.ITALIC + "");
      }
      else if (Message.contains("&j"))
      {
        Message = Message.replace("&j", ChatColor.UNDERLINE + "");
      }
      else if (Message.contains("&k"))
      {
        Message = Message.replace("&k", ChatColor.STRIKETHROUGH + "");
      }
      else if (Message.contains("&f"))
      {
        Message = Message.replace("&f", ChatColor.WHITE + "");
      }
      else if (Message.contains("&e"))
      {
        Message = Message.replace("&e", ChatColor.YELLOW + "");
      }
      else if (Message.contains("&d"))
      {
        Message = Message.replace("&d", ChatColor.LIGHT_PURPLE + "");
      }
      else if (Message.contains("&c"))
      {
        Message = Message.replace("&c", ChatColor.RED + "");
      }
      else if (Message.contains("&b"))
      {
        Message = Message.replace("&b", ChatColor.AQUA + "");
      }
      else if (Message.contains("&a"))
      {
        Message = Message.replace("&a", ChatColor.GREEN + "");
      }
      else if (Message.contains("&9"))
      {
        Message = Message.replace("&9", ChatColor.BLUE + "");
      }
      else if (Message.contains("&8"))
      {
        Message = Message.replace("&8", ChatColor.DARK_GRAY + "");
      }
      else if (Message.contains("&7"))
      {
        Message = Message.replace("&7", ChatColor.GRAY + "");
      }
      else if (Message.contains("&6"))
      {
        Message = Message.replace("&6", ChatColor.GOLD + "");
      }
      else if (Message.contains("&5"))
      {
        Message = Message.replace("&5", ChatColor.LIGHT_PURPLE + "");
      }
      else if (Message.contains("&4"))
      {
        Message = Message.replace("&4", ChatColor.DARK_RED + "");
      }
      else if (Message.contains("&3"))
      {
        Message = Message.replace("&3", ChatColor.DARK_AQUA + "");
      } else if (Message.contains("&2"))
      {
        Message = Message.replace("&2", ChatColor.DARK_GREEN + "");
      } else if (Message.contains("&1"))
      {
        Message = Message.replace("&1", ChatColor.DARK_BLUE + "");
      } else if (Message.contains("&0"))
      {
        Message = Message.replace("&0", ChatColor.BLACK + "");
      }
    }

    return Message;
  }
}