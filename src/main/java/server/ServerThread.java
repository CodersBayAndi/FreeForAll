package server;

import fighting.Arena;
import model.Fighter;
import model.InvalidFighterException;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ServerThread extends Thread {

    private final Socket socket;
    private final Arena arena;
    private boolean isReady = false;

    public ServerThread(Socket socket, Arena arena) {
        this.socket = socket;
        this.arena = arena;
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    public void run() {
        new Thread(() -> {
            try {
                System.out.println("Made connection to: " + socket.getInetAddress());
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

                printInstructions(printWriter);
                createFighter(printWriter);
                isReady = true;
            } catch (SocketException e) {
                System.out.println("Removed connection from: " + socket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void printInstructions(PrintWriter printWriter) {
        printWriter.println("Welchen Kaempfer moechtest du in die Arena schicken?");
        printWriter.println("Du hast 2000 Punkte. Verteile sie auf HP, ATK, DEF und CritChance. HP gibt fünffache Menge und Crit ein Zehntel.");
        printWriter.println("Schicke einen Namen und 4 Zahlen, die durch ein Komma getrennt sind. Beispiel: ");
        printWriter.println("Alf,500,500,800,200");
        printWriter.println("Wird zu dem Kaempfer: Alf, 2500 HP, 500 ATK, 800 DEF, 20% Crit");
    }

    private void createFighter(PrintWriter printWriter) throws IOException {
        BufferedReader inputStream;
        while (true) {
            String line = null;
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while ((line = inputStream.readLine()) != null) {
                try {
                    String[] stats = line.split(",");
                    String name = stats[0];
                    double health = Integer.parseInt(stats[1]);
                    double attack = Integer.parseInt(stats[2]);
                    double defense = Integer.parseInt(stats[3]);
                    double critChance = Integer.parseInt(stats[4]);

                    double sum = health + attack + defense + critChance;
                    if (sum <= 2000 && sum > 0 && name.matches("[a-zA-Z]+")) {
                        Fighter fighter = new Fighter(stats[0], health, attack, defense, critChance);
                        User user = new User(fighter, socket, printWriter, inputStream);
                        arena.addUser(user);
                        printWriter.println("You are registered as: " + fighter);
                        return;
                    } else {
                        throw new InvalidFighterException();
                    }
                } catch (Exception e) {
                    printWriter.println("Ungueltiger Kaempfer, versuche es erneut.");
                }
            }
        }
    }
}
