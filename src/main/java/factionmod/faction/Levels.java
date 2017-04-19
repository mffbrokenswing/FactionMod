package factionmod.faction;

/**
 * This class is used to know everything about the levels of a faction.
 * 
 * @author BrokenSwing
 *
 */
public class Levels {

	public static int getExpNeededForLevel(int level) {
		return (int) (100 + (level * level * Math.log(level)) / 8);
	}

	public static int getMaximumChunksForLevel(int level) {
		return (int) (1.3 * level + 3);
	}

}
