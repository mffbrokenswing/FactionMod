package factionmod.config;

import net.minecraft.util.text.TextFormatting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConfigLanguage {

	public static String			onLevelUp;
	public static String			adminPrefix;
	public static TextFormatting	adminPrefixColor;
	public static String			alreadyInAFaction;
	public static String			factionAlreadyExisting;
	public static String			factionNotExisting;
	public static String			factionCreated;
	public static String			notInAFaction;
	public static String			notOwner;
	public static String			factionRemovedByOwner;
	public static String			factionRemoved;
	public static String			playerAlreadyInAFaction;
	public static String			notAMember;
	public static String			missingPermission;
	public static String			invitedToJoin;
	public static String			playerInvited;
	public static String			playerNoLongerInvited;
	public static String			playerNotAMember;
	public static String			noLongerAMember;
	public static String			nowAMember;
	public static String			playerNoLongerAMember;
	public static String			descriptionChanged;
	public static String			playerJoinedFaction;
	public static String			notInvited;
	public static String			canNotClaim;
	public static String			maxChunksCountReached;
	public static String			chunkClaimed;
	public static String			chunkNotClaimed;
	public static String			chunkUnclaimed;
	public static String			homeChanged;
	public static String			factionNowOpened;
	public static String			factionNowClosed;
	public static String			hasNotHome;
	public static String			wrongDimension;
	public static String			willBeTeleportedToHome;
	public static String			notAvailableName;
	public static String			wrongHierarchyLevel;
	public static String			gradeSet;
	public static String			gradeNotExisting;
	public static String			promoted;
	public static String			playerPromoted;
	public static String			gradeRemoved;
	public static String			relationCreated;
	public static String			relationProposed;
	public static String			relationSent;
	public static String			relationAccepted;

	public static String			description;
	public static String			members;
	public static String			level;
	public static String			experience;
	public static String			opened;
	public static String			yes;
	public static String			no;

	public static String			teleportionTimeRemaining;
	public static String			teleportationCanceled;

	public static void loadFromJson(JsonObject obj) {
		onLevelUp = getString("onLevelUp", obj, "Your faction reached the level %s. Now you can have %s chunks claimed.");
		adminPrefix = getString("adminPrefix", obj, "[ADMIN-MOD]");
		String colorName = getString("adminPrefixColor", obj, "RED");
		TextFormatting color = TextFormatting.getValueByName(colorName);
		if (color == null)
			color = TextFormatting.RED;
		adminPrefixColor = color;
		alreadyInAFaction = getString("alreadyInAFaction", obj, "You're already in a faction.");
		factionAlreadyExisting = getString("factionAlreadyExisting", obj, "The faction %s already exists.");
		factionNotExisting = getString("factionNotExisting", obj, "The faction %s doesn't exist.");
		factionCreated = getString("factionCreated", obj, "The faction %s was created.");
		notInAFaction = getString("notInAFaction", obj, "You're not in a faction.");
		notOwner = getString("notOwner", obj, "You're not the owner of this faction.");
		factionRemovedByOwner = getString("factionRemovedByOwner", obj, "The faction was removed by the owner.");
		factionRemoved = getString("factionRemoved", obj, "The faction %s was removed.");
		playerAlreadyInAFaction = getString("playerAlreadyInAFaction", obj, "This player is already in a faction.");
		notAMember = getString("notAMember", obj, "You're not a member of the faction %s.");
		missingPermission = getString("missingPermission", obj, "You don't have the permission to do that.");
		invitedToJoin = getString("invitedToJoin", obj, "You're invited to join the faction %s.");
		playerInvited = getString("playerInvited", obj, "The player is now invited in the faction %s.");
		playerNoLongerInvited = getString("playerNoLongerInvited", obj, "The player is no longer invited in the faction %s.");
		playerNotAMember = getString("playerNotAMember", obj, "This player is not a member of the faction %s.");
		noLongerAMember = getString("noLongerInTheFaction", obj, "You're no longer a member of the faction %s.");
		nowAMember = getString("nowAMember", obj, "You're now a member of the faction %s.");
		playerNoLongerAMember = getString("playerNoLongerAMember", obj, "The player %s is no longer a member of the faction %s.");
		descriptionChanged = getString("descriptionChanged", obj, "The description was changed.");
		playerJoinedFaction = getString("playerJoinedFaction", obj, "The player %s joined the faction.");
		notInvited = getString("notInvited", obj, "You're not invited in the faction %s.");
		canNotClaim = getString("canNotClaim", obj, "You can't claim this chunk.");
		maxChunksCountReached = getString("maxChunksCountReached", obj, "You can't claim more chunks.");
		chunkClaimed = getString("chunkClaimed", obj, "Chunk claimed !");
		chunkNotClaimed = getString("chunkNotClaimed", obj, "Chunk not claimed.");
		chunkUnclaimed = getString("chunkUnclaimed", obj, "Chunk unclaimed !");
		homeChanged = getString("homeChanged", obj, "The position of the home was changed.");
		factionNowOpened = getString("factionNowOpened", obj, "The faction %s is now opened.");
		factionNowClosed = getString("factionNowClosed", obj, "The faction %s is now closed.");
		hasNotHome = getString("hasNotHome", obj, "The faction %s hasn't a home");
		wrongDimension = getString("wrongDimension", obj, "You're in the wrong dimension.");
		willBeTeleportedToHome = getString("willBeTeleportedToHome", obj, "You will be teleported to the home of the faction %s.");
		notAvailableName = getString("notAvailableName", obj, "The name %s is not available.");
		wrongHierarchyLevel = getString("wrongHierarchyLevel", obj, "The level has to be higher than %s.");
		gradeSet = getString("gradeSet", obj, "The grade %s with level %s has permission(s) %s.");
		gradeNotExisting = getString("gradeNotExisting", obj, "The grade %s doesn't exist.");
		promoted = getString("promoted", obj, "You were promoted to %s.");
		playerPromoted = getString("playerPromoted", obj, "The player %s was promoted to %s.");
		gradeRemoved = getString("gradeRemoved", obj, "The grade %s was removed.");
		relationCreated = getString("relationCreated", obj, "The relation %s was created with the faction %s.");
		relationProposed = getString("relationProposed", obj, "The relation %s was proposed to your faction by the faction %s.");
		relationSent = getString("relationSent", obj, "You sent a request to the faction %s to the relation %s.");
		relationAccepted = getString("relationAccepted", obj, "You accepted the relation %s with the faction %s.");

		description = getString("description", obj, "Description");
		members = getString("members", obj, "Members");
		level = getString("level", obj, "Level");
		experience = getString("experience", obj, "Experience");
		opened = getString("opened", obj, "Opened");
		yes = getString("yes", obj, "yes");
		no = getString("no", obj, "no");

		teleportionTimeRemaining = getString("teleportionTimeRemaining", obj, "You will be teleported in %s seconds.");
		teleportationCanceled = getString("teleportationCanceled", obj, "Your teleportation was canceled.");
	}

	/**
	 * Reads the string with the speficied name in the given JsonObject. If it
	 * doesn't exist, it returns the default value.
	 * 
	 * @param name
	 *            The name of the value
	 * @param element
	 *            The JsonObject
	 * @param defaultValue
	 *            The default value
	 * @return the readed value or the default value
	 */
	private static String getString(String name, JsonObject element, String defaultValue) {
		if (element.has(name)) {
			JsonElement el = element.get(name);
			if (el.isJsonPrimitive()) {
				JsonPrimitive prim = el.getAsJsonPrimitive();
				if (prim.isString()) {
					return prim.getAsString();
				}
			}
		}
		return defaultValue;
	}

}
