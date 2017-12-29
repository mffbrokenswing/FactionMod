package factionmod.enums;

/**
 * All permissions which can be given to a member of a faction.
 *
 * @author BrokenSwing
 *
 */
public enum EnumPermission {

    INVITE_USER("Invite an user"), REMOVE_USER("Remove an user"), CHANGE_DESCRIPTION("Change the description"), CLAIM_CHUNK("Claim a chunk"), SET_HOME("Set the home"), PROMOTE(
            "Promote a player"), SHOW_CHEST("Show the chest of the faction");

    private String displayName;

    private EnumPermission(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

}
