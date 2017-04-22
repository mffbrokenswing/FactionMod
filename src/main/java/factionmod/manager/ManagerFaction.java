package factionmod.manager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import factionmod.FactionMod;
import factionmod.config.Config;
import factionmod.enums.EnumWorldModification;
import factionmod.faction.Faction;
import factionmod.faction.RelationShip;
import factionmod.handler.EventHandlerFaction;

/**
 * Manages chunks claimed by factions.
 * 
 * @author BrokenSwing
 *
 */
public class ManagerFaction extends ChunkManager {

	private Faction	faction;

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
		if (!isInFaction(event.getEntityPlayer()) && !hasPermission(event.getEntityPlayer(), EnumWorldModification.BREAK_BLOCK)) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onBreakBlock(BreakEvent event) {
		if (!isInFaction(event.getPlayer()) && !hasPermission(event.getPlayer(), EnumWorldModification.BREAK_BLOCK)) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onPlaceBlock(PlaceEvent event) {
		if (!isInFaction(event.getPlayer()) && !hasPermission(event.getPlayer(), EnumWorldModification.PLACE_BLOCK)) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onPlayerRightClickBlock(RightClickBlock event) {
		if (!isInFaction(event.getEntityPlayer()) && !hasPermission(event.getEntityPlayer(), EnumWorldModification.USE_BLOCK)) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onPlayerAttack(AttackEntityEvent event) {
		if (event.getTarget() instanceof EntityPlayer) {
			if (faction.getLevel() < Config.immunityLevel)
				event.setCanceled(true);
		}
	}

	@Override
	public ITextComponent getName() {
		return new TextComponentString("--- " + this.faction.getName() + " ---");
	}

	public boolean isInFaction(EntityPlayer player) {
		return faction.isMember(player.getUniqueID());
	}

	public boolean hasPermission(EntityPlayer player, EnumWorldModification permission) {
		if (!EventHandlerFaction.hasUserFaction(player.getUniqueID()))
			return false;
		String fac = EventHandlerFaction.getFactionOf(player.getUniqueID());
		RelationShip relation = faction.getRelationWith(fac);
		return relation.getType().hasPermission(permission);
	}

}
