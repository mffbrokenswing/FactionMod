package factionmod.command.utils;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import factionmod.utils.ServerUtils;

/**
 * Provides the capacity to switch between the name and the UUID of a player.
 * 
 * @author BrokenSwing
 *
 */
public class UUIDHelper {

	public static UUID getUUIDOf(String playerName) {
		GameProfile profile = ServerUtils.getServer().getPlayerProfileCache().getGameProfileForUsername(playerName);
		if (profile != null) {
			return profile.getId();
		}
		return null;
	}

	public static String getNameOf(UUID uuid) {
		GameProfile profile = ServerUtils.getServer().getPlayerProfileCache().getProfileByUUID(uuid);
		if (profile != null) {
			return profile.getName();
		}
		return null;
	}

}
