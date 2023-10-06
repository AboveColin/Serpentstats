package dev.cdevries;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class GUI{
    public static JLabel labelStats;
    public static JLabel labelLastUpdate;
    public static JLabel labelLogging;
    public static JLabel labelPH;
    private static JTextArea console;
    private static JFrame frame;
    private static JScrollPane scrollPane;
    private static JScrollPane UserList;
    private static JScrollPane ModList;
    private static PrintStream outStream;
    public static JList<String> labelUserList;
    public static JList<String> labelModList;
    public static JList<String> labelStaffList;

    public static void start() {
        frame = new JFrame("Stats | " + TwitchApi.config.getChannelName());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console = new JTextArea("", 10, 30);
        console.setEditable(true);
        console.setLineWrap(true);
        scrollPane = new JScrollPane(console);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        labelUserList = new JList < > ();
        UserList = new JScrollPane(labelUserList);
        UserList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        labelModList = new JList < > ();
        ModList = new JScrollPane(labelModList);
        ModList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ModList.setSize(200, 200);


        outStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                GUI.append(new String(Character.toChars(b)));
            }
        });
        System.setOut(outStream);
        frame.pack();

        labelStats = new JLabel();
        labelLastUpdate = new JLabel();
        labelLogging = new JLabel();
        JPanel LU = new JPanel();
        JPanel Statspanel = new JPanel();


        LU.add(labelLastUpdate);
        Statspanel.add(labelStats);
        frame.setLayout(new BorderLayout(5, 5));
        frame.getContentPane().add(BorderLayout.NORTH, LU);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
        frame.getContentPane().add(BorderLayout.EAST, Statspanel);
        frame.getContentPane().add(BorderLayout.WEST, UserList);
        frame.getContentPane().add(BorderLayout.SOUTH, ModList);
        frame.setVisible(true);
        frame.setSize(1500, 500);
        frame.setResizable(true);
    }

    public static void setText(String text) {
        console.setText(text);
    }

    public static void append(String text) {
        console.append(text);
    }

    public static void append(int number) {
        console.append("" + number);
    }

    public static void append(double number) {
        console.append("" + number);
    }

    public static void append(boolean bool) {
        console.append("" + bool);
    }

    public static String getText() {
        return console.getText();
    }
}