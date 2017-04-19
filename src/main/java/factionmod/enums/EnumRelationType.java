package factionmod.enums;

public enum EnumRelationType {

	ENEMY("Enemy", false, EnumEntityInteraction.ATTACK),
	ALLY("Ally", true, EnumWorldModification.BREAK_BLOCK, EnumWorldModification.PLACE_BLOCK, EnumWorldModification.PLACE_BLOCK, EnumEntityInteraction.TRADE),
	COMMERCIAL("Commercial", true, EnumEntityInteraction.TRADE),
	NEUTRAL("Neutral", true, EnumEntityInteraction.ATTACK);

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
