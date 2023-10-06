package dev.cdevries.Timer;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;

import dev.cdevries.TwitchApi;
import io.socket.client.IO;
import io.socket.client.Socket;

public class ObtainTimer extends Thread {

    @Override
    public void run() {
        try {
            System.out.println("Starting timer...");
            Obtain_newTimer();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void Obtain_newTimer() throws IOException {
        Socket socket;
        try {
            socket = IO.socket("hmmm");
            socket.connect();
            socket.on("connect", args1 -> {
                socket.emit("join", "Hello World from client");
            });
            socket.on("timerupdate", arg1 -> {
                JSONObject jo = new JSONObject(arg1[0].toString());
                TwitchApi.config.setTimer(jo.getInt("Time"));
            });
        } catch (URISyntaxException e) {
            System.out.println(e);
        }
        
    }

    

    
}
