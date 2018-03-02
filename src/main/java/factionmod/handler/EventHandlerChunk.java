package factionmod.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import factionmod.FactionMod;
import factionmod.data.FactionModDatas;
import factionmod.manager.IChunkManager;
import factionmod.manager.instanciation.Zone;
import factionmod.manager.instanciation.ZoneInstance;
import factionmod.utils.DimensionalPosition;
import factionmod.utils.ServerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

/**
 * It handles everything which is relative to the management of the chunks of
 * the world.
 *
 * @author BrokenSwing
 *
 */
@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerChunk {

    /** Links all chunks to its manager */
    private static final HashMap<DimensionalPosition, IChunkManager> MANAGERS         = new HashMap<>();
    /**
     * Links all chunks to a {@link ZoneInstance} to recreate the manager when
     * reloading the server
     */
    private static final HashMap<DimensionalPosition, ZoneInstance>  ZONE_INSTANCES   = new HashMap<>();
    /**
     * Links all the players with the name of the last manager which handled him
     */
    private static final HashMap<UUID, String>                       CHUNK_NAME_CACHE = new HashMap<>();
    /** Links each {@link Zone} with his name */
    private static final HashMap<String, Zone>                       ZONE_MAPPING     = new HashMap<>();

    /**
     * Registers a {@link Zone}.
     *
     * @param zone
     *            The zone to register
     */
    public static void registerZone(final Zone zone) {
        ZONE_MAPPING.put(zone.getName(), zone);
    }

    /**
     * Returns a set containing all the names of the zones.
     *
     * @return an unmodifiable set of String
     */
    public static Set<String> getZonesNames() {
        return Collections.unmodifiableSet(ZONE_MAPPING.keySet());
    }

    /**
     * Returns the {@link Zone} associated with the given name.
     *
     * @param name
     *            The name of the Zone
     * @return The instance of the zone
     */
    public static Zone getZone(final String name) {
        return ZONE_MAPPING.get(name);
    }

    /**
     * Registers an {@link IChunkManager}.
     *
     * @param manager
     *            The {@link IChunkManager}
     * @param pos
     *            The position to handle with this manager
     * @param instance
     *            The instanciator of the manager
     * @param refreshPlayers
     *            Set it to true if the name of the chunk should be refresh
     */
    public static void registerChunkManager(final IChunkManager manager, final DimensionalPosition pos, final ZoneInstance instance, final boolean refreshPlayers) {
        MANAGERS.put(pos, manager);
        ZONE_INSTANCES.put(pos, instance);
        if (refreshPlayers) {
            refreshPlayersDisplays(pos);
        }
        FactionModDatas.save();
    }

    /**
     * Unregister an {@link IChunkManager}.
     *
     * @param pos
     *            The position of this {@link IChunkManager}
     * @param refreshPlayers
     *            Set it to true if the name of the chunk should be refresh
     */
    public static void unregisterChunkManager(final DimensionalPosition pos, final boolean refreshPlayers) {
        MANAGERS.remove(pos);
        ZONE_INSTANCES.remove(pos);
        if (refreshPlayers) {
            refreshPlayersDisplays(pos);
        }
        FactionModDatas.save();
    }

    public static Map<DimensionalPosition, ZoneInstance> getZonesInstances() {
        return Collections.unmodifiableMap(ZONE_INSTANCES);
    }

    /**
     * Returns the manager at the {@link BlockPos} in the {@link World}.
     *
     * @param world
     *            The world
     * @param pos
     *            The block position
     * @return the associated {@link IChunkManager}, can be null
     */
    public static IChunkManager getManagerFor(final World world, final BlockPos pos) {
        return getManagerFor(DimensionalPosition.from(world, pos));

    }

    /**
     * The manager of the chunk at the {@link DimensionalPosition}.
     *
     * @param position
     *            The position
     * @return the associated {@link IChunkManager}, can be null
     */
    public static IChunkManager getManagerFor(final DimensionalPosition position) {
        return MANAGERS.get(position);
    }

    /**
     * Returns the manager of the chunk where the entity is placed.
     *
     * @param entity
     *            The entity in the world
     * @return the associated {@link IChunkManager}, can be null
     */
    public static IChunkManager getManagerFor(final Entity entity) {
        return getManagerFor(entity.getEntityWorld(), entity.getPosition());
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onBreakBlock(final BreakEvent event) {
        final IChunkManager manager = getManagerFor(event.getWorld(), event.getPos());
        if (manager != null)
            manager.onBreakBlock(event);
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onPlaceBlock(final PlaceEvent event) {
        final IChunkManager manager = getManagerFor(event.getWorld(), event.getPos());
        if (manager != null) {
            manager.onPlaceBlock(event);
            if (event.isCanceled())
                // Should fix a sync bug
                ((EntityPlayerMP) event.getPlayer()).connection.sendPacket(new SPacketSetSlot(-2, event.getPlayer().inventory.currentItem, event.getPlayer().getHeldItem(event.getHand())));
        }
    }

    /**
     * Each block is sended to the attached manager.
     */
    @SubscribeEvent
    public static void onExplosion(final ExplosionEvent.Detonate event) {
        final HashMap<DimensionalPosition, List<BlockPos>> blocks = new HashMap<>();
        for (final BlockPos pos : event.getAffectedBlocks()) {
            final DimensionalPosition dimPos = DimensionalPosition.from(event.getWorld(), pos);
            List<BlockPos> positions = blocks.get(dimPos);
            if (positions == null) {
                positions = new ArrayList<>();
                blocks.put(dimPos, positions);
            }
            positions.add(pos);
        }
        for (final Entry<DimensionalPosition, List<BlockPos>> entry : blocks.entrySet()) {
            final IChunkManager manager = getManagerFor(entry.getKey());
            if (manager != null) {
                event.getAffectedBlocks().removeAll(entry.getValue());
                manager.onBlocksExplode(event.getWorld(), entry.getValue());
                event.getAffectedBlocks().addAll(entry.getValue());
            }
        }
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onEntityHurt(final LivingHurtEvent event) {
        final IChunkManager manager = MANAGERS.get(DimensionalPosition.from(event.getEntity()));
        if (manager != null)
            manager.onEntityHurt(event);
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onPlayerRightClickBlock(final RightClickBlock event) {
        final IChunkManager manager = getManagerFor(DimensionalPosition.from(event.getWorld(), event.getPos()));
        if (manager != null)
            manager.onPlayerRightClickBlock(event);
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onPlayerRightClickEntity(final EntityInteract event) {
        final IChunkManager manager = getManagerFor(event.getTarget());
        if (manager != null)
            manager.onPlayerRightClickEntity(event);
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onPlayerRightClickItem(final RightClickItem event) {
        final IChunkManager manager = getManagerFor(event.getEntity());
        if (manager != null)
            manager.onPlayerRightClickItem(event);
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onEntityJoin(final EntityJoinWorldEvent event) {
        final IChunkManager manager = getManagerFor(event.getEntity());
        if (manager != null)
            manager.onEntityJoin(event);
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onEnchantmentLevelSet(final EnchantmentLevelSetEvent event) {
        final IChunkManager manager = getManagerFor(event.getWorld(), event.getPos());
        if (manager != null)
            manager.onEnchantmentLevelSet(event);
    }

    /**
     * Used to send the name of the chunk to the player.
     */
    @SubscribeEvent
    public static void onEnteringChunk(final EntityEvent.EnteringChunk event) {
        refreshPlayerDisplay(event.getEntity(), new DimensionalPosition(new ChunkPos(event.getNewChunkX(), event.getNewChunkZ()), event.getEntity().getEntityWorld().provider.getDimension()));
    }

    /**
     * Checks if the name of the chunk changed and send the new name to the player
     * if needed.
     *
     * @param entity
     *            The entity to send the message
     * @param position
     *            The position of the chunk
     */
    private static void refreshPlayerDisplay(final Entity entity, final DimensionalPosition position) {
        final UUID uuid = entity.getUniqueID();
        ITextComponent message;
        final IChunkManager manager = getManagerFor(position);
        if (manager != null)
            message = manager.getName();
        else
            message = new TextComponentString("* Wilderness *").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE));
        if (!CHUNK_NAME_CACHE.containsKey(uuid) || !CHUNK_NAME_CACHE.get(uuid).equals(message.getFormattedText())) {
            CHUNK_NAME_CACHE.put(uuid, message.getFormattedText());
            entity.sendMessage(message);
        }
    }

    /**
     * Invokes
     * {@link EventHandlerChunk#refreshPlayerDisplay(Entity, DimensionalPosition)}
     * for each entity in the given chunk.
     *
     * @param position
     *            The position of the chunk
     */
    private static void refreshPlayersDisplays(final DimensionalPosition position) {
        final World world = ServerUtils.getServer().getWorld(position.getDimension());
        final Chunk chunk = world.getChunkFromChunkCoords(position.getPos().x, position.getPos().z);
        final ClassInheritanceMultiMap<Entity>[] list = chunk.getEntityLists();
        for (final ClassInheritanceMultiMap<Entity> element : list)
            for (final Entity entity : element)
                if (entity instanceof EntityPlayer)
                    refreshPlayerDisplay(entity, position);
    }

    /**
     * Used to remove the cache of the player disconnecting.
     */
    @SubscribeEvent
    public static void onLeavingServer(final PlayerLoggedOutEvent event) {
        CHUNK_NAME_CACHE.remove(event.player.getUniqueID());
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onItemToss(final ItemTossEvent event) {
        final IChunkManager manager = getManagerFor(event.getEntity());
        if (manager != null)
            manager.onItemToss(event);
    }

    /**
     * Sended to the attached manager.
     */
    @SubscribeEvent
    public static void onBucketFill(final FillBucketEvent event) {
        if (event.getTarget() != null) {
            final IChunkManager manager = getManagerFor(DimensionalPosition.from(event.getWorld(), event.getTarget().getBlockPos()));
            if (manager != null)
                manager.onBucketFill(event);
        }
    }

}
