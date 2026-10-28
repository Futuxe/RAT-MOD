package com.rjewi.utils;

import net.minecraft.datafixer.fix.ChunkDeleteIgnoredLightDataFix;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpServer {
    private final int port;
    private final MinecraftServer mc;

    private volatile BufferedWriter clientWriter;
    private volatile String clientIp;


    public TcpServer(int port, MinecraftServer mc){
        this.mc = mc;
        this.port = port;
    }

    public void start(){
        new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(port)){
                System.out.println("TCP Server listening on " + port);


                while (true){
                    try(Socket clientSocket = ss.accept()){
                        if (clientWriter != null) continue;

                        clientIp = clientSocket.getInetAddress().getHostAddress();

                        clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

                        mc.execute(() -> mc.getPlayerManager().broadcast(Text.literal("[" + clientIp + "] connected"), false));

                        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                        for (String line; (line = clientReader.readLine()) != null; ){
                            String lineToSend = line;
                            mc.execute(() -> mc.getPlayerManager().broadcast(Text.literal("[" + clientIp + "]" + lineToSend), false));
                        }
                    } catch (IOException ignored){
                    } finally{
                        mc.execute(() -> mc.getPlayerManager().broadcast(Text.literal("[" + clientIp + "] disconnected"), false));
                        clientWriter = null;
                        clientIp = null;

                }
                }

                } catch(IOException e){
                    e.printStackTrace();
                }
        }, "tcp-server").start();


    }

    public boolean connected() {
        return clientWriter != null;
    }
    public void sendCmd(String cmd){
        try{
            if (clientWriter == null) return;
            clientWriter.write(cmd + "\n");
            clientWriter.flush();
        } catch (IOException e) {
            clientWriter = null;
        }

    }
}
