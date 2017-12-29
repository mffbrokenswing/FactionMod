package factionmod.manager;

import java.util.List;

import net.minecraft.util.math.BlockPos;
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
 * A simple implementation of {@link IChunkManager} to avoid developers to
 * override each methods.
 *
 * @author BrokenSwing
 *
 */
public abstract class ChunkManager implements IChunkManager {

    @Override
    public void onBreakBlock(final BreakEvent event) {}

    @Override
    public void onPlaceBlock(final PlaceEvent event) {}

    @Override
    public void onBlocksExplode(final World world, final List<BlockPos> blocks) {}

    @Override
    public void onEntityHurt(final LivingHurtEvent event) {}

    @Override
    public void onPlayerRightClickBlock(final RightClickBlock event) {}

    @Override
    public void onPlayerRightClickEntity(final EntityInteract event) {}

    @Override
    public void onPlayerRightClickItem(final RightClickItem event) {}

    @Override
    public void onEntityJoin(final EntityJoinWorldEvent event) {}

    @Override
    public void onItemToss(final ItemTossEvent event) {}

    @Override
    public void onEntityUseItem(final LivingEntityUseItemEvent event) {}

    @Override
    public void onBucketFill(final FillBucketEvent event) {}

    @Override
    public void onEnchantmentLevelSet(final EnchantmentLevelSetEvent event) {}

    @Override
    public void onPlayerAttack(final AttackEntityEvent event) {}

}
