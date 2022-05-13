package dev.caledonian.commands;

import dev.caledonian.DarkBlueBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class Commands {

    private DarkBlueBot main;
    private JDA jda;
    public Commands(DarkBlueBot main, JDA jda) {
        this.main = main;
        this.jda = jda;
    }

    public void setupCommands(){
        Guild guild = jda.getGuildById(main.getConfig().getJSONObject("development").getLong("discord-id"));
        CommandListUpdateAction commands = guild.updateCommands();
        CommandListUpdateAction globalCommands = jda.updateCommands();

        commands.addCommands(new CommandData(
                "unlink", "DarkBlue: Unverify your account from our guild."
        )).queue();
    }
}
