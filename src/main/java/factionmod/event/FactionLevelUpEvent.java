package factionmod.event;

import factionmod.faction.Faction;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a faction levels up. This is not cancelable. This
 * has no result.
 *
 * @author BrokenSwing
 *
 */
public class FactionLevelUpEvent extends Event {

    private final Faction faction;

    public FactionLevelUpEvent(final Faction faction) {
        this.faction = faction;
    }

    /**
     * The faction which levels up.
     * 
     * @return The faction
     */
    public Faction getFaction() {
        return this.faction;
    }

}
