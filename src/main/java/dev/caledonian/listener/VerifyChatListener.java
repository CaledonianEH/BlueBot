package dev.caledonian.listener;

import dev.caledonian.DarkBlueBot;
import dev.caledonian.managers.hypixel.HypixelPlayerGetter;
import dev.caledonian.utils.PremadeEmbeds;
import dev.caledonian.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.shanerx.mojang.Mojang;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VerifyChatListener extends ListenerAdapter {

    private DarkBlueBot main;
    private JDA jda;
    private HypixelPlayerGetter playerGetter;

    public VerifyChatListener(DarkBlueBot main, JDA jda, HypixelPlayerGetter playerGetter) {
        this.main = main;
        this.jda = jda;
        this.playerGetter = playerGetter;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        MessageChannel channel = jda.getTextChannelById(main.getConfig().getJSONObject("channels").getString("verify"));
        if (e.getChannel().getId().equals(channel.getId())) {
            try {
                User user = e.getAuthor();
                Mojang api = new Mojang().connect();
                String message = e.getMessage().getContentRaw();
                UUID playerUUID;
                if(user.isBot()) return;
                e.getMessage().delete().queue();
                try { playerUUID = Utils.convertToLongUUID(api.getUUIDOfUsername(message));
                }catch (Exception ex){
                    sendWarningMessage(e.getMessage(), "An issue was encountered while retrieving your account's info. Does it exist?");
                    return;
                }
                String username = playerGetter.getPlayerProfile(playerUUID).getString("displayname");

                if(playerGetter.playerExistsInAPI(playerUUID) && playerGetter.playerDiscordValid(user, playerUUID)){
                    if(playerGetter.getPLayerGiftedRanks(playerUUID) >= 100){
                        Role role = e.getGuild().getRoleById(main.getConfig().getJSONObject("roles").getString("darkBlue"));
                        e.getGuild().addRoleToMember(e.getMember(), role).queue();
                        e.getGuild().modifyNickname(e.getMember(), String.format("DB \u2022 %s", username)).queue();
                    }else {
                        sendWarningMessage(e.getMessage(), "You don't have the Dark Blue plus color!");
                    }
                }else {
                    sendLinkMessage(e.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                e.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                e.getMessage().replyEmbeds(PremadeEmbeds.error(String.format("%s\n\n- %s", ex.getMessage(), ex.getCause())).build())
                        .queue((result) -> {
                            Utils.deleteAfter(result, 15);
                        });
            }
        }
    }

    // Embeds
    private void sendLinkMessage(Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Utils.toHex("#FD4646"));
        eb.setTitle("Your account isn't linked!");
        eb.setDescription("Your Discord account isn't linked on hypixel, and so we can't verify that you own the minecraft account.\n\nFollow the instructions below, to link your account");
        eb.setThumbnail("https://i.imgur.com/LzRqVIy.png");
        eb.setFooter(main.getConfig().getString("footer-link"), main.getConfig().getString("footer-icon"));

        message.replyEmbeds(eb.build()).setActionRow(Button.secondary("linkDm", "Send me more info in my DMs")).queue((result) -> {
            Utils.deleteAfter(result, 15);
        });
    }

    private void sendWarningMessage(Message message, String reason) {
        message.replyEmbeds(PremadeEmbeds.warning(reason).build())
                .queue((result) -> {
                    Utils.deleteAfter(result, 10);
                });
    }
    private void sendErrorMessage(Message message, String reason) {
        message.replyEmbeds(PremadeEmbeds.error(reason).build())
                .queue((result) -> {
                    Utils.deleteAfter(result, 10);
                });
    }
}
