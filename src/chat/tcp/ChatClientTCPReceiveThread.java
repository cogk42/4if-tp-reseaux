package chat.tcp;

import chat.modele.ChatClientState;
import chat.modele.Message;
import chat.modele.Protocol;
import chat.modele.Rename;

import java.io.*;

public class ChatClientTCPReceiveThread extends Thread {
    private final BufferedReader socketInput;
    private final ChatClientState chatClientState;

    private boolean shouldStop = false;

    /**
     * Constructeur de thread de réception chat.client
     * @param socketInput Information sur la socket utilisée par le thread pour recevoir des messages
     */
    public ChatClientTCPReceiveThread(
            BufferedReader socketInput,
            ChatClientState chatClientState
    ) {
        this.socketInput = socketInput;
        this.chatClientState = chatClientState;
    }

    /**
     * Cette fonction donne l'information de demande d'arrêt du thread en modifiant shouldStop
     */
    public void endThread() {
        shouldStop = true;
    }

    public void run() {
        String line;
        String[] arguments;

        while (!shouldStop) {
            try {
                line = socketInput.readLine();
            } catch (IOException e) {
                // e.printStackTrace();
                break;
            }

            if (line == null) {
                continue;
            }

            // Parse the input command
            arguments = line.split("\u0000", 2);

            String commande = arguments.length > 0 ? arguments[0] : "";
            String parametres = arguments.length > 1 ? arguments[1] : "";

            switch (commande) {
                case "M":
                    Message message = Protocol.deserializeMessage(parametres);
                    if (chatClientState.getRoom().equals(message.getRoom())) {
                        System.out.println(message.toString());
                    }
                    break;
                case "R":
                    Rename rename = Protocol.deserializeRename(parametres);
                    System.out.println(rename.toString());
                    break;
                case "E":
                    System.err.println("Erreur de protocole");
                    return;
                case "D":
                    System.err.println("Vous avez été déconnecté.e");
                    return;
                default:
                    System.err.println("Commande serveur inconnue: " + line);
                    return; // stop the thread
            }
        }
    }
}