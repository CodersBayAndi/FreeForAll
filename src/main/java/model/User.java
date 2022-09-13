package model;

import server.ServerThread;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {

    private Fighter fighter;
    private Socket socket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;

    public User(Fighter fighter, Socket socket, PrintWriter outputStream, BufferedReader inputStream) {
        this.fighter = fighter;
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    public Fighter getFighter() {
        return fighter;
    }

    public void setFighter(Fighter fighter) {
        this.fighter = fighter;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintWriter outputStream) {
        this.outputStream = outputStream;
    }

    public BufferedReader getInputStream() {
        return inputStream;
    }

    public void setInputStream(BufferedReader inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public String toString() {
        return "User{" +
                "fighter=" + fighter +
                ", socket=" + socket +
                ", outputStream=" + outputStream +
                ", inputStream=" + inputStream +
                '}';
    }
}
