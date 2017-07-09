package factionmod.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	private static final HashMap<DimensionalPosition, IChunkManager>	managers		= new HashMap<DimensionalPosition, IChunkManager>();
	/**
	 * Links all chunks to a {@link ZoneInstance} to recreate the manager when
	 * reloading the server
	 */
	private static final HashMap<DimensionalPosition, ZoneInstance>		zoneInstances	= new HashMap<DimensionalPosition, ZoneInstance>();
	/**
	 * Links all the players with the name of the last manager which handled him
	 */
	private static final HashMap<UUID, String>							chunkNamesCache	= new HashMap<UUID, String>();
	/** Links each {@link Zone} with his name */
	private static final HashMap<String, Zone>							zoneMapping		= new HashMap<String, Zone>();

	/**
	 * Registers a {@link Zone}.
	 * 
	 * @param zone
	 *            The zone to register
	 */
	public static void registerZone(Zone zone) {
		zoneMapping.put(zone.getName(), zone);
	}

	/**
	 * Returns the {@link Zone} associated with the given name.
	 * 
	 * @param name
	 *            The name of the Zone
	 * @return The instance of the zone
	 */
	public static Zone getZone(String name) {
		return zoneMapping.get(name);
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
	public static void registerChunkManager(IChunkManager manager, DimensionalPosition pos, ZoneInstance instance, boolean refreshPlayers) {
		managers.put(pos, manager);
		zoneInstances.put(pos, instance);
		if (refreshPlayers)
			checkForChunkNameUpdate(pos);
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
	public static void unregisterChunkManager(DimensionalPosition pos, boolean refreshPlayers) {
		managers.remove(pos);
		zoneInstances.remove(pos);
		if (refreshPlayers)
			checkForChunkNameUpdate(pos);
		FactionModDatas.save();
	}

	public static Map<DimensionalPosition, ZoneInstance> getZonesInstances() {
		return Collections.unmodifiableMap(zoneInstances);
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
	public static IChunkManager getManagerFor(World world, BlockPos pos) {
		return getManagerFor(DimensionalPosition.from(world, pos));

	}

	/**
	 * The manager of the chunk at the {@link DimensionalPosition}.
	 * 
	 * @param position
	 *            The position
	 * @return the associated {@link IChunkManager}, can be null
	 */
	public static IChunkManager getManagerFor(DimensionalPosition position) {
		return managers.get(position);
	}

	/**
	 * Returns the manager of the chunk where the entity is placed.
	 * 
	 * @param entity
	 *            The entity in the world
	 * @return the associated {@link IChunkManager}, can be null
	 */
	public static IChunkManager getManagerFor(Entity entity) {
		return getManagerFor(entity.getEntityWorld(), entity.getPosition());
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onBreakBlock(BreakEvent event) {
		final IChunkManager manager = getManagerFor(event.getWorld(), event.getPos());
		if (manager != null) {
			manager.onBreakBlock(event);
		}
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlaceBlock(PlaceEvent event) {
		final IChunkManager manager = getManagerFor(event.getWorld(), event.getPos());
		if (manager != null) {
			manager.onPlaceBlock(event);
		}
	}

	/**
	 * Each block is sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		HashMap<DimensionalPosition, List<BlockPos>> blocks = new HashMap<DimensionalPosition, List<BlockPos>>();
		for(BlockPos pos : event.getAffectedBlocks()) {
			DimensionalPosition dimPos = DimensionalPosition.from(event.getWorld(), pos);
			List<BlockPos> positions = blocks.get(dimPos);
			if (positions == null) {
				positions = new ArrayList<BlockPos>();
				blocks.put(dimPos, positions);
			}
			positions.add(pos);
		}
		for(Entry<DimensionalPosition, List<BlockPos>> entry : blocks.entrySet()) {
			IChunkManager manager = getManagerFor(entry.getKey());
			if (manager != null) {
				event.getAffectedBlocks().removeAll(entry.getValue());
				manager.onBlocksExplode(event.getWorld(), entry.getValue());
				event.getAffectedBlocks().addAll(entry.getValue());
			}
		}
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		final IChunkManager manager = managers.get(DimensionalPosition.from(event.getEntity()));
		if (manager != null) {
			manager.onEntityHurt(event);
		}
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerRightClickBlock(RightClickBlock event) {
		final IChunkManager manager = getManagerFor(DimensionalPosition.from(event.getWorld(), event.getPos()));
		if (manager != null) {
			manager.onPlayerRightClickBlock(event);
		}
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerRightClickEntity(EntityInteract event) {
		final IChunkManager manager = getManagerFor(event.getTarget());
		if (manager != null) {
			manager.onPlayerRightClickEntity(event);
		}
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerRightClickItem(RightClickItem event) {
		final IChunkManager manager = getManagerFor(event.getEntity());
		if (manager != null) {
			manager.onPlayerRightClickItem(event);
		}
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onEntityJoin(EntityJoinWorldEvent event) {
		final IChunkManager manager = getManagerFor(event.getEntity());
		if (manager != null) {
			manager.onEntityJoin(event);
		}
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
		IChunkManager manager = getManagerFor(event.getWorld(), event.getPos());
		if (manager != null) {
			manager.onEnchantmentLevelSet(event);
		}
	}

	/**
	 * Used to send the name of the chunk to the player.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onEnteringChunk(EntityEvent.EnteringChunk event) {
		checkForChunkNameUpdate(event.getEntity(), new DimensionalPosition(new ChunkPos(event.getNewChunkX(), event.getNewChunkZ()), event.getEntity().getEntityWorld().provider.getDimension()));
	}

	/**
	 * Checks if the name of the chunk changed and send the new name to the
	 * player if needed.
	 * 
	 * @param entity
	 *            The entity to send the message
	 * @param position
	 *            The position of the chunk
	 */
	private static void checkForChunkNameUpdate(Entity entity, DimensionalPosition position) {
		UUID uuid = entity.getUniqueID();
		ITextComponent message;
		IChunkManager manager = getManagerFor(position);
		if (manager != null) {
			message = manager.getName();
		} else {
			message = new TextComponentString("* Wilderness *").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE));
		}
		if (!chunkNamesCache.containsKey(uuid) || !chunkNamesCache.get(uuid).equals(message.getFormattedText())) {
			chunkNamesCache.put(uuid, message.getFormattedText());
			entity.sendMessage(message);
		}
	}

	/**
	 * Invokes
	 * {@link EventHandlerChunk#checkForChunkNameUpdate(Entity, DimensionalPosition)}
	 * for each entity in the given chunk.
	 * 
	 * @param position
	 *            The position of the chunk
	 */
	private static void checkForChunkNameUpdate(DimensionalPosition position) {
		World world = ServerUtils.getServer().worldServerForDimension(position.getDimension());
		Chunk chunk = world.getChunkFromChunkCoords(position.getPos().chunkXPos, position.getPos().chunkZPos);
		ClassInheritanceMultiMap<Entity>[] list = chunk.getEntityLists();
		for(int i = 0; i < list.length; i++) {
			for(Entity entity : list[i]) {
				if (entity instanceof EntityPlayer) {
					checkForChunkNameUpdate(entity, position);
				}
			}
		}
	}

	/**
	 * Used to remove the cache of the player disconnecting.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onLeavingServer(PlayerLoggedOutEvent event) {
		chunkNamesCache.remove(event.player.getUniqueID());
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onItemToss(ItemTossEvent event) {
		IChunkManager manager = getManagerFor(event.getEntity());
		if (manager != null) {
			manager.onItemToss(event);
		}
	}

	/**
	 * Sended to the attached manager.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onBucketFill(FillBucketEvent event) {
		if (event.getTarget() != null) {
			IChunkManager manager = getManagerFor(DimensionalPosition.from(event.getWorld(), event.getTarget().getBlockPos()));
			if (manager != null) {
				manager.onBucketFill(event);
			}
		}
	}

}
