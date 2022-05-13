package dev.caledonian.managers.hypixel;

import dev.caledonian.DarkBlueBot;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class HypixelPlayerGetter {

    private DarkBlueBot main;
    private JDA jda;
    public HypixelPlayerGetter(DarkBlueBot main, JDA jda) {
        this.main = main;
        this.jda = jda;
    }

    public JSONObject getPlayerSocialMedia(UUID uuid){
        return getPlayerProfile(uuid).getJSONObject("socialMedia").getJSONObject("links");
    }
    public int getPLayerGiftedRanks(UUID uuid){
        if(getPlayerProfile(uuid).has("giftingMeta")){
            if(getPlayerProfile(uuid).getJSONObject("giftingMeta").has("ranksGiven")){
                return getPlayerProfile(uuid).getJSONObject("giftingMeta").getInt("ranksGiven");
            }
        }
        return 0;
    }
    public boolean playerDiscordValid(User user, UUID uuid){
        try{
            JSONObject playerSocial = getPlayerSocialMedia(uuid);
            if(!playerSocial.has("DISCORD")){return false;}
            return jda.getUserByTag(playerSocial.getString("DISCORD")).equals(user);
        }catch (Exception ex){
            ex.printStackTrace();
            return false;}
    }

    // Utils
    // Opens a connection to the API, and converts the resulting string into a JSONObject
    @SneakyThrows
    public JSONObject getPlayerProfile(UUID uuid){
        URL url = new URL(String.format("https://api.hypixel.net/player?key=%s&uuid=%s",
                main.getHypixelAPIKey().toString(),
                uuid.toString()));

//        url.openConnection();
        InputStream is = url.openConnection().getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = br.readLine();

        return new JSONObject(line).getJSONObject("player");
    }

    // Opens a connection to the API, converts the resulting string into a JSONObject, then returning the result of "success". False if theres an error
    @SneakyThrows
    public boolean playerExistsInAPI(UUID uuid){
        URL url = new URL(String.format("https://api.hypixel.net/player?key=%s&uuid=%s",
                main.getHypixelAPIKey().toString(),
                uuid.toString()));

//        url.openConnection();
        InputStream is = url.openConnection().getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = br.readLine();
        JSONObject object = new JSONObject(line);

        return object.getBoolean("success");
    }
}
