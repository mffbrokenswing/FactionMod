package factionmod.handler;

import java.util.UUID;

import factionmod.FactionMod;
import factionmod.api.FactionModAPI.FactionAPI;
import factionmod.config.ConfigExperience;
import factionmod.config.ConfigLang;
import factionmod.event.FactionLevelUpEvent;
import factionmod.faction.Faction;
import factionmod.faction.Levels;
import factionmod.utils.MessageHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * It handles everything which is relative to the experience of the factions.
 * 
 * @author BrokenSwing
 *
 */
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
                    FactionAPI.getFactionOf(target.getUniqueID()).damageFaction(1);
                    addExp(faction, ConfigExperience.killEnemy, player.getUniqueID());
                }
            } else if (target instanceof EntityWitherSkeleton) {
                addExp(faction, ConfigExperience.killWitherSkeleton, player.getUniqueID());
            } else if (target instanceof EntityWither) {
                addExp(faction, ConfigExperience.killWither, player.getUniqueID());
            } else if (target instanceof EntityDragon) {
                addExp(faction, ConfigExperience.killDragon, player.getUniqueID());
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
     * @param member
     *            The UUID of the player who made the faction win the experience
     */
    public static void addExp(Faction faction, int amount, UUID member) {
        if (amount > 0) {
            faction.increaseExp(amount, member);
        }
    }

    /**
     * Used to broadcast a message to the faction when the faction levels up.
     * 
     * @param event
     */
    @SubscribeEvent
    public static void onLevelUp(FactionLevelUpEvent event) {
        EventHandlerFaction.broadcastToFaction(event.getFaction(), String.format(ConfigLang.translate("faction.levelup"), event.getFaction().getLevel(), Levels.getMaximumChunksForLevel(event.getFaction().getLevel())), MessageHelper.INFO);
    }

}
