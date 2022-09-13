package server;

import fighting.Arena;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private static boolean hasMoreFighters = true;
    private static final List<ServerThread> serverThreadList = new ArrayList<>();

    public static void main(String[] args) {
        Arena arena = new Arena();

        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            Scanner scanner = new Scanner(System.in);

            while (hasMoreFighters) {
                System.out.println("Waiting for a fighter");
                Socket socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket, arena);
                serverThread.start();
                serverThreadList.add(serverThread);

                System.out.println("Type 'start' to start the Arena or hit Enter if you want to add a fighter.");
                String userInput = scanner.nextLine();

                if (userInput.equals("start")) {
                    System.out.println("Starting fight.");
                    checkReadiness();
                    arena.startFight();
                    hasMoreFighters = false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void checkReadiness() {
        boolean isEveryoneReady = true;
        for (ServerThread thread : serverThreadList) {
            isEveryoneReady &= thread.isReady();
        }

        if (!isEveryoneReady) {
            try {
                System.out.println("Not everyone is Ready.");
                Thread.sleep(5000);
                checkReadiness();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
