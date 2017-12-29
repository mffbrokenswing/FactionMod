package factionmod.api;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;

import factionmod.faction.Faction;
import factionmod.handler.EventHandlerChunk;
import factionmod.handler.EventHandlerExperience;
import factionmod.handler.EventHandlerFaction;
import factionmod.manager.IChunkManager;
import factionmod.manager.instanciation.ChunkManagerCreator;
import factionmod.manager.instanciation.ZoneInstance;
import factionmod.utils.DimensionalPosition;
import net.minecraft.world.chunk.Chunk;

public class FactionModAPI {

    public static class ExperienceAPI {

        /**
         * Adds the specified amoutn of experience to the specified {@link Faction}.
         *
         * @param faction
         *            The {@link Faction}
         * @param amount
         *            The amount of experience
         * @param member
         *            The UUID of the player who made the {@link Faction} win the
         *            experience, can be null
         */
        public static void addExperience(final Faction faction, final int amount, final UUID member) {
            EventHandlerExperience.addExp(faction, amount, member);
        }

        /**
         * Sets the level and the experience of a {@link Faction}. The level has to be
         * greater than 0 and the experience equals to or greater than 0.
         *
         * @param faction
         *            The {@link Faction}
         * @param level
         *            The level
         * @param experience
         *            The experience
         */
        public static void setLevelAndExperience(final Faction faction, final int level, final int experience) {
            if (level > 0 && experience >= 0) {
                faction.setExp(experience);
                faction.setLevel(level);
            }
        }

    }

    public static class FactionAPI {

        /**
         * Indicates if a player has a {@link Faction}.
         *
         * @param uuid
         *            The UUID of the player
         * @return true if the player has a {@link Faction}
         */
        public static boolean hasPlayerFaction(final UUID uuid) {
            return EventHandlerFaction.hasUserFaction(uuid);
        }

        /**
         * Returns the {@link Faction} with the specified name. If no {@link Faction}
         * has this name, it will return {@code null}.
         *
         * @param name
         *            The name of the {@link Faction}.
         * @return the {@link Faction} or {@code null}
         */
        public static Faction getFaction(final String name) {
            return EventHandlerFaction.getFaction(name);
        }

        /**
         * Returns the {@link Faction} of a player.
         *
         * @param uuid
         *            The UUID of the player
         * @return the {@link Faction} or {@code null} if the player hasn't any
         *         {@link Faction}
         */
        public static Faction getFactionOf(final UUID uuid) {
            final String name = EventHandlerFaction.getFactionOf(uuid);
            if (name.isEmpty())
                return null;
            return EventHandlerFaction.getFaction(name);
        }

        /**
         * Returns a random {@link Faction} or null if there is no {@link Faction}
         * registered.
         *
         * @param rand
         *            The object used the create the random number
         * @return a {@link Faction} or {@code null}
         */
        public static Faction getRandomFaction(final Random rand) {
            final Collection<Faction> factions = EventHandlerFaction.getFactions().values();
            if (factions.isEmpty())
                return null;
            final int index = rand.nextInt(factions.size());
            return factions.toArray(new Faction[0])[index];
        }

    }

    public static class ChunkAPI {

        /**
         * Indicates if a chunk at the specified {@link DimensionalPosition} is managed
         * by an instance of {@link IChunkManager}.
         *
         * @param position
         *            The {@link DimensionalPosition} of the chunk
         * @return true if the chunk is managed
         */
        public static boolean isChunkManaged(final DimensionalPosition position) {
            return EventHandlerChunk.getManagerFor(position) != null;
        }

        /**
         * Returns the {@link IChunkManager} linked to the {@link Chunk} at the
         * specified {@link DimensionalPosition}. Returns null if the {@link Chunk}
         * isn't managed.
         *
         * @param position
         *            The {@link DimensionalPosition} of the {@link Chunk}
         * @return the {@link IChunkManager} or null
         */
        public static IChunkManager getManagerFor(final DimensionalPosition position) {
            return EventHandlerChunk.getManagerFor(position);
        }

        /**
         * Removes the {@link IChunkManager} of a {@link Chunk}.
         *
         * @param position
         *            The {@link DimensionalPosition} of the {@link Chunk}.
         */
        public static void removeManager(final DimensionalPosition position) {
            EventHandlerChunk.unregisterChunkManager(position, true);
        }

        /**
         * Sets the specified {@link IChunkManager} for a {@link Chunk}. It overrides
         * the previous {@link IChinkManager}. You can use {@link ChunkManagerCreator}
         * to easily create {@link IChunkManager}s.
         *
         * @param manager
         *            The {@link IChunkManager} to set
         * @param pos
         *            The {@link DimensionalPosition} of the {@link Chunk}
         * @param instance
         *            The {@link ZoneInstance} which permits to retrieve the instance of
         *            the {@link IChunkManager}
         */
        public static void setManager(final IChunkManager manager, final DimensionalPosition pos, final ZoneInstance instance) {
            EventHandlerChunk.registerChunkManager(manager, pos, instance, true);
        }

    }

}
