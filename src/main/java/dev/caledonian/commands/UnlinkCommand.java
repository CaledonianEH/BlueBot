package dev.caledonian.commands;

import dev.caledonian.DarkBlueBot;
import dev.caledonian.utils.PremadeEmbeds;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnlinkCommand extends ListenerAdapter {

    private DarkBlueBot main;
    private JDA jda;
    public UnlinkCommand(DarkBlueBot main, JDA jda) {
        this.main = main;
        this.jda = jda;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent e){
        if(e.getName().equalsIgnoreCase("unlink")){
            User user = e.getUser();
            Member member = e.getMember();
            Guild guild = e.getGuild();
            try {
                e.deferReply().queue();
                Role role = e.getGuild().getRoleById(main.getConfig().getJSONObject("roles").getString("darkBlue"));
                if(member.getRoles().contains(role)){
                    guild.modifyNickname(member, user.getName()).queue();
                    guild.removeRoleFromMember(member, role).queue();

                    e.getHook().sendMessageEmbeds(PremadeEmbeds.success("Successfully unlinked your account from the DarkBlue discord server.").build()).setEphemeral(true).queue();
                }else {
                    e.getHook().sendMessageEmbeds(PremadeEmbeds.warning("You don't have the DarkBlue role, or haven't linked in the first place.").build()).setEphemeral(true).queue();
                }
            }catch (Exception ex){
                ex.printStackTrace();
                e.replyEmbeds(PremadeEmbeds.error(ex.getMessage()).build()).queue();
            }
        }
    }
}
