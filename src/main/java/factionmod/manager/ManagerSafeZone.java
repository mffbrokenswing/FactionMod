package factionmod.manager;

import factionmod.handler.EventHandlerAdmin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

/**
 * Manages safe chunks, player shouldn't be able to take damages.
 * 
 * @author BrokenSwing
 *
 */
public class ManagerSafeZone extends ManagerWarZone {

	public static final ManagerSafeZone	DEFAULT	= new ManagerSafeZone();

	@Override
	public void onEntityHurt(LivingHurtEvent event) {
		if (event.getEntity() instanceof EntityPlayerMP) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onPlayerAttack(AttackEntityEvent event) {
		if (!EventHandlerAdmin.isAdmin((EntityPlayerMP) event.getEntityPlayer()))
			event.setCanceled(true);
	}

	@Override
	public void onEntityJoin(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityMob || entity instanceof EntityDragon || entity instanceof EntityAmbientCreature || entity instanceof EntityFlying || entity instanceof EntitySlime || entity instanceof EntityWaterMob) {
			event.setCanceled(true);
		}
	}

	@Override
	public ITextComponent getName() {
		return new TextComponentString("* Safezone *").setStyle(new Style().setColor(TextFormatting.YELLOW));
	}

}
