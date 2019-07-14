package net.thegamingcraft.BungeeMsg.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.util.List;
/**
 * 
 * @author 1337Zero
 *
 */
public class FileConfig {

	private HashMap<String, String> data = new HashMap<String, String>();
	private String path = "";	
	private List<String> defaultconfig = Arrays.asList(
			"Message.proxy-deny-msg;&4You are a proxy...","Message.socialspy-on;&4You should recieve private Messages now!","Message.socialspy-off;&4You should not recieve any private Messages now!",
			"Message.message-send;&dTo <target>: <msg>","Message.message-recieve;&dFrom <from>: <msg>","Message.player-offline;&4The player you last got a message from is not online","Message.no-reply;&4You have nobody to reply to, you must reieve a message first",
			"Message.not-to-you;&4You cannot send message to yourself","Message.unknown-player;&4Could not find player <player>","Message.socialspy-format;<from> to <target> >> <msg>","Message.socialspy-format-console;&4*** BungePrivateMessage *** <from> to <target> >> <msg>",
			"Settings.socialspy-cmd-output;false","Settings.enable-listcmd;false");
	
	public FileConfig(){
		path = System.getProperty("user.dir");
		path = path + System.getProperty("file.separator") + "plugins" + System.getProperty("file.separator") + "BungeeMsg";		
		System.out.println(path);
		try {
			loadConfig();
		} catch (Exception e) {
			System.out.println("---- Oops >.< ----");
			e.printStackTrace();
		}
	}
	/**
	 * Load all stuff from HDD
	 */
	private void loadConfig() throws Exception{
		data.clear();
		File folder_Lite_Zombe = new File(path);		
		
		if(!folder_Lite_Zombe.exists()){
			folder_Lite_Zombe.mkdir();
		}
		File config_File = new File(path + System.getProperty("file.separator") + "config.cfg");
		if(!config_File.exists()){
			config_File.createNewFile();
			createConfigFile(defaultconfig, "");
		}
		BufferedReader br = new BufferedReader (new FileReader(config_File));
		String loaded = br.readLine();
		while(loaded != null){
			if(!loaded.contains("#")){	
				if(loaded.length() > 1){
					data.put(loaded.split(";")[0], loaded.split(";")[1]);
				}				
			}			
			loaded = br.readLine();
		}
		br.close();
	}
	/**
	 * Creates the Config File and writes data from a list into it.
	 * @param list
	 * @throws Exception
	 */
	private void createConfigFile(List<String> list,String valuetochange) throws Exception{
		File config_File = new File(path + System.getProperty("file.separator") + "config.cfg");
		config_File.createNewFile();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(config_File));
		for(int i = 0; i < list.size();i++){			
			if(list.get(i).split(":")[0].equalsIgnoreCase(valuetochange.split(":")[0])){
				bw.write(valuetochange + "\n");
			}else{
				bw.write(list.get(i) + "\n");
			}		
		}
		bw.flush();
		bw.close();
	}
	/**
	 * Save Stuff to HDD
	 * @throws Exception 
	 */
	private void saveConfig(String changedpart) throws Exception{
		File config_File = new File(path +  System.getProperty("file.separator") + "config.cfg");		
		List<String> back = new ArrayList<String>();		
		for(String key : data.keySet()){
	      back.add(key + ":" + data.get(key));
	    }
		config_File.delete();
		createConfigFile(back,changedpart);
		loadConfig();
	}
	/**
	 * Returns Data stored in a HashMap, loded from the HDD.
	 * @param key
	 * @return String data loaded from File
	 */
	public String getData(String key){
		String back = "";
		if(data.get(key) == null){
			System.out.println("Your config-File is old, i will try update it for you ;D");
			for(int i = 0; i < defaultconfig.size();i++){
				if(defaultconfig.get(i).split(":")[0].equalsIgnoreCase(key)){
					data.put(key, defaultconfig.get(i).split(":")[1]);
					System.out.println("adding " + defaultconfig.get(i));
					back = defaultconfig.get(i).split(":")[1];
					try {
						saveConfig("");
					} catch (Exception e) {
						e.printStackTrace();
					}
					return back;
				}
			}
		}		
		return data.get(key);
	}
	public void replaceData(String key,String newdata){
		data.put(key, newdata);
		try{
			saveConfig(key + ":" +newdata);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}	
}
