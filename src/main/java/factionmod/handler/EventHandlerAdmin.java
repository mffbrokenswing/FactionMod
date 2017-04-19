package factionmod.handler;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import factionmod.FactionMod;

@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerAdmin {

	private static final ArrayList<EntityPlayerMP>	ADMINS	= new ArrayList<EntityPlayerMP>();

	/**
	 * Used to remove admins when they disconnect.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		removeAdmin((EntityPlayerMP) event.player);
	}

	/**
	 * Adds an admins.
	 * 
	 * @param player
	 *            The player
	 */
	public static void addAdmin(EntityPlayerMP player) {
		if (!ADMINS.contains(player)) {
			ADMINS.add(player);
			player.refreshDisplayName();
		}
	}

	/**
	 * Removes an admin.
	 * 
	 * @param player
	 *            The player
	 */
	public static void removeAdmin(EntityPlayerMP player) {
		ADMINS.remove(player);
		player.refreshDisplayName();
	}

	/**
	 * Indicates if a player is admin.
	 * 
	 * @param player
	 *            The player
	 * @return true if the player is admin
	 */
	public static boolean isAdmin(EntityPlayerMP player) {
		return ADMINS.contains(player);
	}

	/**
	 * Indicates if the player with the specified {@link UUID} is admin.
	 * 
	 * @param uuid
	 *            The UUID of the player
	 * @return true if the player if admin
	 */
	public static boolean isAdmin(UUID uuid) {
		for(EntityPlayer player : ADMINS) {
			if (player.getUniqueID().equals(uuid))
				return true;
		}
		return false;
	}

}
