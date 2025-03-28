package prj.shtelo.inminic.client.telecommunication;

import prj.shtelo.inminic.client.Root;
import prj.shtelo.inminic.client.cameraobject.Player;
import prj.shtelo.inminic.client.rootobject.RootObject;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

class ClientThread extends Thread {
    private Socket socket;
    private Client client;
    private Root root;

    private Scanner scanner;

    ClientThread(Socket socket, Client client, Root root) throws IOException {
        this.socket = socket;
        this.client = client;
        this.root = root;

        init();
    }

    private void init() throws IOException {
        scanner = new Scanner(socket.getInputStream());

        String message;
        String[] messages;

        client.send("serverInfo");
        message = recv();
        messages = message.split("\t");

        if (messages[0].equalsIgnoreCase("serverInfo")) {
            client.changeMapName(messages[1]);

            int count = Integer.parseInt(messages[2]);
            for (int i = 0; i < count; i++) {
                messages = recv().split("\t");
                String name = messages[1];
                double x = Double.parseDouble(messages[2]);
                double y = Double.parseDouble(messages[3]);
                if (!name.equalsIgnoreCase(root.getName())) {
                    RootObject.add(new Player(x, y, name, root.getCamera(), root));
                }
            }
        }
    }

    @Override
    public void run() {
        String message;
        String[] messages;
        while (client.isConnected()) {
            try {
                message = recv();
            } catch (NoSuchElementException e) {
                root.getClient().disconnect();
                JOptionPane.showMessageDialog(null, "서버가 종료되었습니다.", "InMinic Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            messages = message.split("\t");

            if (messages[0].equalsIgnoreCase("connect")) {
                if (!messages[1].equalsIgnoreCase(root.getCharacter().getName())) {
                    double x = Double.parseDouble(messages[2]);
                    double y = Double.parseDouble(messages[3]);
                    RootObject.add(new Player(x, y, messages[1], root.getCamera(), root));
                }
            } else if (messages[0].equalsIgnoreCase("disconnect")) {
                String name = messages[1];
                System.out.println(name);
                root.findPlayerByName(name).destroy();
            } else if (messages[0].equalsIgnoreCase("move")) {
                String name = messages[1];
                if (!name.equalsIgnoreCase(root.getCharacter().getName())) {
                    double x = Double.parseDouble(messages[2]);
                    double y = Double.parseDouble(messages[3]);
                    boolean watchingRight = Boolean.parseBoolean(messages[4]);
                    int form = Integer.parseInt(messages[5]);

                    Player player = root.findPlayerByName(name);
                    player.setX(x);
                    player.setY(y);
                    player.setWatchingRight(watchingRight);
                    player.setForm(form);
                }
            } else if (messages[0].equalsIgnoreCase("chatting")) {
                root.getChattingBox().add(messages[1], messages[2]);
            }
        }
    }

    private String recv() {
        return scanner.nextLine();
    }
}
