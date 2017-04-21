package factionmod.enums;

/**
 * Enumerates all the types of relation which are possible between two faction.
 * 
 * @author BrokenSwing
 *
 */
public enum EnumRelationType {

	ENEMY("enemy", false, EnumEntityInteraction.ATTACK),
	ALLY("ally", true, EnumWorldModification.BREAK_BLOCK, EnumWorldModification.PLACE_BLOCK, EnumWorldModification.PLACE_BLOCK, EnumEntityInteraction.TRADE),
	COMMERCIAL("commercial", true, EnumEntityInteraction.TRADE),
	NEUTRAL("neutral", true, EnumEntityInteraction.ATTACK);

	private String						displayName;
	private IInteractionPermission[]	permissions;
	private boolean						needResponse;

	private EnumRelationType(String displayName, boolean needResponse, IInteractionPermission... permissions) {
		this.displayName = displayName;
		this.permissions = permissions;
		this.needResponse = needResponse;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public boolean isResponseNeeded() {
		return this.needResponse;
	}

	public boolean hasPermission(IInteractionPermission perm) {
		for(IInteractionPermission permission : permissions) {
			if (perm == permission)
				return true;
		}
		return false;
	}

	public IInteractionPermission[] getPermissions() {
		return this.permissions;
	}

}
