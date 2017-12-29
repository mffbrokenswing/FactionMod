package factionmod.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import factionmod.FactionMod;
import factionmod.config.ConfigLang;
import factionmod.utils.MessageHelper;
import factionmod.utils.ServerUtils;
import factionmod.utils.TeleportationHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * Handles the teleportations delays.
 *
 * @author BrokenSwing
 *
 */
@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerTeleportation {

    private static final HashMap<UUID, Long>     timers            = new HashMap<>();
    private static final HashMap<UUID, BlockPos> positions         = new HashMap<>();
    private static final HashMap<UUID, BlockPos> standingPositions = new HashMap<>();
    private static long                          lastTickTime      = System.currentTimeMillis();

    @SubscribeEvent
    public static void onServerTick(final ServerTickEvent event) {
        synchronized (timers) {
            final Iterator<Entry<UUID, Long>> it = timers.entrySet().iterator();
            final long currentTime = System.currentTimeMillis();

            while (it.hasNext()) {
                final Entry<UUID, Long> entry = it.next();
                final UUID uuid = entry.getKey();
                final long remainingTime = entry.getValue() - (currentTime - lastTickTime);
                if (remainingTime < 0) {
                    TeleportationHelper.teleport(uuid, positions.get(uuid));
                    positions.remove(uuid);
                    standingPositions.remove(uuid);
                    it.remove();
                } else {
                    if ((int) (entry.getValue() / 1000) != (int) (remainingTime / 1000))
                        sendPlayerMessage(uuid, (int) (remainingTime / 1000));
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
    public static void onPlayerMove(final PlayerTickEvent event) {
        synchronized (standingPositions) {
            final UUID uuid = event.player.getUniqueID();
            if (standingPositions.containsKey(uuid))
                if (!event.player.getPosition().equals(standingPositions.get(uuid)))
                    cancelTeleportation(uuid);
        }
    }

    /**
     * Used to cancel teleportation when the player takes damages.
     * 
     * @param event
     */
    @SubscribeEvent
    public static void onPlayerHurt(final LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityPlayer)
            if (positions.containsKey(event.getEntity().getUniqueID()))
                cancelTeleportation(event.getEntity().getUniqueID());
    }

    private static void sendPlayerMessage(final UUID uuid, final int remainingTime) {
        final EntityPlayer player = ServerUtils.getPlayer(uuid);
        if (player != null)
            player.sendMessage(MessageHelper.info(String.format(ConfigLang.translate("teleportation.time.remaining"), remainingTime + 1)));
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
    public static void teleport(final EntityPlayer player, final BlockPos pos, final int time) {
        synchronized (timers) {
            final UUID uuid = player.getUniqueID();
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
    public static void cancelTeleportation(final UUID uuid) {
        synchronized (timers) {
            timers.remove(uuid);
            positions.remove(uuid);
            standingPositions.remove(uuid);
            final EntityPlayer player = ServerUtils.getPlayer(uuid);
            if (player != null)
                player.sendMessage(MessageHelper.warn(ConfigLang.translate("teleportation.canceled")));
        }
    }

}
