package net.irisshaders.lilybot.commands.moderation;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.irisshaders.lilybot.utils.Constants;
import net.irisshaders.lilybot.utils.ResponseHelper;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class Unban extends SlashCommand {

    public Unban() {
        name = "unban";
        help = "Unbans a user. Hello again!";
        defaultEnabled = false;
        enabledRoles = new String[]{Constants.MODERATOR_ROLE};
        guildOnly = true;
        botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        botMissingPermMessage = "The bot does not have the `BAN MEMBERS` permission.";
        List<OptionData> optionData = new ArrayList<>();
        optionData.add(new OptionData(OptionType.USER, "id", "The ID of the user to unban.").setRequired(true));
        this.options = optionData;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        TextChannel actionLog = event.getGuild().getTextChannelById(Constants.ACTION_LOG);
        Guild guild = event.getGuild();
        User userById = event.getOption("id").getAsUser();
        User user = event.getUser();

        guild.unban(userById).queue(unused -> {

            MessageEmbed unbanEmbed = new EmbedBuilder()
                    .setTitle("Unbanned a member.")
                    .addField("Unbanned:", userById.getAsTag(), false)
                    .setColor(Color.CYAN)
                    .setFooter("Requested by " + user.getAsTag(), user.getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .build();

            event.replyEmbeds(unbanEmbed).mentionRepliedUser(false).setEphemeral(true).queue();
            actionLog.sendMessageEmbeds(unbanEmbed).queue();

        }, throwable -> event.replyEmbeds(ResponseHelper.genFailureEmbed(user, "Failed to unban.", null))
                .mentionRepliedUser(false).setEphemeral(true).queue()
        );

    }

}
