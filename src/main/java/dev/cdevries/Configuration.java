package dev.cdevries;

import java.io.IOException;

import dev.cdevries.StreamElements.Commands;

public class Configuration {
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String channelName;
    private String channelId;
    private String currentGame;
    private int mpm;
    private int cooldown;
    private int mph;
    private String imgUrl;
    private int subCount;
    private long totalChatMessages;
    private double timer;
    private String currentHoelaatMessage;
    private boolean isSleeping;
    private boolean enableGUI;


    public void setClientId(String cID) {
        this.clientId = cID;
    }

    public void setClientSecret(String cSecret) {
        this.clientSecret = cSecret;
    }

    public void setAccessToken(String aToken) {
        this.accessToken = aToken;
    }

    public void setChannelID(String channelID) {
        this.channelId = channelID;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setMpm(int perMinute) {
        this.mpm = perMinute;
    }

    public void setCurrentGame(String currentGame) {
        this.currentGame = currentGame;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setMph(int messagesPerHour) {
        this.mph = messagesPerHour;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public void setTotalChatMessages(long l) {
        this.totalChatMessages = l;
    }

    public void setTimer(double d) {
        this.timer = d;
    }

    public void setCurrentHoelaatMessage(String currentHoelaatMessage) {
        this.currentHoelaatMessage = currentHoelaatMessage;
    }

    public void setSleeping(boolean isSleeping) {
        System.out.println(this.isSleeping);
        if (!this.isSleeping == isSleeping) {
            System.out.println("Sleeping Time Toggled!");
            this.isSleeping = isSleeping;
            try {
                Commands.ToggleHoelaat();
            } catch (IOException e) {
                System.out.println("Could not toggle hoelaat: " + e);
            }
        }
        
    }

    public void setEnableGUI(boolean enableGUI) {
        this.enableGUI = enableGUI;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public int getMpm() {
        return mpm;
    }

    public String getCurrentGame() {
        return currentGame;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMph() {
        return mph;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getSubCount() {
        return subCount;
    }

    public long getTotalChatMessages() {
        return totalChatMessages;
    }

    public double getTimer() {
        return timer;
    }

    public String getCurrentHoelaatMessage() {
        return currentHoelaatMessage;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    public boolean isEnableGUI() {
        return enableGUI;
    }

}
