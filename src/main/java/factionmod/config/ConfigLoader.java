package factionmod.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import factionmod.FactionMod;
import factionmod.handler.EventHandlerChunk;
import factionmod.manager.ManagerFaction;
import factionmod.manager.ManagerSafeZone;
import factionmod.manager.ManagerWarZone;
import factionmod.manager.instanciation.Zone;
import factionmod.utils.ServerUtils;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.config.Configuration;

/**
 * The configuration of the mod, it loads and saves the state of the mod.
 *
 * @author BrokenSwing
 *
 */
public class ConfigLoader {

    /**
     * Loads a list of {@link Zone} from a file.
     *
     * @param fileName
     *            The name of the file
     */
    public static final void loadZones(final String fileName) {
        ServerUtils.getProfiler().startSection("loadZones");

        Zone z;
        final JsonElement file = getFile(fileName, false);
        if (file != null) {
            final JsonArray zones = file.getAsJsonArray();
            for (int i = 0; i < zones.size(); i++) {
                final JsonObject zone = zones.get(i).getAsJsonObject();
                final String name = JsonUtils.getString(zone, "name");
                final String clazz = JsonUtils.getString(zone, "class");
                final String instance = JsonUtils.getString(zone, "instance", "");
                final String parameters = JsonUtils.getString(zone, "parameters", "");
                try {
                    if (!instance.isEmpty())
                        z = new Zone(name, clazz, instance, parameters);
                    else
                        z = new Zone(name, clazz, parameters);
                    EventHandlerChunk.registerZone(z);
                } catch (final Exception e) {
                    FactionMod.getLogger().error("Couldn't load zone " + name);
                    e.printStackTrace();
                }
            }
        }

        ServerUtils.getProfiler().endSection();
    }

    public static final String CONFIG_VERSION = "v2";

    /**
     * Loads the configuration using the Forge system.
     */
    private static void loadConfigurationFile() {
        final Configuration cfg = new Configuration(new File(FactionMod.getConfigDir(), "configuration.cfg"), CONFIG_VERSION, false);

        ConfigGeneral.loadFromConfig(cfg);
        ConfigExperience.loadFromConfig(cfg);
        ConfigLang.loadFromConfig(cfg);
        ConfigFactionInventory.loadFromConfig(cfg);
        ConfigHtml.loadFromConfig(cfg);
        ConfigChat.loadFromConfig(cfg);

        cfg.save();
    }

    /**
     * Loads all the parameters from the configuration file.
     */
    public static void loadConfigFile() {
        ServerUtils.getProfiler().startSection("loadConfiguration");

        JsonElement element = getFile("configuration.json", true);
        if (element == null) {
            loadConfigurationFile();
            return;
        }

        FactionMod.getLogger().warn("###############################################");
        FactionMod.getLogger().warn("###############################################");
        FactionMod.getLogger().warn("########            WARNING            ########");
        FactionMod.getLogger().warn("###############################################");
        FactionMod.getLogger().warn("###############################################");
        FactionMod.getLogger().warn("");
        FactionMod.getLogger().warn("You should use the new configuration system. Delete the old 'configuration.json' file and let the new 'configuration.cfg' file being generated.");
        FactionMod.getLogger().warn("");
        FactionMod.getLogger().warn("");

        if (!element.isJsonObject())
            element = new JsonObject();

        JsonObject expObj = new JsonObject();
        JsonObject languageObj = new JsonObject();
        JsonArray chestArray = new JsonArray();

        JsonElement el;
        final JsonObject root = element.getAsJsonObject();

        if (root.has("exp")) {
            el = root.get("exp");
            if (el.isJsonObject())
                expObj = el.getAsJsonObject();
        }
        if (root.has("language")) {
            el = root.get("language");
            if (el.isJsonObject())
                languageObj = el.getAsJsonObject();
        }
        if (root.has("unvalidItems")) {
            el = root.get("unvalidItems");
            if (el.isJsonArray())
                chestArray = el.getAsJsonArray();
        }

        ConfigGeneral.loadFromJson(root);
        ConfigExperience.loadFromJson(expObj);
        ConfigLang.loadFromJson(languageObj);
        ConfigFactionInventory.loadFromJson(chestArray);

        ServerUtils.getProfiler().endSection();
    }

    /**
     * Loads a file from the config directory. Will return null if the file doesn't
     * exist.
     *
     * @param fileName
     *            The name of the file
     * @param fromConfigDir
     *            Set it to true if you want the path to be relative to the faction
     *            mod config directoru
     * @return a {@link JsonElement} or null if the file doesn't exist
     */
    private static JsonElement getFile(final String fileName, final boolean fromConfigDir) {
        final File file = fromConfigDir ? new File(FactionMod.getConfigDir(), fileName) : new File(fileName);
        if (file.exists())
            try {
                return new JsonParser().parse(new FileReader(file));
            } catch (final Exception e) {
                FactionMod.getLogger().error("Error on loading file " + fileName);
                e.printStackTrace();
            }
        return null;
    }

    /**
     * Writes text in a file.
     *
     * @param fileName
     *            The name of the file
     * @param contents
     *            The text to write
     */
    private static void writeFile(final String fileName, final String contents) {
        final File file = new File(FactionMod.getConfigDir() + "/" + fileName);
        try {
            file.createNewFile();

            final FileWriter writer = new FileWriter(file);
            writer.write(contents);
            writer.flush();
            writer.close();
        } catch (final IOException e) {
            FactionMod.getLogger().error("Error on writing file " + fileName);
            e.printStackTrace();
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Creates the directory containing the configuration files of the mod. Creates
     * the files zones.json if it doesn't exist.
     */
    public static void initDirectory() {
        final File dir = new File(FactionMod.getConfigDir());
        if (!dir.exists())
            dir.mkdirs();
        final File file = new File(FactionMod.getConfigDir() + "/zones.json");
        if (!file.exists()) {
            ServerUtils.getProfiler().startSection("generateDefaultZonesFile");
            final JsonArray root = new JsonArray();
            final JsonObject safeZone = new JsonObject();
            safeZone.add("name", new JsonPrimitive("safe"));
            safeZone.add("class", new JsonPrimitive(ManagerSafeZone.class.getName()));
            safeZone.add("instance", new JsonPrimitive("DEFAULT"));

            final JsonObject factionZone = new JsonObject();
            factionZone.add("name", new JsonPrimitive("faction"));
            factionZone.add("class", new JsonPrimitive(ManagerFaction.class.getName()));

            final JsonObject warZone = new JsonObject();
            warZone.add("name", new JsonPrimitive("war"));
            warZone.add("class", new JsonPrimitive(ManagerWarZone.class.getName()));
            warZone.add("instance", new JsonPrimitive("DEFAULT"));

            root.add(safeZone);
            root.add(warZone);
            root.add(factionZone);

            writeFile("zones.json", GSON.toJson(root));
            ServerUtils.getProfiler().endSection();
        }
    }

}
