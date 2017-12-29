package factionmod.event;

import factionmod.faction.Faction;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a {@link Faction} changes its recruit link.
 *
 * @author BrokenSwing
 *
 */
public class RecruitLinkChangedEvent extends Event {

    private final Faction faction;
    private String        link;

    public RecruitLinkChangedEvent(final Faction faction, final String link) {
        this.faction = faction;
        this.link = link;
    }

    /**
     * Returns the {@link Faction} which changes its recruit link.
     *
     * @return the {@link Faction}
     */
    public Faction getFaction() {
        return this.faction;
    }

    /**
     * The new recruit link
     *
     * @return the link, can be empty
     */
    public String getNewLink() {
        return this.link;
    }

    /**
     * Returns the old recruit link.
     *
     * @return the link
     */
    public String getOldLink() {
        return this.faction.getRecruitLink();
    }

    /**
     * You can use it to change to link, set it to an empty {@link String} if you
     * want to remove the recruit link. DON'T USE
     * {@link Faction#setRecruitLink(String)} in an event, you may create an
     * infinite loop.
     *
     * @param link
     *            The new link
     */
    public void setNewLink(final String link) {
        this.link = link;
    }

}
