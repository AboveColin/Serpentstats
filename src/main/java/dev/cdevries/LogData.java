package dev.cdevries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import dev.cdevries.StreamElements.ObtainSubs;
import dev.cdevries.SQLConnection.SQLConnection;

public class LogData extends Thread {
    private static int currentviewCount;
    private static int preViewCount = 0;
    private static int diffViewCount;
    private static String currentGamePlaying;
    private static int currentCurrentViewers;
    private static int preCurrentViewers = 0;
    private static int diffCurrentViewers;
    private static int currentMpm;
    private static int preMpm = 0;
    private static int diffMpm;
    private static int currentMph;
    private static int preMph = 0;
    private static int diffMph;
    private static int currentMps;
    private static int preMps = 0;
    private static int diffMps;
    private static double currentAmlh;
    private static double preAmlh = 0;
    private static double diffAmlh;
    private static double currentAmlhS;
    private static double preAmlhS = 0;
    private static double diffAmlhS;
    private static int n = 0;
    private static int currentSubCount;
    private static int preSubCount = 0;
    private static int diffSubCount;
    private static int followerCount;
    private static int preFollowerCount= 0;
    private static int diffFollowerCount;
    private static double currentTimer;
    private static SQLConnection con = SQLConnection.getConnection();
  
    @Override
    public void run() {
        System.out.println("ON!");
        
        while (n == 0) {

            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            try {
                currentCurrentViewers = TwitchApi.getCurrentViewCount() == 0 ? TwitchApi.current_viewers() : TwitchApi.getCurrentViewCount();
                if (currentCurrentViewers == 0) {
                    Statement stmt = con.conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT Viewers FROM Data ORDER BY Timestamp DESC LIMIT 1");
                    rs.next();
                    currentCurrentViewers = rs.getInt(1);
                }
                diffCurrentViewers = currentCurrentViewers - preCurrentViewers;
                preCurrentViewers = currentCurrentViewers;
            } catch (Exception e) {
                System.out.println("[currentViewers] error: " + e);
                currentCurrentViewers = -1;
            }
            
            try {
                currentGamePlaying = TwitchApi.config.getCurrentGame();
                if (currentGamePlaying == null) {
                    currentGamePlaying = TwitchApi.getGamePlaying();
                }
            } catch (Exception e) {
                System.out.println("[currentGamePlaying] Error: " + e);
                currentGamePlaying = "error";
                try { 
                    currentGamePlaying = TwitchApi.getGamePlaying();
                } catch (Exception e2) {
                    System.out.println("[currentGamePlaying2] Error: " + e2);
                    currentGamePlaying = "error";
                }
            }

            try {
                if (currentGamePlaying.equals("I'm Only Sleeping")) {
                    TwitchApi.config.setSleeping(true);
                } else {
                    TwitchApi.config.setSleeping(false);
                }
            } catch (Exception e) {
                System.out.println("[currentGamePlayingSleep] Error: " + e);
            }

            try {
                currentviewCount = TwitchApi.getViewCount();
                if (currentviewCount == 0) {
                    Statement stmt = con.conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT TotalViews FROM Data ORDER BY Timestamp DESC LIMIT 1");
                    rs.next();
                    currentviewCount = rs.getInt(1);
                }
                diffViewCount = currentviewCount - preViewCount;
                preViewCount = currentviewCount;
            } catch (Exception e) {
                System.out.println("[CurrentViewCount] error: " + e);
                currentviewCount = -1;
            }

            try {
                currentMpm = TwitchApi.getMessagePerMinute();
                diffMpm = currentMpm - preMpm;
                preMpm = currentMpm;
            } catch (Exception e) {
                System.out.println("[GetMessagePerMinute] error: " + e);
                currentMpm = -1;
            }

            try {
                currentMph = TwitchApi.getMessagePerHour();
                diffMph = currentMph - preMph;
                preMph = currentMph;
            } catch (Exception e) {
                System.out.println("[GetMessagePerHour] error: " + e);
                currentMph = -1;
            }

            try {
                currentMps = TwitchApi.getMessagePerSecond();
                diffMps = currentMps - preMps;
                preMps = currentMps;
            } catch (Exception e) {
                System.out.println("[GetMessagePerSecond] error: " + e);
                currentMps = -1;
            }

            try {
                currentAmlh = TwitchApi.getAvarageMessageLastHour();
                diffAmlh = currentAmlh - preAmlh;
                preAmlh = currentAmlh;
            } catch (Exception e) {
                System.out.println("[GetAverageMessageLastHour] error: " + e);
                currentAmlh = -1;
            }

            try {
                currentAmlhS = TwitchApi.getAvarageMessageLastSecond();
                diffAmlhS = currentAmlhS - preAmlhS;
                preAmlhS = currentAmlhS;
            } catch (Exception e) {
                System.out.println("[GetAverageMessageLastSecond] error: " + e);
                currentAmlhS = -1;
            }

            try {
                currentTimer = TwitchApi.config.getTimer();
            } catch (Exception e) {
                System.out.println("[GetTimer] error: " + e);
                try {
                    try {
                        Statement stmt = con.conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT Timer FROM Data ORDER BY Timestamp DESC LIMIT 1");
                        rs.next();
                        currentTimer = rs.getDouble(1);
                    } catch (SQLException e2) {
                        System.out.println("[CurrentTimer0] "+ e2);
                        currentTimer = -1;
                    }
                } catch (Exception e2) {
                    System.out.println("[CurrentTimer01] error: " + e2);
                    currentTimer = -1;
                }
            }

            try {
                try {
                    currentSubCount = ObtainSubs.getSubscriberAmount();
                } catch (Exception e) {
                    System.out.println("[CurrentSubCount] error: " + e);
                    currentSubCount = TwitchApi.config.getSubCount();
                    if (currentSubCount == 0) {
                        try {
                            Statement stmt = con.conn.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT Subscribers FROM Data ORDER BY Timestamp DESC LIMIT 1");
                            rs.next();
                            currentSubCount = rs.getInt(1);
                        } catch (SQLException e2) {
                            System.out.println(e2);
                            currentSubCount = 0;
                        }
                    }
                }
                diffSubCount = currentSubCount - preSubCount;
                preSubCount = currentSubCount;
            } catch (Exception e) {
                currentSubCount = -1;
            }

            try {
                followerCount = TwitchApi.getFollowerCount();
                diffFollowerCount = followerCount - preFollowerCount;
                preFollowerCount = followerCount;
            } catch (Exception e) {
                System.out.println("[FollowerCount] error: " + e);
                followerCount = -1;
            }

            if (TwitchApi.config.isEnableGUI()) {
                String output = "";
                if (currentMpm < currentAmlh) {
                    output = "x";
                } else {
                    output = "V";
                }

                String DateTimeAfter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                GUI.labelLastUpdate.setText("Last Update: " + dateTime);
                GUI.labelStats.setText("<html>" +
                    " <img width='100' height='100' src='" + String.valueOf(TwitchApi.config.getImgUrl()) + "'/> <br>" +
                    " Total Views: <font color='green'>" + String.valueOf(currentviewCount) + " (" + String.valueOf(diffViewCount) + ")</font> <br>" +
                    " Viewers: <font color='green'>" + String.valueOf(currentCurrentViewers) + " (" + String.valueOf(diffCurrentViewers) + ")</font> <br>" +
                    " Game: <font color='green'>" + String.valueOf(currentGamePlaying) + "</font> <br>" +
                    " Messages Per Second: <font color='green'>" + String.valueOf(currentMps) + " (" + String.valueOf(diffMps) + ")</font> <br>" +
                    " Messages Last Minute: <font color='green'>" + String.valueOf(currentMpm) + " (" + String.valueOf(diffMpm) + ")</font> <br>" +
                    " Messages Last Hour: <font color='green'>" + String.valueOf(currentMph) + " (" + String.valueOf(diffMph) + ")</font> <br>" +
                    " Average Messages per Minute: <font color='green'>" + String.valueOf(currentAmlh) + " (" + String.valueOf(diffAmlh) + ")</font> <br>" +
                    " Average Messages per Second: <font color='green'>" + String.valueOf(currentAmlhS) + " (" + String.valueOf(diffAmlhS) + ")</font> <br>" +
                    " Current SubCount: <font color='green'>" + String.valueOf(currentSubCount) + " (" + String.valueOf(diffSubCount) + ")</font> <br>" +
                    " Follower Count: <font color='green'>" + String.valueOf(followerCount) + " (" + String.valueOf(diffFollowerCount) + ")</font> <br>" +
                    " Current Timer: <font color='green'>" + String.valueOf(currentTimer) + "" + "</font> <br>" +
                    " Tijd update: <font color='green'>" + String.valueOf(DateTimeAfter) + "" + "</font> <br>" +
                    " Big Moment? " + output + "</html>");


                GUI.labelUserList.setListData(TwitchApi.getUserList());
                GUI.labelModList.setListData(TwitchApi.getModList());
            }
            // GUI.labelStaffList.setListData(TwitchApi.getStaffList());

            

            System.out.println("[DATA] Current Time: " + dateTime);
            System.out.println("[DATA] Total viewers: " + currentviewCount);
            System.out.println("[DATA] Current viewers: " + currentCurrentViewers);
            System.out.println("[DATA] Currently Playing: " + currentGamePlaying);
            System.out.println("[DATA] Messages per Second: " + currentMps);
            System.out.println("[DATA] Messages Per Minute: " + currentMpm);
            System.out.println("[DATA] Messages Per Hour: " + currentMph);
            System.out.println("[DATA] Avarage Messages per Minute: " + currentAmlh);
            System.out.println("[DATA] Avarage Messages per Second: " + currentAmlhS);
            System.out.println("[DATA] Current SubCount: " + currentSubCount);
            System.out.println("[DATA] Follower Count: " + followerCount);
            System.out.println("[DATA] Current Timer: " + currentTimer);

            // System.out.println(ANSI_WHITE + "[DATA] StaffList: " + ANSI_GREEN + TwitchApi.getStaffList());

            // System.out.println("[DATA] Current Time: " + dateTime + "Total Viewers: " + currentviewCount + "Current Viewers: " + currentCurrentViewers + "Currently Playing: " + currentGamePlaying + "Messages Per Minute: " + currentMpm + "Messages Per Hour: " + currentMph + "Avarage Messages per Minute: " + currentAmlh);

            try {
                PreparedStatement statement = con.conn.prepareStatement("INSERT INTO Data (Timestamp, TotalViews, Followers, Viewers, Game, MPS, MPM, MPH, Average, AverageS, Subscribers, Timer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, dateTime);
                statement.setInt(2, currentviewCount);
                statement.setInt(3, followerCount);
                statement.setInt(4, currentCurrentViewers);
                statement.setString(5, currentGamePlaying);
                statement.setInt(6, currentMps);
                statement.setInt(7, currentMpm);
                statement.setInt(8, currentMph);
                statement.setDouble(9, currentAmlh);
                statement.setDouble(10, currentAmlhS);
                statement.setInt(11, currentSubCount);
                statement.setDouble(12, currentTimer);
                statement.execute();
                System.out.println("[DATA] Wrote to DB!");
            } catch (Exception e) {
                System.out.println("[DATA] Error writing to DB!");
                System.out.println(e);
            }



        }

        try {
            con.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}