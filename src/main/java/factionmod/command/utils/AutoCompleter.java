package factionmod.command.utils;

import java.util.ArrayList;

import factionmod.utils.ServerUtils;

/**
 * This class provide an help to complete the commands.
 * 
 * @author BrokenSwing
 *
 */
public class AutoCompleter {

	public static ArrayList<String> completePlayer(String playerName) {
		String[] usernames = ServerUtils.getServer().getPlayerProfileCache().getUsernames();
		ArrayList<String> ret = new ArrayList<String>();
		for(String username : usernames) {
			if (username.toLowerCase().startsWith(playerName.toLowerCase())) {
				ret.add(username);
			}
		}
		return ret;
	}

	public static ArrayList<String> complete(String name, Object[] objects) {
		ArrayList<String> ret = new ArrayList<String>();
		for(Object obj : objects) {
			if (obj.toString().toLowerCase().startsWith(name.toLowerCase())) {
				ret.add(obj.toString());
			}
		}
		return ret;
	}

}
