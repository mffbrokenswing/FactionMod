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

    private Faction faction;
    private UUID    playerUUID;
    private Grade   previousGrade;
    private Grade   newGrade;

    public GradeChangeEvent(Faction faction, UUID playerUUID, Grade previousGrade, Grade newGrade) {
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
