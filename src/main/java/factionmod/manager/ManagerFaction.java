package factionmod.manager;

import factionmod.FactionMod;
import factionmod.config.ConfigGeneral;
import factionmod.faction.Faction;
import factionmod.handler.EventHandlerFaction;
import factionmod.utils.OptionHelper;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

/**
 * Manages chunks claimed by factions.
 *
 * @author BrokenSwing
 *
 */
public class ManagerFaction extends ChunkManager {

    private final Faction faction;

    public ManagerFaction(final String[] factionName) {
        faction = EventHandlerFaction.getFaction(factionName[0]);
        if (faction == null)
            FactionMod.getLogger().warn("The server will certainly crash.");
    }

    public Faction getFaction() {
        return faction;
    }

    @Override
    public void onBucketFill(final FillBucketEvent event) {
        if (!isInFaction(event.getEntityPlayer()))
            event.setCanceled(true);
    }

    @Override
    public void onBreakBlock(final BreakEvent event) {
        if (!isInFaction(event.getPlayer()))
            event.setCanceled(true);
    }

    @Override
    public void onPlaceBlock(final PlaceEvent event) {
        if (!isInFaction(event.getPlayer()))
            event.setCanceled(true);
    }

    @Override
    public void onPlayerRightClickBlock(final RightClickBlock event) {
        if (!isInFaction(event.getEntityPlayer()))
            event.setCanceled(true);
    }

    @Override
    public void onPlayerAttack(final AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPlayer)
            if (faction.isMember(event.getEntityPlayer().getUniqueID()) || faction.isMember(event.getTarget().getUniqueID()))
                if (faction.getLevel() < ConfigGeneral.getInt("immunity_level"))
                    event.setCanceled(true);
    }

    private String displayedName = "--- %s ---";

    @Override
    public ITextComponent getName() {
        return new TextComponentString(String.format(displayedName, this.faction.getName()));
    }

    private boolean isInFaction(final EntityPlayer player) {
        return faction.isMember(player.getUniqueID());
    }

    @Override
    public void handleParameters(final String parameters) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<String> nameOpt = parser.accepts("name", "The displayed text when entering in the chunk").withRequiredArg();

        try {
            final OptionSet options = parser.parse(OptionHelper.separateOptions(parameters));
            if (options.has(nameOpt))
                displayedName = options.valueOf(nameOpt);
        } catch (final Exception e) {
            FactionMod.getLogger().error("Error while handling parameters");
            e.printStackTrace();
        }
    }

}
