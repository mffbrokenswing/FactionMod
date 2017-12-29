package factionmod.manager;

import java.util.List;

import factionmod.handler.EventHandlerChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

/**
 * An object which will manage everything which is happening is a chunk. You
 * have to register it to {@link EventHandlerChunk}. Methods should be explicit.
 *
 * @author BrokenSwing
 *
 */
public interface IChunkManager {

    /**
     * Returns the component sended to the player when entering in the chunk managed
     * by this instance.
     *
     * @return the component
     */
    public ITextComponent getName();

    /**
     * Handles the parameters specified in the zone.json file.
     *
     * @param parameters
     *            The string representing the parameters
     */
    public void handleParameters(String parameters);

    public void onBreakBlock(final BreakEvent event);

    public void onPlaceBlock(final PlaceEvent event);

    /**
     * This is called when blocks of the chunk explode. If you want to cancel the
     * blocks to explode, remove their positions from the list.
     *
     * @param world
     *            The world the explosion happenned
     * @param blocks
     *            The positions of the blocks which exploded
     */
    public void onBlocksExplode(final World world, final List<BlockPos> blocks);

    public void onEntityHurt(final LivingHurtEvent event);

    public void onPlayerRightClickBlock(final RightClickBlock event);

    public void onPlayerRightClickEntity(final EntityInteract event);

    public void onPlayerRightClickItem(final RightClickItem event);

    public void onEntityJoin(final EntityJoinWorldEvent event);

    public void onEnchantmentLevelSet(final EnchantmentLevelSetEvent event);

    public void onItemToss(final ItemTossEvent event);

    public void onEntityUseItem(final LivingEntityUseItemEvent event);

    public void onBucketFill(final FillBucketEvent event);

    public void onPlayerAttack(final AttackEntityEvent event);

}
