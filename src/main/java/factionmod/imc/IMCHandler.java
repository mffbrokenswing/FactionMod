package factionmod.imc;

import factionmod.config.ConfigLoader;
import factionmod.utils.ServerUtils;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCHandler {

    public static void handleMessage(final IMCMessage message) {
        if (message.key.trim().isEmpty())
            return;

        if (message.isStringMessage())
            handleStringMessage(message);
        else if (message.isItemStackMessage())
            handleItemStackMessage(message);
        else if (message.isFunctionMessage())
            handleFunctionMessage(message);
        else if (message.isNBTMessage())
            handleNBTMessage(message);
        else if (message.isResourceLocationMessage())
            handleResourceLocationMessage(message);

    }

    private static void handleStringMessage(final IMCMessage message) {
        final String key = message.key.trim();
        final String value = message.getStringValue().trim();
        if (value.isEmpty())
            return;

        // Loads zones from a specified file
        if (key.equals("zone-file")) {
            ServerUtils.getProfiler().startSection("configuration");
            ConfigLoader.loadZones(value);
            ServerUtils.getProfiler().endSection();
        }
    }

    private static void handleItemStackMessage(final IMCMessage message) {

    }

    private static void handleFunctionMessage(final IMCMessage message) {

    }

    private static void handleNBTMessage(final IMCMessage message) {

    }

    private static void handleResourceLocationMessage(final IMCMessage message) {

    }

}
