package dev.cdevries;

import dev.cdevries.Timer.ObtainTimer;

public class App {
    public TwitchApi channel;

    public static void main(String[] args) {
        
        new TwitchApi();
        TwitchApi.init();
        TwitchApi.connect();
        TwitchApi.config.setEnableGUI(false);
        if (TwitchApi.config.isEnableGUI()) {
            GUI.start();
        }
        EventListener.run();
        Thread Timer = new Thread(new ObtainTimer());
        Timer.start();
        Thread logData = new Thread(new LogData());
        logData.start();
    
        System.out.println("Succesfully started!");
        
    }
}