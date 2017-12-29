package factionmod.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import factionmod.FactionMod;
import factionmod.command.utils.UUIDHelper;
import factionmod.config.ConfigHtml;
import factionmod.config.ConfigLang;
import factionmod.event.FactionsLoadedEvent;
import factionmod.faction.Faction;
import factionmod.faction.Grade;
import factionmod.faction.Levels;
import factionmod.faction.Member;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * It handles the generation of the html files for the description of the
 * factions.
 *
 * @author BrokenSwing
 *
 */
@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerHtml {

    private static int tickCount = 0;

    @SubscribeEvent
    public static void serverTick(final ServerTickEvent event) {
        if (tickCount >= ConfigHtml.getInt("update_period")) {
            tickCount = 0;
            EventHandlerFaction.getFactions().values().forEach(faction -> generateFile(faction));
        } else
            tickCount++;
    }

    @SubscribeEvent
    public static void factionsLoaded(final FactionsLoadedEvent event) {
        EventHandlerFaction.getFactions().values().forEach(faction -> generateFile(faction));
    }

    private static void generateFile(final Faction faction) {
        if (!ConfigHtml.getBool("enable_html_generation"))
            return;

        final File templateFile = new File(ConfigHtml.getString("template_path"));
        if (!templateFile.exists())
            return;

        String template = "";

        try {
            template = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            FactionMod.getLogger().error("Error while reading template file : " + templateFile.getAbsolutePath());
            e.printStackTrace();
            return;
        }

        template = template.replaceAll("%faction-name%", faction.getName());
        template = template.replaceAll("%faction-description%", faction.getDesc());
        template = template.replaceAll("%members-count%", "" + faction.getMembers().size());
        template = template.replaceAll("%recruit-link%", faction.getRecruitLink());
        template = template.replaceAll("%is-opened%", faction.isOpened() ? ConfigLang.translate("lang.yes") : ConfigLang.translate("lang.no"));
        template = template.replaceAll("%faction-level%", "" + faction.getLevel());
        template = template.replaceAll("%faction-experience%", "" + faction.getExp());
        template = template.replaceAll("%faction-experience-needed%", "" + Levels.getExpNeededForLevel(faction.getLevel() + 1));
        template = template.replaceAll("%faction-damages%", "" + faction.getDamages());

        int start = template.indexOf("%start-member%");
        int end = template.indexOf("%end-member%");
        if (start != -1 && end != -1) {
            String totalSection = "";
            String memberSection = template.substring(start, end + "%end-member%".length());
            template = template.replace(memberSection, "");
            memberSection = memberSection.replace("%start-member%", "").replace("%end-member%", "");

            final List<Member> members = faction.getMembers();
            Collections.sort(members);

            for (final Member member : members) {
                String current = memberSection.replaceAll("%name%", UUIDHelper.getNameOf(member.getUUID()));
                current = current.replaceAll("%grade%", member.getGrade().getName());
                current = current.replaceAll("%experience-earned%", "" + member.getExperience());
                current = current.replaceAll("%uuid%", member.getUUID().toString());

                totalSection += current;
            }
            template = template.replace("%member-section%", totalSection);
        }

        start = template.indexOf("%start-grade%");
        end = template.indexOf("%end-grade%");
        if (start != -1 && end != -1) {
            String totalSection = "";
            String gradeSection = template.substring(start, end + "%end-grade%".length());
            template = template.replace(gradeSection, "");
            gradeSection = gradeSection.replace("%start-grade%", "").replace("%end-grade%", "");

            final List<Grade> grades = Lists.newArrayList(faction.getGrades());
            grades.add(Grade.OWNER);
            grades.add(Grade.MEMBER);

            Collections.sort(grades);

            for (final Grade grade : grades) {
                String current = gradeSection.replaceAll("%name%", grade.getName());
                current = current.replaceAll("%permissions", grade.getPermissionsAsString());
                current = current.replaceAll("%priority%", grade.getPriority() + "");
                totalSection += current;
            }
            template = template.replace("%grade-section%", totalSection);
        }

        final File output = new File(ConfigHtml.getString("output_directory") + File.separator + faction.getName() + ".html");
        try {
            FileUtils.write(output, template, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            FactionMod.getLogger().error("Error while writing into file : " + output.getAbsolutePath());
            e.printStackTrace();
        }
    }

}
