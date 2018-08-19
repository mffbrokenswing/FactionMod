package factionmod.common.config;

import factionmod.common.FactionConstants;
import net.minecraftforge.common.config.Config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

@Config(modid = FactionConstants.MOD_ID)
public class FactionConfig {

    static {
        Stream.of(FactionConfig.class.getDeclaredFields()).forEach(FactionConfig::createRow);
    }

    public static void createRow(Field field) {
        if(field.getModifiers() == (Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC)) {

        }
    }

}
