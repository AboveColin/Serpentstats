package dev.cdevries;

import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.eventsub.domain.Reward;
import com.github.twitch4j.eventsub.events.ChannelPointsCustomRewardEvent;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.CheerEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.common.enums.CommandPermission;

import java.sql.PreparedStatement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Calendar;
import java.util.Set;

import dev.cdevries.SQLConnection.SQLConnection;

public class EventListener {
    private static String giftedBy;
    private static Long timestamp;
    private static SQLConnection con = SQLConnection.getConnection();

    public static void run() {
        startGoLiveListener();
        startGoOfflineListener();
        startCurrentGameListener();
        startChatLogger();
        startSubListener();
        startRaidListener();
        startCheerListener();
    }
    public static void startChatLogger_test() {
        TwitchApi.twitchClient.getChat().joinChannel(TwitchApi.channelName);
        TwitchApi.twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            System.out.println(event.toString());
        });
    }

    public static void startChannelRewardsLogger_test() {
        TwitchApi.twitchClient.getChat().joinChannel(TwitchApi.channelName);
        TwitchApi.twitchClient.getEventManager().onEvent(ChannelPointsReward.class, event -> {
            System.out.println("Channel points rewards");
            System.out.println(event.toString());
        });
    }

    public static void startChannelRewardsLogger2_test() {
        TwitchApi.twitchClient.getChat().joinChannel(TwitchApi.channelName);
        TwitchApi.twitchClient.getEventManager().onEvent(ChannelPointsCustomRewardEvent.class, event -> {
            System.out.println("ChannelPointsCustomRewardEvent");
            System.out.println(event.toString());
        });
    }

    public static void startChannelRewardsLogger3_test() {
        TwitchApi.twitchClient.getChat().joinChannel(TwitchApi.channelName);
        TwitchApi.twitchClient.getEventManager().onEvent(ChannelPointsRedemptionEvent.class, event -> {
            System.out.println("hannelPointsRedemptionEvent");
            System.out.println(event.toString());
        });
    }

    public static void startChannelRewardsLogger4_test() {
        TwitchApi.twitchClient.getChat().joinChannel(TwitchApi.channelName);
        TwitchApi.twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, event -> {
            System.out.println("RewardRedeemedEvent.class");
            System.out.println(event.toString());
        });
    }

    public static void startChannelRewardsLogger5_test() {
        TwitchApi.twitchClient.getChat().joinChannel(TwitchApi.channelName);
        TwitchApi.twitchClient.getEventManager().onEvent(Reward.class, event -> {
            System.out.println("RewardRedeemedEvent.class");
            System.out.println(event.toString());
        });
    }


    public static void startChatLogger() {
        TwitchApi.twitchClient.getChat().joinChannel(TwitchApi.channelName);
        TwitchApi.twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            try {

                timestamp = Long.parseLong(event.toString().split("tmi-sent-ts=")[1].split(",")[0].replace("\"", ""));
                int first_message = Integer.parseInt(event.toString().split("first-msg=")[1].split(",")[0].replace("\"", ""));
                String timeD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
                String user = event.getUser().getName();
                String message = event.getMessage();
                int months = event.getSubscriberMonths();
                int tier = event.getSubscriptionTier();
                Set<CommandPermission> status = event.getPermissions();

                if (event.getMessage().contains("channel heeft momenteel ")) {
                    TwitchApi.config.setSubCount(Integer.parseInt(event.getMessage().replaceAll("[^0-9]", "")));
                }
                int vip = 0;
                if(status.toString().contains("VIP")) {
                    vip = 1;
                }
                int mod = 0;
                if(status.toString().contains("MODERATOR")) {
                    mod = 1;
                }
                
                if(status.toString().contains("FOUNDER")) {
                    months = Integer.parseInt(event.toString().split("badge-info=founder/")[1].split("},")[0]);
                    tier = 1;
                }


                PreparedStatement statement = con.conn.prepareStatement("INSERT INTO Chat (Timestamp, Username, Message, Months, Tier, Status, VIP, Moderator, first_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, timeD);
                statement.setString(2, user);
                statement.setString(3, message);
                statement.setInt(4, months);
                statement.setInt(5, tier);
                statement.setString(6, status+"");
                statement.setInt(7, vip);
                statement.setInt(8, mod);
                statement.setInt(9, first_message);
                statement.execute();

                System.out.println("[CHAT] Wrote chat message to DB!");
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public static void startCurrentGameListener() {
        TwitchApi.twitchClient.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> {
            GUI.labelLogging.setText(GUI.labelLogging.getText() + "[LSTN] " + event.getChannel().getName() + " is now playing " + event.getStream().getGameName() + "!<br>");
            TwitchApi.config.setCurrentGame(event.getStream().getGameName());
        });
    }

    public static void startGoLiveListener() {
        TwitchApi.twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            GUI.labelLogging.setText(GUI.labelLogging.getText() + "[LSTN] " + event.getChannel().getName() + " went live with title " + event.getStream().getTitle() + " on game " + event.getStream().getGameName() + "!<br>");
            TwitchApi.config.setCurrentGame(event.getStream().getGameName());
        });
    }

    public static void startGoOfflineListener() {
        TwitchApi.twitchClient.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> {
            GUI.labelLogging.setText(GUI.labelLogging.getText() + "[LSTN] " + event.getChannel().getName() + " went offline!<br>");
            TwitchApi.config.setCurrentGame("Offline");
        });
    }

    public static void startSubListener() {
        TwitchApi.twitchClient.getEventManager().onEvent(SubscriptionEvent.class, event -> {
            System.out.println("[LSTN] subscribed!");
            if (event.getGiftedBy() != null) {
                giftedBy = event.getGiftedBy().getName();
            } else {
                giftedBy = "";
            }
            try {
                
                PreparedStatement statement = con.conn.prepareStatement("INSERT INTO Subs (Timestamp, Username, Months, Gifted, GiftedBy, subStreak, Tier) VALUES (?, ?, ?, ?, ?, ?, ?)");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                statement.setString(1, dateFormat.format(cal.getTime()));
                statement.setString(2, event.getUser().getName());
                statement.setInt(3, event.getMonths());
                statement.setInt(4, event.getGifted() ? 1 : 0);
                statement.setString(5, giftedBy);
                statement.setInt(6, event.getSubStreak());
                statement.setString(7, event.getSubscriptionPlan().toString());


                statement.execute();

                System.out.println("[CHAT] Wrote subs from "+event.getUser().getName()+" to to DB!");
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public static void startCheerListener() {
        TwitchApi.twitchClient.getEventManager().onEvent(CheerEvent.class, event -> {
            System.out.println("[LSTN] CHEER!");
            try {
                PreparedStatement statement = con.conn.prepareStatement("INSERT INTO Cheers (Timestamp, Username, Cheers) VALUES (?, ?, ?)");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                statement.setString(1, dateFormat.format(cal.getTime()));
                statement.setString(2, event.getUser().getName());
                statement.setInt(3, event.getBits());
                statement.execute();

                System.out.println("[CHAT] Wrote Cheers from "+event.getUser().getName()+" to to DB!");
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public static void startRaidListener() {
        TwitchApi.twitchClient.getEventManager().onEvent(RaidEvent.class, event -> {
            System.out.println("[LSTN] RAID!");
            try {
                PreparedStatement statement = con.conn.prepareStatement("INSERT INTO Raids (Timestamp, Username, Viewers) VALUES (?, ?, ?)");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                statement.setString(1, dateFormat.format(cal.getTime()));
                statement.setString(2, event.getRaider().getName());
                statement.setInt(3, event.getViewers());
                statement.execute();

                System.out.println("[RAID] Raid from "+event.getRaider().getName()+" to to DB!");
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }
}