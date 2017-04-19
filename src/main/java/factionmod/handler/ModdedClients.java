package factionmod.handler;

import static factionmod.handler.EventHandlerFaction.*;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import factionmod.FactionMod;
import factionmod.faction.Faction;
import factionmod.network.PacketFaction;

@EventBusSubscriber(modid = FactionMod.MODID)
public class ModdedClients {

	private static final ArrayList<EntityPlayerMP>	moddedClients	= new ArrayList<EntityPlayerMP>();

	/**
	 * Adds a moddded client.
	 * 
	 * @param player
	 *            The player who has a modded client
	 */
	public static void addModdedClient(EntityPlayerMP player) {
		if (!moddedClients.contains(player)) {
			moddedClients.add(player);
			updateClient(player);
		}
	}

	/**
	 * Refresh all members of a faction.
	 * 
	 * @param faction
	 *            The faction
	 */
	public static void updateFaction(Faction faction) {
		NBTTagCompound compound = new NBTTagCompound();
		for(EntityPlayerMP player : moddedClients) {
			if (faction.isMember(player.getUniqueID())) {
				updateClient(player, compound);
			}
		}
	}

	/**
	 * Updates the player with the given UUID
	 * 
	 * @param uuid
	 *            The UUID of the player
	 */
	public static void updateClient(UUID uuid) {
		for(EntityPlayerMP player : moddedClients) {
			if(player.getUniqueID().equals(uuid)) {
				updateClient(player);
				return;
			}
		}
	}

	/**
	 * Updates the informations on the moddded client.
	 * 
	 * @param player
	 *            The player to update
	 */
	public static void updateClient(EntityPlayerMP player) {
		if (hasUserFaction(player.getUniqueID())) {
			Faction faction = EventHandlerFaction.getFaction(getFactionOf(player.getUniqueID()));
			updateClient(player, faction.toNBT());
		} else {
			FactionMod.getNetwork().sendTo(new PacketFaction(), player);
		}
	}

	/**
	 * Sends the specified NBTTagCompound to the specified player.
	 * 
	 * @param player
	 *            The player
	 * @param faction
	 *            The NBTTagCompound
	 */
	public static void updateClient(EntityPlayerMP player, NBTTagCompound faction) {
		FactionMod.getNetwork().sendTo(new PacketFaction(faction), player);
	}

	/**
	 * Indicates if a player is modded.
	 * 
	 * @param player
	 *            The player to test
	 * @return true if the player is modded, else false
	 */
	public static boolean isClientModded(EntityPlayerMP player) {
		return moddedClients.contains(player);
	}

	/**
	 * Used to remove the players who disconnect from the list.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void playerLoggedOut(PlayerLoggedOutEvent event) {
		moddedClients.remove(event.player);
	}

}
