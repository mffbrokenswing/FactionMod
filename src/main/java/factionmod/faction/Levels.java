package factionmod.faction;

/**
 * This class is used to know everything about the levels of a faction.
 * 
 * @author BrokenSwing
 *
 */
public class Levels {

	/**
	 * Indicates the amount of experience need to level up.
	 * 
	 * @param level
	 *            The level to reach
	 * @return the amount of experience needed
	 */
	public static int getExpNeededForLevel(int level) {
		return (int) (100 + (level * level * Math.log(level)) / 8);
	}

	/**
	 * Indicates the maximum of chunks that a faction can claim at a specified
	 * level.
	 * 
	 * @param level
	 *            The level of the faction
	 * @return the maximum count of chunks that can be claimed
	 */
	public static int getMaximumChunksForLevel(int level) {
		return (int) (1.3 * level + 3);
	}

}
