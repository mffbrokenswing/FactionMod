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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * It handles everything which is relative to the experience of the factions.
 *
 * @author BrokenSwing
 *
 */
@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerExperience {

    private static final IForgeRegistry<EntityEntry> ENTITIES = ForgeRegistries.ENTITIES;

    /**
     * Used to give experience to factions when their members kills monsters or
     * players.
     */
    @SubscribeEvent
    public static void playerKillEntity(final LivingDeathEvent event) {
        if (event.getSource().getDamageType().equals("player")) {
            final EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
            if (!EventHandlerFaction.hasUserFaction(player.getUniqueID()))
                return;
            final Faction faction = EventHandlerFaction.getFaction(EventHandlerFaction.getFactionOf(player.getUniqueID()));
            final Entity target = event.getEntity();
            if (target instanceof EntityPlayerMP) {
                if (EventHandlerFaction.hasUserFaction(target.getUniqueID())) {
                    FactionAPI.getFactionOf(target.getUniqueID()).damageFaction(1);
                    final int exp = ConfigExperience.getExpFor("kill_enemy");
                    addExp(faction, exp, player.getUniqueID());
                    FactionMod.getLogger().debug("The player " + player.getName() + " killed an enemy and earned " + exp + " experience for faction " + faction.getName());
                }
            } else
                ENTITIES.getEntries().forEach(entry -> {
                    if (entry.getValue().getEntityClass().equals(target.getClass())) {
                        final int exp = ConfigExperience.getExpFor("kill_" + entry.getKey().toString());
                        addExp(faction, exp, player.getUniqueID());
                        FactionMod.getLogger().debug("The player " + player.getName() + " killed " + entry.getKey() + " and earned " + exp + " experience for faction " + faction.getName());
                    }
                });
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
    public static void addExp(final Faction faction, final int amount, final UUID member) {
        if (amount > 0)
            faction.increaseExp(amount, member);
    }

    /**
     * Used to broadcast a message to the faction when the faction levels up.
     */
    @SubscribeEvent
    public static void onLevelUp(final FactionLevelUpEvent event) {
        EventHandlerFaction.broadcastToFaction(event.getFaction(),
                String.format(ConfigLang.translate("faction.levelup"), event.getFaction().getLevel(), Levels.getMaximumChunksForLevel(event.getFaction().getLevel())), MessageHelper.INFO);
    }

}
