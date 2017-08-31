package factionmod.manager;

import factionmod.FactionMod;
import factionmod.config.ConfigGeneral;
import factionmod.faction.Faction;
import factionmod.handler.EventHandlerFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

/**
 * Manages chunks claimed by factions.
 * 
 * @author BrokenSwing
 *
 */
public class ManagerFaction extends ChunkManager {

    private Faction faction;

    public ManagerFaction(String[] factionName) {
        faction = EventHandlerFaction.getFaction(factionName[0]);
        if (faction == null) {
            FactionMod.getLogger().warn("The server will certainly crash.");
        }
    }

    public Faction getFaction() {
        return faction;
    }

    @Override
    public void onBucketFill(FillBucketEvent event) {
        if (!isInFaction(event.getEntityPlayer())) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onBreakBlock(BreakEvent event) {
        if (!isInFaction(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onPlaceBlock(PlaceEvent event) {
        if (!isInFaction(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onPlayerRightClickBlock(RightClickBlock event) {
        if (!isInFaction(event.getEntityPlayer())) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPlayer) {
            if (faction.isMember(event.getEntityPlayer().getUniqueID()) || faction.isMember(event.getTarget().getUniqueID()))
                if (faction.getLevel() < ConfigGeneral.getInt("immunity_level"))
                    event.setCanceled(true);
        }
    }

    @Override
    public ITextComponent getName() {
        return new TextComponentString("--- " + this.faction.getName() + " ---");
    }

    private boolean isInFaction(EntityPlayer player) {
        return faction.isMember(player.getUniqueID());
    }

}
