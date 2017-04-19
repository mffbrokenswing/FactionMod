package factionmod.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import factionmod.FactionMod;
import factionmod.enums.EnumEntityInteraction;
import factionmod.enums.EnumRelationType;
import factionmod.faction.Faction;
import factionmod.faction.RelationShip;

@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerRelation {

	private static final ArrayList<RelationShip>	RELATIONS	= new ArrayList<RelationShip>();
	private static final ArrayList<RelationShip>	PENDING		= new ArrayList<RelationShip>();

	/**
	 * Used to disable friendly-fire
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerAttack(AttackEntityEvent event) {
		if (event.getTarget() instanceof EntityPlayer) {
			EntityPlayer target = (EntityPlayer) event.getTarget();
			if (EventHandlerFaction.hasUserFaction(target.getUniqueID()) && EventHandlerFaction.hasUserFaction(event.getEntityPlayer().getUniqueID())) {
				String fac1 = EventHandlerFaction.getFactionOf(target.getUniqueID());
				String fac2 = EventHandlerFaction.getFactionOf(event.getEntityPlayer().getUniqueID());
				if (!fac1.equalsIgnoreCase(fac2)) {
					Faction faction = EventHandlerFaction.getFaction(fac2);
					if (!faction.getRelationWith(fac1).getType().hasPermission(EnumEntityInteraction.ATTACK)) {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	/**
	 * Sets a relation between to factions. Removes the current relation if
	 * existing.
	 * 
	 * @param faction1
	 *            The first faction
	 * @param faction2
	 *            The second faction
	 * @param type
	 *            The type of relation
	 */
	public static void setRelation(Faction faction1, Faction faction2, EnumRelationType type) {
		removeRelation(faction1, faction2);
		removePending(faction1, faction2);
		RelationShip relation = new RelationShip(type, faction1.getName(), faction2.getName());
		faction1.addRelation(relation);
		faction2.addRelation(relation);
		RELATIONS.add(relation);
	}

	/**
	 * Adds a relation.
	 * 
	 * @param relation
	 *            The relation to add
	 * @param pending
	 *            Set it to true if you want to make this relation pending
	 */
	public static void setRelation(RelationShip relation, boolean pending) {
		if (EventHandlerFaction.doesFactionExist(relation.getFirstFaction()) && EventHandlerFaction.doesFactionExist(relation.getSecondFaction())) {
			Faction faction1 = EventHandlerFaction.getFaction(relation.getFirstFaction());
			Faction faction2 = EventHandlerFaction.getFaction(relation.getSecondFaction());
			if (pending) {
				setPending(faction1, faction2, relation.getType());
			} else {
				setRelation(faction1, faction2, relation.getType());
			}
		}
	}

	/**
	 * Removes a relation between to factions.
	 * 
	 * @param faction1
	 *            The first faction
	 * @param faction2
	 *            The second faction
	 */
	public static void removeRelation(Faction faction1, Faction faction2) {
		ListIterator<RelationShip> it = RELATIONS.listIterator();
		while (it.hasNext()) {
			RelationShip relation = it.next();
			if (relation.getFirstFaction().equalsIgnoreCase(faction1.getName()) && relation.getSecondFaction().equalsIgnoreCase(faction2.getName()) || relation.getFirstFaction().equalsIgnoreCase(faction2.getName()) && relation.getSecondFaction().equalsIgnoreCase(faction1.getName())) {
				it.remove();
				return;
			}
		}
	}

	/**
	 * Sets a pending relation.
	 * 
	 * @param faction1
	 *            The faction which wants to create the relation
	 * @param faction2
	 *            The second faction
	 * @param type
	 *            The type of the relation
	 */
	public static void setPending(Faction faction1, Faction faction2, EnumRelationType type) {
		removePending(faction1, faction2);
		PENDING.add(new RelationShip(type, faction2.getName(), faction1.getName()));
	}

	/**
	 * Removes a pending relation.
	 * 
	 * @param faction1
	 *            The first faction
	 * @param faction2
	 *            The second faction
	 */
	public static void removePending(Faction faction1, Faction faction2) {
		RelationShip relation = getPending(faction1, faction2);
		if (relation != null)
			PENDING.remove(relation);
	}

	/**
	 * Returns a pending relation.
	 * 
	 * @param faction1
	 *            The first faction
	 * @param faction2
	 *            The second faction
	 * @return the pending relation between the factions, or null if it doesn't
	 *         exist
	 */
	public static RelationShip getPending(Faction faction1, Faction faction2) {
		ListIterator<RelationShip> it = PENDING.listIterator();
		while (it.hasNext()) {
			RelationShip relation = it.next();
			if (relation.getFirstFaction().equalsIgnoreCase(faction1.getName()) && relation.getSecondFaction().equalsIgnoreCase(faction2.getName()) || relation.getFirstFaction().equalsIgnoreCase(faction2.getName()) && relation.getSecondFaction().equalsIgnoreCase(faction1.getName())) {
				return relation;
			}
		}
		return null;
	}

	public static List<RelationShip> getRelations() {
		return Collections.unmodifiableList(RELATIONS);
	}

	public static List<RelationShip> getPendingRelations() {
		return Collections.unmodifiableList(PENDING);
	}

	public static void clearRegistry() {
		RELATIONS.clear();
		PENDING.clear();
	}

}
