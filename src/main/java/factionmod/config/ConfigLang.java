package factionmod.config;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import factionmod.utils.ServerUtils;
import net.minecraftforge.common.config.Configuration;

public class ConfigLang {

    private static final HashMap<String, String> TRANSLATIONS = new HashMap<>();

    private static HashMap<String, String> init() {
        final HashMap<String, String> translations = new HashMap<>();
        translations.put("admin.prefix", "[F-ADMIN]");

        translations.put("faction.levelup", "Your faction reached the level %s. Now you can have %s chunks claimed.");
        translations.put("faction.create.name.taken", "The faction %s already exists.");
        translations.put("faction.create.name.free", "The name %s is unused.");
        translations.put("faction.create.name.error.length", "The maximum length for the name of the faction is %s.");
        translations.put("faction.inexisting", "The faction %s doesn't exist.");
        translations.put("faction.create.success", "The faction %s was created.");
        translations.put("faction.removed.owner", "The faction was removed by the owner.");
        translations.put("faction.removed.other", "The faction was removed by %s.");
        translations.put("faction.disband.success", "The faction %s was removed.");
        translations.put("faction.invite.success.invited", "The player is now invited in the faction %s.");
        translations.put("faction.invite.success.notinvited", "The player is no longer invited in the faction %s.");
        translations.put("faction.desc.changed", "The description was changed.");
        translations.put("faction.desc.warn.length", "The description was truncated. The maximum length is %s.");
        translations.put("faction.claim.fail.nothere", "You can't claim this chunk.");
        translations.put("faction.claim.fail.maxreached", "You can't claim more chunks.");
        translations.put("faction.claim.success", "Chunk claimed !");
        translations.put("faction.chunk.notclaimed", "Chunk not claimed.");
        translations.put("faction.unclaim.success", "Chunk unclaimed !");
        translations.put("faction.home.location.changed", "The position of the home was changed.");
        translations.put("faction.opened.yes", "The faction %s is now opened.");
        translations.put("faction.opened.no", "The faction %s is now closed.");
        translations.put("faction.home.inexisting", "The faction %s hasn't a home");
        translations.put("faction.home.success.start", "You will be teleported to the home of the faction %s.");
        translations.put("faction.grade.name.taken", "The name %s is not available.");
        translations.put("faction.grade.level.invalid", "The level has to be higher than %s.");
        translations.put("faction.grade.set.success", "The grade %s with level %s has permission(s) %s.");
        translations.put("faction.grade.inexisting", "The grade %s doesn't exist.");
        translations.put("faction.grade.remove.success", "The grade %s was removed.");
        translations.put("faction.chest.displayed", "Chest of the faction %s displayed.");
        translations.put("faction.chest.name", "%s");
        translations.put("faction.link.recruit.changed", "The recruit link was changed.");
        translations.put("faction.join.recruit", "You're not invited in the faction, but you can follow this link to be recruited : %s");

        translations.put("player.self.faction.has", "You're already in a faction.");
        translations.put("player.self.faction.hasnot", "You're not in a faction.");
        translations.put("player.self.member.grade.owner.isnt", "You're not the owner of this faction.");
        translations.put("player.self.member.isnt", "You're not a member of the faction %s.");
        translations.put("player.self.permission.hasnt", "You don't have the permission to do that.");
        translations.put("player.self.invitation.received", "You're invited to join the faction %s.");
        translations.put("player.self.invitation.hasnt", "You're not invited in the faction %s.");
        translations.put("player.self.member.nolonger", "You're no longer a member of the faction %s.");
        translations.put("player.self.member.became", "You're now a member of the faction %s.");
        translations.put("player.self.location.dim.wrong", "You're in the wrong dimension.");
        translations.put("player.self.member.grade.promoted", "You were promoted to %s.");
        translations.put("player.self.admin.nolonger", "You're no longer admin");
        translations.put("player.self.admin.became", "You're now admin. This status will be removed if you disconnect.");

        translations.put("player.other.faction.has", "This player is already in a faction.");
        translations.put("player.other.member.isnt", "This player is not a member of the faction %s.");
        translations.put("player.other.member.nolonger", "The player %s is no longer a member of the faction %s.");
        translations.put("player.other.faction.joined", "The player %s joined the faction.");
        translations.put("player.other.member.grade.promoted", "The player %s was promoted to %s.");
        translations.put("player.other.admin.became", "The player %s is now admin.");
        translations.put("player.other.admin.nolonger", "The player %s is no longer admin.");

        translations.put("lang.description", "Description");
        translations.put("lang.members", "Members");
        translations.put("lang.level", "Level");
        translations.put("lang.experience", "Experience");
        translations.put("lang.opened", "Opened");
        translations.put("lang.damages", "Damages");
        translations.put("lang.yes", "yes");
        translations.put("lang.no", "no");

        translations.put("teleportation.time.remaining", "You will be teleported in %s seconds.");
        translations.put("teleportation.canceled", "Your teleportation was canceled.");
        
        translations.put("message.no.target", "Your message hasn't any target.");

        return translations;
    }

    /**
     * Translates the given key. If it doesn't exist, it returns the key.
     *
     * @param key
     *            The key to translate
     * @return the translation
     */
    public static String translate(final String key) {
        return TRANSLATIONS.containsKey(key) ? TRANSLATIONS.get(key) : key;
    }

    /**
     * Loads the configuration of the language from the JSON configuration file.
     *
     * @param obj
     *            The language JSON object
     */
    public static void loadFromJson(final JsonObject obj) {
        ServerUtils.getProfiler().startSection("language");

        TRANSLATIONS.clear();
        init().forEach(TRANSLATIONS::put);

        for (final Entry<String, JsonElement> entry : obj.entrySet())
            if (entry.getValue().isJsonPrimitive()) {
                final JsonPrimitive prim = entry.getValue().getAsJsonPrimitive();
                if (prim.isString())
                    TRANSLATIONS.put(entry.getKey(), prim.getAsString());
            }

        ServerUtils.getProfiler().endSection();
    }

    private static final String CAT = "language";

    /**
     * Loads the configuration for the language from the configuration provided by
     * Forge system.
     *
     * @param config
     *            The configuration
     */
    public static void loadFromConfig(final Configuration config) {
        ServerUtils.getProfiler().startSection("language");

        config.setCategoryComment(CAT, "This section contains all the translation keys of the mod. It permits you to modify messages sended to players");

        TRANSLATIONS.clear();
        final HashMap<String, String> temp = init();
        temp.forEach((key, value) -> TRANSLATIONS.put(key, config.get(CAT, key, value).getString()));

        ServerUtils.getProfiler().endSection();
    }

}
