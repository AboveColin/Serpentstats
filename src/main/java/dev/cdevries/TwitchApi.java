package dev.cdevries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Arrays;

import org.json.*;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.FollowList;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.helix.domain.VideoList;

import dev.cdevries.SQLConnection.SQLConnection;

public class TwitchApi {
    public static TwitchClient twitchClient;
    public static Configuration config = new Configuration();
    public static String channelName;
    private static SQLConnection con = SQLConnection.getConnection();


    public TwitchApi() {
        config.setChannelName("channel");
        channelName = config.getChannelName().toLowerCase();
    }

    public static void init() {
        config.setClientId("CLIENTID");
        config.setClientSecret("SECRETKEY");
        config.setAccessToken("SECRETKEY");
    }

    public static void connect() {
        twitchClient = TwitchClientBuilder.builder()
            .withEnableHelix(true)
            .withEnableChat(true)
            .withEnablePubSub(true)
            .withEnablePubSub(true)
            .withClientId(config.getClientId())
            .withClientSecret(config.getClientSecret())
            .withDefaultAuthToken(new OAuth2Credential("twitch", config.getAccessToken()))
            .build();
        twitchClient.getClientHelper().enableStreamEventListener(channelName);
        setChannelId();
        TwitchApi.config.setImgUrl(twitchClient.getHelix().getUsers(null, null, Arrays.asList(channelName)).execute().getUsers().get(0).getProfileImageUrl());
    }

    public static int current_viewers() throws IOException, JSONException {
        String url = "https://tmi.twitch.tv/group/user/" + channelName + "/chatters";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in .readLine()) != null) {
            response.append(inputLine);
        } in .close();
        JSONObject json = new JSONObject(response.toString());
        return json.getInt("chatter_count");
    }

    public static int getViewCount() {
        try {
            UserList resultList = twitchClient.getHelix().getUsers(null, null, Arrays.asList(channelName)).execute();
            return resultList.getUsers().get(0).getViewCount();
        } catch (Exception e) {
            System.out.println("[API getVC] "+e);
            return -1;
        }
        
    }

    public static String getGamePlaying() {
        String game;
        StreamList resultList = twitchClient.getHelix().getStreams(null, null, null, 5, null, null, Arrays.asList(config.getChannelId()), null).execute();
        try {
            game = resultList.getStreams().get(0).getGameName().replace("é", "e");
        } catch (Exception e) {
            game = "Offline";
        }

        return game;
    }

    public static void setChannelId() {
        UserList resultList = twitchClient.getHelix().getUsers(null, null, Arrays.asList(channelName)).execute();
        config.setChannelID(resultList.getUsers().get(0).getId());
    }

    public static int getMessagePerMinute() {
        try {
            Statement stmt = con.conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Chat WHERE TIMESTAMP > DATE_SUB(NOW(),INTERVAL 1 MINUTE)");
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("[API gMPM] "+e);
            return 0;
        }

    }

    public static int getMessagePerHour() {
        try {
            Statement stmt = con.conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Chat WHERE TIMESTAMP > DATE_SUB(NOW(),INTERVAL 60 MINUTE)");
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("[API gMPH] "+e);
            return 0;
        }
    }

    public static int getMessagePerSecond() {
        try {
            Statement stmt = con.conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Chat WHERE TIMESTAMP > DATE_SUB(NOW(),INTERVAL 5 SECOND)");
            rs.next();
            return (rs.getInt(1) / 5);
        } catch (SQLException e) {
            System.out.println("[API gMPS] "+e);
            return 0;
        }
    }

    public static double getAvarageMessageLastHour() {
        return getMessagePerHour() / 60;
    }

    public static double getAvarageMessageLastSecond() {
        return getMessagePerHour() / 3600;
    }


    public static int getCurrentViewCount() {
        int currentViewCount;
        StreamList resultList = twitchClient.getHelix().getStreams(null, null, null, 5, null, null, Arrays.asList(config.getChannelId()), null).execute();
        try {
            currentViewCount = resultList.getStreams().get(0).getViewerCount();
        } catch (Exception e) {
            System.out.println("[API getCurrentViewCount] "+e);
            currentViewCount = 0;
        }

        return currentViewCount;
    }

    public static void getUserInfo() {

        UserList resultList = twitchClient.getHelix().getUsers(null, null, Arrays.asList(config.getChannelName())).execute();
        resultList.getUsers().forEach(user -> {
            System.out.println(user);
        });
    }

    public static int getFollowerCount() {
        FollowList resultList = twitchClient.getHelix().getFollowers(config.getAccessToken(), null, config.getChannelId(), null, 1).execute();
        return resultList.getTotal();
    }

    public static String getUserId(String uName) {
        UserList resultList = twitchClient.getHelix().getUsers(null, null, Arrays.asList(uName)).execute();
        return resultList.getUsers().get(0).getId();
    }

    public static Instant getUserAccountCreationDate(String uname) {
        UserList resultList = twitchClient.getHelix().getUsers(null, null, Arrays.asList(uname)).execute();
        return resultList.getUsers().get(0).getCreatedAt();
    }

    public static void getUserFollowing(String uname) {
        FollowList resultList = twitchClient.getHelix().getFollowers(config.getAccessToken(), getUserId(uname), null, null, 100).execute();
        resultList.getFollows().forEach(follow -> {
            // System.out.println(follow.getFromName() + " is following " + follow.getToName());
        });
    }

    public static void getUserVideos() {
        VideoList resultList = twitchClient.getHelix().getVideos(config.getAccessToken(), null, config.getChannelId(), null, null, null, "trending", null, null, null, 100).execute();
        resultList.getVideos().forEach(video -> {
            // System.out.println(video.getId() + ": " + video.getTitle() + " - by: " + video.getUserName());
        });
    }

    public static String[] getUserList() {

        String[] userList = new String[0];
        try {
            URL url = new URL("https://tmi.twitch.tv/group/user/" + channelName + "/chatters");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            JSONObject json = new JSONObject(response.toString());
            JSONArray viewers = json.getJSONObject("chatters").getJSONArray("viewers");
            userList = new String[viewers.length()];
            for (int i = 0; i < viewers.length(); i++) {
                userList[i] = viewers.getString(i);
            }
        } catch (Exception e) {
            System.out.println("[API getUserList] Error: " + e);
        }
        return userList;
    }

    public static String[] getModList() {

        String[] moderatorList = new String[0];
        try {
            URL url = new URL("https://tmi.twitch.tv/group/user/" + channelName + "/chatters");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            JSONObject json = new JSONObject(response.toString());
            JSONArray moderators = json.getJSONObject("chatters").getJSONArray("moderators");
            moderatorList = new String[moderators.length()];
            for (int i = 0; i < moderators.length(); i++) {
                moderatorList[i] = moderators.getString(i);
            }
        } catch (Exception e) {
            System.out.println("[API GetModeratorList] Error: " + e);
        }
        return moderatorList;

    }

    public static String[] getStaffList() {

        String[] staffList = new String[0];
        try {
            URL url = new URL("https://tmi.twitch.tv/group/user/" + channelName + "/chatters");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            JSONObject json = new JSONObject(response.toString());
            JSONArray staff = json.getJSONObject("chatters").getJSONArray("staff");
            staffList = new String[staff.length()];
            for (int i = 0; i < staff.length(); i++) {
                staffList[i] = staff.getString(i);
            }
        } catch (Exception e) {
            System.out.println("[API getStaffList] Error: " + e);
        }
        return staffList;
    }



}