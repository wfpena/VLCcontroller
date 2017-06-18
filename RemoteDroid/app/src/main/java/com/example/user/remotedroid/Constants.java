package com.example.user.remotedroid;


public class Constants {

    public static String SERVER_IP = "192.168.1.77";//Tem que ser o IP do VLC local
    public static int SERVER_PORT = 4212;
    public static final String PLAY="play";
    public static final String NEXT="next";
    public static final String PREVIOUS="previous";
    public static final String MOUSE_LEFT_CLICK="left_click";


    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }

    public static void setServerPort(String serverPort) {
        SERVER_PORT = Integer.parseInt(serverPort);
    }

    public static String getServerIp() {
        return SERVER_IP;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }


}