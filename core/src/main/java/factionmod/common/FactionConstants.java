package factionmod.common;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

public class FactionConstants {

    public static final String MOD_ID = "faction";
    public static final String MOD_NAME = "Faction mod";
    public static final String VERSION = "${version}";

    @Mod.Instance(value = MOD_ID, owner = MOD_ID)
    public static Object MOD_INSTANCE;
    public static Logger LOGGER;

}
