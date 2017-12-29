package factionmod.event;

import java.util.UUID;

import factionmod.faction.Faction;
import factionmod.faction.Grade;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when the grade of a player changes.
 *
 * @author BrokenSwing
 *
 */
public class GradeChangeEvent extends Event {

    private final Faction faction;
    private final UUID    playerUUID;
    private final Grade   previousGrade;
    private final Grade   newGrade;

    public GradeChangeEvent(final Faction faction, final UUID playerUUID, final Grade previousGrade, final Grade newGrade) {
        this.faction = faction;
        this.playerUUID = playerUUID;
        this.previousGrade = previousGrade;
        this.newGrade = newGrade;
    }

    public Faction getFaction() {
        return faction;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Grade getPreviousGrade() {
        return previousGrade;
    }

    public Grade getNewGrade() {
        return newGrade;
    }

}
