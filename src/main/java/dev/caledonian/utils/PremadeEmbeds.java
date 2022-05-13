package dev.caledonian.utils;

import dev.caledonian.DarkBlueBot;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

public class PremadeEmbeds {

    private static DarkBlueBot main;
    private static JDA jda;
    public PremadeEmbeds(DarkBlueBot main, JDA jda) {
        PremadeEmbeds.main = main;
        PremadeEmbeds.jda = jda;
    }

    @SneakyThrows
    public static EmbedBuilder error(String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Unexpected error!");
        eb.setColor(Utils.toHex("#FE3F3F"));
        eb.setDescription(String.format("Looks like %s has encountered an unknown error. Please report this to the developers in our [main discord](%s)\n\n```diff\n- %s\n```", jda.getSelfUser().getName(),
                main.getConfig().getString("support-discord"),
                cause));
        eb.setThumbnail("https://i.imgur.com/LzRqVIy.png");
        eb.setFooter(main.getConfig().getString("footer-link"), main.getConfig().getString("footer-icon"));

        return eb;
    }

    @SneakyThrows
    public static EmbedBuilder warning(String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Wait a minute!");
        eb.setColor(Utils.toHex("#FD4646"));
        eb.setDescription(String.format("Hey, wait a minute! Are you using this thing right? Get support in our [main discord](%s)\n\n```diff\n- %s\n```",
                main.getConfig().getString("support-discord"),
                cause));
        eb.setThumbnail("https://i.imgur.com/LzRqVIy.png");
        eb.setFooter(main.getConfig().getString("footer-link"), main.getConfig().getString("footer-icon"));

        return eb;
    }

    @SneakyThrows
    public static EmbedBuilder success(String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Success!");
        eb.setColor(Utils.toHex("#21DE3D"));
        eb.setDescription(String.format("%s",
                cause));
        eb.setThumbnail("https://i.imgur.com/fKjkvDX.png");
        eb.setFooter(main.getConfig().getString("footer-link"), main.getConfig().getString("footer-icon"));

        return eb;
    }

    @SneakyThrows
    public static EmbedBuilder success(String mesage, String cause){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Success!");
        eb.setColor(Utils.toHex("#21DE3D"));
        eb.setDescription(String.format("%s\n\n```diff\n+ %s\n```",
                mesage, cause));
        eb.setThumbnail("https://i.imgur.com/fKjkvDX.png");
        eb.setFooter(main.getConfig().getString("footer-link"), main.getConfig().getString("footer-icon"));

        return eb;
    }
}
