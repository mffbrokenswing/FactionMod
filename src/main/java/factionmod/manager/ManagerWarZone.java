package factionmod.manager;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import factionmod.handler.EventHandlerAdmin;

/**
 * Manages chunks where the players can fight without affecting the terrain.
 * 
 * @author BrokenSwing
 *
 */
public class ManagerWarZone extends ChunkManager {

	public static final ManagerWarZone	DEFAULT	= new ManagerWarZone();

	@Override
	public void onBucketFill(FillBucketEvent event) {
		if (!EventHandlerAdmin.isAdmin((EntityPlayerMP) event.getEntityPlayer()))
			event.setCanceled(true);
	}

	@Override
	public void onBreakBlock(BreakEvent event) {
		if (!EventHandlerAdmin.isAdmin((EntityPlayerMP) event.getPlayer()))
			event.setCanceled(true);
	}

	@Override
	public void onPlaceBlock(PlaceEvent event) {
		if (!EventHandlerAdmin.isAdmin((EntityPlayerMP) event.getPlayer()))
			event.setCanceled(true);
	}

	@Override
	public void onBlocksExplode(World world, List<BlockPos> blocks) {
		blocks.clear();
	}

	@Override
	public void onEntityJoin(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityMob || entity instanceof EntityDragon || entity instanceof EntityAmbientCreature || entity instanceof EntityFlying || entity instanceof EntitySlime || entity instanceof EntityWaterMob) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
		event.setLevel(30);
	}

	@Override
	public ITextComponent getName() {
		return new TextComponentString("* WarZone *").setStyle(new Style().setColor(TextFormatting.DARK_RED));
	}

}
