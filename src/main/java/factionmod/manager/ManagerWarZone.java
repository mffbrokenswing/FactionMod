package factionmod.manager;

import java.util.List;

import factionmod.FactionMod;
import factionmod.handler.EventHandlerAdmin;
import factionmod.utils.OptionHelper;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

/**
 * Manages chunks where the players can fight without affecting the terrain.
 *
 * @author BrokenSwing
 *
 */
public class ManagerWarZone extends ChunkManager {

    public static final ManagerWarZone DEFAULT = new ManagerWarZone();

    @Override
    public void onBucketFill(final FillBucketEvent event) {
        if (!EventHandlerAdmin.isAdmin((EntityPlayerMP) event.getEntityPlayer()))
            event.setCanceled(true);
    }

    @Override
    public void onBreakBlock(final BreakEvent event) {
        if (!EventHandlerAdmin.isAdmin((EntityPlayerMP) event.getPlayer()))
            event.setCanceled(true);
    }

    @Override
    public void onPlaceBlock(final PlaceEvent event) {
        if (!EventHandlerAdmin.isAdmin((EntityPlayerMP) event.getPlayer()))
            event.setCanceled(true);
    }

    @Override
    public void onBlocksExplode(final World world, final List<BlockPos> blocks) {
        blocks.clear();
    }

    @Override
    public void onEntityJoin(final EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof EntityMob || entity instanceof EntityDragon || entity instanceof EntityAmbientCreature || entity instanceof EntityFlying || entity instanceof EntitySlime
                || entity instanceof EntityWaterMob)
            event.setCanceled(true);
    }

    private static int     enchantLevel           = 30;
    private static boolean enforceEnchantingLevel = true;

    @Override
    public void onEnchantmentLevelSet(final EnchantmentLevelSetEvent event) {
        event.setLevel(getEnchantLevel());
    }

    protected int getEnchantLevel() {
        return enchantLevel;
    }

    protected boolean enforceEnchantingLevel() {
        return enforceEnchantingLevel;
    }

    private static String displayedName = "* WarZone *";

    @Override
    public ITextComponent getName() {
        return new TextComponentString(displayedName).setStyle(new Style().setColor(TextFormatting.DARK_RED));
    }

    @Override
    public void handleParameters(final String parameters) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<String> nameOpt = parser.accepts("name", "The displayed text when entering in the chunk").withRequiredArg();
        final OptionSpec<Boolean> enforceEnchantOpt = parser.accepts("enforceEnchantmentLevel", "Indicates if the level of the enchantment should be enforced").withRequiredArg().ofType(Boolean.class);
        final OptionSpec<Integer> enchantLevelOpt = parser.accepts("enchantmentLevel", "The enchantment level which will be enforced").withRequiredArg().ofType(Integer.class);

        try {
            final OptionSet options = parser.parse(OptionHelper.separateOptions(parameters));
            if (options.has(nameOpt))
                displayedName = options.valueOf(nameOpt);

            if (options.has(enforceEnchantOpt))
                enforceEnchantingLevel = options.valueOf(enforceEnchantOpt).booleanValue();

            if (options.has(enchantLevelOpt))
                enchantLevel = options.valueOf(enchantLevelOpt).intValue();
        } catch (final Exception e) {
            FactionMod.getLogger().error("Error while handling parameters");
            e.printStackTrace();
        }
    }

}
