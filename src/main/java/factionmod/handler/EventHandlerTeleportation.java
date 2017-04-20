package factionmod.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import factionmod.FactionMod;
import factionmod.utils.MessageHelper;
import factionmod.utils.ServerUtils;
import factionmod.utils.TeleportationHelper;

/**
 * Handles the teleportations delays.
 * 
 * @author BrokenSwing
 *
 */
@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerTeleportation {

	private static final HashMap<UUID, Long>		timers				= new HashMap<UUID, Long>();
	private static final HashMap<UUID, BlockPos>	positions			= new HashMap<UUID, BlockPos>();
	private static final HashMap<UUID, BlockPos>	standingPositions	= new HashMap<UUID, BlockPos>();
	private static long								lastTickTime		= System.currentTimeMillis();

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		synchronized (timers) {
			Iterator<Entry<UUID, Long>> it = timers.entrySet().iterator();
			long currentTime = System.currentTimeMillis();

			while (it.hasNext()) {
				Entry<UUID, Long> entry = it.next();
				UUID uuid = entry.getKey();
				long remainingTime = entry.getValue() - (currentTime - lastTickTime);
				if (remainingTime < 0) {
					TeleportationHelper.teleport(uuid, positions.get(uuid));
					positions.remove(uuid);
					standingPositions.remove(uuid);
					it.remove();
				} else {
					if ((int) (entry.getValue() / 1000) != (int) (remainingTime / 1000)) {
						sendPlayerMessage(uuid, (int) (remainingTime / 1000));
					}
					entry.setValue(remainingTime);
				}
			}
			lastTickTime = System.currentTimeMillis();
		}
	}

	/**
	 * Used to cancel the teleportation when the player moves.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerMove(PlayerTickEvent event) {
		synchronized (standingPositions) {
			UUID uuid = event.player.getUniqueID();
			if (standingPositions.containsKey(uuid)) {
				if (!event.player.getPosition().equals(standingPositions.get(uuid))) {
					cancelTeleportation(uuid);
				}
			}
		}
	}

	/**
	 * Used to cancel teleportation when the player takes damages.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerHurt(LivingHurtEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			if (positions.containsKey(event.getEntity().getUniqueID())) {
				cancelTeleportation(event.getEntity().getUniqueID());
			}
		}
	}

	private static void sendPlayerMessage(UUID uuid, int remainingTime) {
		EntityPlayer player = ServerUtils.getPlayer(uuid);
		if (player != null) {
			player.sendMessage(MessageHelper.info("You will be teleported in " + (remainingTime + 1) + " seconds."));
		}
	}

	/**
	 * Teleports a player to a position after a delay.
	 * 
	 * @param uuid
	 *            The UUID of the player
	 * @param pos
	 *            The teleportation point
	 * @param time
	 *            The delay
	 */
	public static void teleport(EntityPlayer player, BlockPos pos, int time) {
		synchronized (timers) {
			UUID uuid = player.getUniqueID();
			timers.put(uuid, time * 1000L);
			positions.put(uuid, pos);
			standingPositions.put(uuid, player.getPosition());
		}
	}

	/**
	 * Cancels the teleportation process of a player.
	 * 
	 * @param uuid
	 *            The UUID of the player
	 */
	public static void cancelTeleportation(UUID uuid) {
		synchronized (timers) {
			timers.remove(uuid);
			positions.remove(uuid);
			standingPositions.remove(uuid);
			EntityPlayer player = ServerUtils.getPlayer(uuid);
			if (player != null) {
				player.sendMessage(MessageHelper.warn("Your teleportation was canceled."));
			}
		}
	}

}
