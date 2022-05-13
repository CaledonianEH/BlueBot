/*
 * Copyright (c) 2/26/22, 8:55 PM, Caledonian. (https://www.caledonian.dev)
 *
 * Not intended for distribution without explicit permission from the original author.
 */

package dev.caledonian.utils;

import dev.caledonian.DarkBlueBot;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONArray;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static DarkBlueBot main;
    private static JDA jda;

    public Utils(DarkBlueBot main, JDA jda){
        this.main = main;
        this.jda = jda;
    }

    @SneakyThrows
    public static boolean isDeveloper(User user){
        return developers().contains(user.getId());
    }

    @SneakyThrows
    public static List<String> developers(){
        ArrayList<String> listdata = new ArrayList<>();
        JSONArray jsonArray = main.getConfig().getJSONObject("development").getJSONArray("developers");
        if(jsonArray != null){
            for (int i=0;i<jsonArray.length();i++){
                listdata.add(jsonArray.getString(i));
            }
        }

        return listdata;
    }

    public static void deleteAfter(Message message, int delay){
        message.delete().queueAfter(delay, TimeUnit.SECONDS);
    }

    public static String getLogTime(){
        String timeStamp;
        timeStamp = new SimpleDateFormat("M/d/yy hh:mm:ss:SSSS z").format(new Date());

        return String.format("[%s] ", timeStamp);
    }

    public static Color toHex(String colorStr) {
        String finalColorString = colorStr;
        return new Color(
                Integer.valueOf( finalColorString.substring( 1, 3 ), 16 ),
                Integer.valueOf( finalColorString.substring( 3, 5 ), 16 ),
                Integer.valueOf( finalColorString.substring( 5, 7 ), 16 ) );
    }

    public static UUID convertToLongUUID(String shortUUID){
        StringBuffer sb = new StringBuffer(shortUUID);
        sb.insert(8, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(13, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(18, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(23, "-");

        return UUID.fromString(sb.toString());
    }

    public static void sendConsoleLog(String message, Object... components){
        System.out.printf("%s %s%n", getLogTime(), String.format(message, components));
    }
}
