package factionmod.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import factionmod.FactionMod;
import factionmod.config.ConfigExperience;
import factionmod.enums.EnumRelationType;
import factionmod.event.FactionLevelUpEvent;
import factionmod.faction.Faction;
import factionmod.faction.Levels;
import factionmod.utils.MessageHelper;

@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerExperience {

	/**
	 * Used to give experience to factions when their members kills monsters or
	 * players.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void playerKillEntity(LivingDeathEvent event) {
		if (event.getSource().getDamageType().equals("player")) {
			EntityPlayerMP player = (EntityPlayerMP) event.getSource().getEntity();
			if (!EventHandlerFaction.hasUserFaction(player.getUniqueID()))
				return;
			Faction faction = EventHandlerFaction.getFaction(EventHandlerFaction.getFactionOf(player.getUniqueID()));
			Entity target = event.getEntity();
			if (target instanceof EntityPlayerMP) {
				if (EventHandlerFaction.hasUserFaction(target.getUniqueID())) {
					if (faction.getRelationWith(EventHandlerFaction.getFactionOf(target.getUniqueID())).getType() == EnumRelationType.ENEMY) {
						addExp(faction, ConfigExperience.killEnemy);
					}
				}
			} else if (target instanceof EntityWitherSkeleton) {
				addExp(faction, ConfigExperience.killWitherSkeleton);
			} else if (target instanceof EntityWither) {
				addExp(faction, ConfigExperience.killWither);
			} else if (target instanceof EntityDragon) {
				addExp(faction, ConfigExperience.killDragon);
			}
		}
	}

	/**
	 * Adds the specified amount of experience to the specified faction. If the
	 * experience increased, it will update the modded clients.
	 * 
	 * @param faction
	 *            The faction
	 * @param amount
	 *            The amount of experience to add
	 */
	public static void addExp(Faction faction, int amount) {
		if (amount > 0) {
			faction.increaseExp(amount);
			ModdedClients.updateFaction(faction);
		}
	}

	/**
	 * Used to broadcast a message to the faction when the faction levels up.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onLevelUp(FactionLevelUpEvent event) {
		EventHandlerFaction.broadcastToFaction(event.getFaction(), "Your faction reached the level " + event.getFaction().getLevel() + ". Now you can have " + Levels.getMaximumChunksForLevel(event.getFaction().getLevel()) + " chunks claimed.", MessageHelper.INFO);
	}

}
