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

    /**
     * Retrieves the {@link UUID} of a player from hs name.
     * 
     * @param playerName
     *            The name of the player
     * @return the UUID of the player
     */
    public static UUID getUUIDOf(final String playerName) {
        final GameProfile profile = ServerUtils.getServer().getPlayerProfileCache().getGameProfileForUsername(playerName);
        if (profile != null)
            return profile.getId();
        return null;
    }

    /**
     * Retrieves the name of a player from his {@link UUID}.
     * 
     * @param uuid
     *            The UUID of the player
     * @return the name of the player
     */
    public static String getNameOf(final UUID uuid) {
        final GameProfile profile = ServerUtils.getServer().getPlayerProfileCache().getProfileByUUID(uuid);
        if (profile != null)
            return profile.getName();
        return null;
    }

}
