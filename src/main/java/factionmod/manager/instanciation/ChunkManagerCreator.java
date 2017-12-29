package factionmod.manager.instanciation;

import akka.japi.Pair;
import factionmod.handler.EventHandlerChunk;
import factionmod.manager.IChunkManager;

/**
 * This class provides an easy way to instance a IChunkManager from the zone's
 * name and the arguments.
 *
 * @author BrokenSwing
 *
 */
public class ChunkManagerCreator {

    /**
     * Creates an {@link IChunkManager} and a {@link ZoneInstance} from a
     * {@link Zone} name and arguments. If the instanciation doesn't take arguments,
     * give an empty array.
     * 
     * @param handlerName
     *            The name of the zone
     * @param args
     *            The arguments
     * @return The pair or null if a zone with the given name doesn't exist
     */
    public static Pair<IChunkManager, ZoneInstance> createChunkHandler(final String handlerName, final String... args) {
        final Zone zone = EventHandlerChunk.getZone(handlerName);
        if (zone != null)
            if (zone.isStandAlone())
                return Pair.<IChunkManager, ZoneInstance>apply(zone.getInstance(), new ZoneInstance(zone.getName(), new String[0]));
            else
                try {
                    return Pair.<IChunkManager, ZoneInstance>apply(zone.createInstance(args), new ZoneInstance(zone.getName(), args));
                } catch (final Exception e) {
                    e.printStackTrace();
                }
        return null;
    }

}
