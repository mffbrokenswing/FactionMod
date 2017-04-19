package factionmod.manager;

import java.util.List;

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
 * A simple implementation of {@link IChunkManager} to avoid developers to
 * override each methods.
 * 
 * @author BrokenSwing
 *
 */
public abstract class ChunkManager implements IChunkManager {

	@Override
	public void onBreakBlock(BreakEvent event) {}

	@Override
	public void onPlaceBlock(PlaceEvent event) {}

	@Override
	public void onBlocksExplode(World world, List<BlockPos> blocks) {}

	@Override
	public void onEntityHurt(LivingHurtEvent event) {}

	@Override
	public void onPlayerRightClickBlock(RightClickBlock event) {}

	@Override
	public void onPlayerRightClickEntity(EntityInteract event) {}

	@Override
	public void onPlayerRightClickItem(RightClickItem event) {}

	@Override
	public void onEntityJoin(EntityJoinWorldEvent event) {}

	@Override
	public ITextComponent getName() {
		return null;
	}

	@Override
	public void onItemToss(ItemTossEvent event) {}

	@Override
	public void onEntityUseItem(LivingEntityUseItemEvent event) {}

	@Override
	public void onBucketFill(FillBucketEvent event) {}

	@Override
	public void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {}

	@Override
	public void onPlayerAttack(AttackEntityEvent event) {}

}
