package dev.caledonian;

import dev.caledonian.commands.Commands;
import dev.caledonian.commands.UnlinkCommand;
import dev.caledonian.listener.VerifyChatListener;
import dev.caledonian.managers.hypixel.HypixelPlayerGetter;
import dev.caledonian.utils.PremadeEmbeds;
import dev.caledonian.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class DarkBlueBot {

    private JDA jda;
    @Getter
    private HypixelAPI hypixelAPI;
    @Getter
    @Setter
    private UUID hypixelAPIKey;
    private long time = 0;
    private static long startTime;

    @Getter
    public HypixelPlayerGetter playerGetter;

    public static void main(String[] args) {
        DarkBlueBot main = new DarkBlueBot();
        main.startBot();
    }

    public void startBot() {
        time = System.currentTimeMillis();
        Utils.sendConsoleLog("[BOT] Loading DarkBlueBot by Caledonian");

        // Loading configuration to a JSON object
        try {
            Objects.requireNonNull(getConfig()).getString("token");
            Utils.sendConsoleLog("[CFG] Successfully loaded config.json into memory. Took %sms",
                    System.currentTimeMillis() - time);
        } catch (Exception ex) {
            Utils.sendConsoleLog("[CFG] Failed to find config.json, cannot start bot.");
            return;
        }

        Utils.sendConsoleLog("[BOT] Connecting to the bot using the provided token.");
        time = System.currentTimeMillis();
        try {
            JDABuilder builder = JDABuilder.createDefault(getConfig().getString("token"))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL);

            // Setup sharding
//            for(int i = 0; i < 10; i++){
//                builder.useSharding(i, 10);
//            }
            jda = builder.build().awaitReady();
            // .build().awaitReady()
            Utils.sendConsoleLog("[BOT] Successfully connected to %s with a valid token. Took %sms", jda.getSelfUser().getAsTag(),
                    System.currentTimeMillis() - time);
        } catch (LoginException ex) {
            Utils.sendConsoleLog("[BOT] [FAILED] [LoginException] Failed to validate the provided token. Failed in %sms", System.currentTimeMillis() - time);
        } catch (InterruptedException ex) {
            Utils.sendConsoleLog("[BOT] [FAILED] [InterruptedExcpetion] Task was interrupted whilst connecting. Failed in %sms", System.currentTimeMillis() - time);
        }

        // Activities
        Utils.sendConsoleLog("[BOT] Registering cosmetic status for %s", jda.getSelfUser().getAsTag());
        time = System.currentTimeMillis();
        jda.getPresence().setStatus(OnlineStatus.fromKey(getConfig().getJSONObject("activity").getString("status")));
        jda.getPresence().setActivity(Activity.competing(getConfig().getJSONObject("activity").getString("activity")));
        Utils.sendConsoleLog("[BOT] Successfully set status for %s", jda.getSelfUser().getAsTag());

        // Registering Events & Managers
        Utils.sendConsoleLog("[BOT] Registering command classes");
        time = System.currentTimeMillis();
        setupHypixelAPI();
        new PremadeEmbeds(this, jda);
        Commands commands = new Commands(this, jda);
        commands.setupCommands();
        playerGetter = new HypixelPlayerGetter(this, jda);
        registerEvents();

        Utils.sendConsoleLog("[BOT] Successfully registered command classes. Took %sms", System.currentTimeMillis() - time);
    }

    public void registerEvents(){
        jda.addEventListener(new VerifyChatListener(this, jda, playerGetter));
        jda.addEventListener(new UnlinkCommand(this, jda));
    }

    // Hypixel
    public void setupHypixelAPI(){
        UUID key = UUID.fromString(getConfig().getJSONObject("hypixel").getString("key"));
        setHypixelAPIKey(key);
        hypixelAPI = new HypixelAPI(new ApacheHttpClient(key));

    }

    // Configuration
    @SneakyThrows
    public JSONObject getConfig() {
        try {
            JSONObject config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
            return config;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
