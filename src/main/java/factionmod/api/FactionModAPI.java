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

public class FactionModAPI {

	public static class ExperienceAPI {

		/**
		 * Adds the specified amoutn of experience to the specified faction and
		 * updates the modded clients.
		 * 
		 * @param faction
		 *            The faction
		 * @param amount
		 *            The amount of experience
		 * @param The
		 *            UUID of the player who made the faction win the
		 *            experience, can be null
		 */
		public static void addExperience(Faction faction, int amount, UUID member) {
			EventHandlerExperience.addExp(faction, amount, member);
		}

		/**
		 * Sets the level and the experience of a faction. It update the modded
		 * clients. The level has to be greater than 0 and the experience equals
		 * to or greater than 0.
		 * 
		 * @param faction
		 *            The faction
		 * @param level
		 *            The level
		 * @param experience
		 *            The experience
		 */
		public static void setLevelAndExperience(Faction faction, int level, int experience) {
			if (level > 0 && experience >= 0) {
				faction.setExp(experience);
				faction.setLevel(level);
			}
		}

	}

	public static class FactionAPI {

		/**
		 * Indicates if a player has a faction.
		 * 
		 * @param uuid
		 *            The UUID of the player
		 * @return true if the player has a faction
		 */
		public static boolean hasPlayerFaction(UUID uuid) {
			return EventHandlerFaction.hasUserFaction(uuid);
		}

		/**
		 * Returns the faction with the specified name. If any faction has this
		 * name, it will return null.
		 * 
		 * @param name
		 *            The name of the faction.
		 * @return
		 */
		public static Faction getFaction(String name) {
			return EventHandlerFaction.getFaction(name);
		}

		/**
		 * Returns the faction of a player.
		 * 
		 * @param uuid
		 *            The UUID of the player
		 * @return the faction or null if the player hasn't any faction
		 */
		public static Faction getFactionOf(UUID uuid) {
			String name = EventHandlerFaction.getFactionOf(uuid);
			if (name.isEmpty())
				return null;
			return EventHandlerFaction.getFaction(name);
		}

		/**
		 * Returns a random faction or null if there is no faction registered.
		 * 
		 * @param rand
		 *            The object used the create the random number
		 * @return a faction or null
		 */
		public static Faction getRandomFaction(Random rand) {
			Collection<Faction> factions = EventHandlerFaction.getFactions().values();
			if (factions.isEmpty())
				return null;
			int index = rand.nextInt(factions.size());
			return factions.toArray(new Faction[0])[index];
		}

	}

	public static class ChunkAPI {

		/**
		 * Indicates if a chunk is managed by an instance of
		 * {@link IChunkManager}.
		 * 
		 * @param position
		 *            The position of the chunk
		 * @return true if the chunk is managed
		 */
		public static boolean isChunkManaged(DimensionalPosition position) {
			return EventHandlerChunk.getManagerFor(position) != null;
		}

		/**
		 * Returns the manager linked to the chunk at the specified position.
		 * Returns null if the chunk isn't managed.
		 * 
		 * @param position
		 *            The position of the chunk
		 * @return the manager or null
		 */
		public static IChunkManager getManagerFor(DimensionalPosition position) {
			return EventHandlerChunk.getManagerFor(position);
		}

		/**
		 * Removes the manager of a chunk.
		 * 
		 * @param position
		 *            The position of the chunk.
		 */
		public static void removeManager(DimensionalPosition position) {
			EventHandlerChunk.unregisterChunkManager(position, true);
		}

		/**
		 * Sets the specified manager for a chunk. It overrides the previous
		 * manager. You can use {@link ChunkManagerCreator} to create easily
		 * managers.
		 * 
		 * @param manager
		 *            The manager to set
		 * @param pos
		 *            The position of the chunk
		 * @param instance
		 *            The object which permits to retrieve the instance of the
		 *            manager
		 */
		public static void setManager(IChunkManager manager, DimensionalPosition pos, ZoneInstance instance) {
			EventHandlerChunk.registerChunkManager(manager, pos, instance, true);
		}

	}

}
