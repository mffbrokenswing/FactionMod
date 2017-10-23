package factionmod.imc;

import factionmod.config.ConfigLoader;
import factionmod.utils.ServerUtils;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCHandler {

    public static void handleMessage(IMCMessage message) {
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

    private static void handleStringMessage(IMCMessage message) {
        String key = message.key.trim();
        String value = message.getStringValue().trim();
        if (value.isEmpty())
            return;

        // Loads zones from a specified file
        if (key.equals("zone-file")) {
            ServerUtils.getProfiler().startSection("configuration");
            ConfigLoader.loadZones(value);
            ServerUtils.getProfiler().endSection();
        }
    }

    private static void handleItemStackMessage(IMCMessage message) {

    }

    private static void handleFunctionMessage(IMCMessage message) {

    }

    private static void handleNBTMessage(IMCMessage message) {

    }

    private static void handleResourceLocationMessage(IMCMessage message) {

    }

}
