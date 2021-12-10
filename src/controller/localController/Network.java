package controller.localController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.OutputModelData;
import javafx.application.Platform;
import launcher.IWindow;
import model.BoardGame;
import nutsAndBolts.NetworkMessage;
import nutsAndBolts.PieceSquareColor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Network implements Runnable {

    private Thread thread;
    private Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private UUID opponentUUID;
    private PieceSquareColor opponentColor;
    private boolean partyReady = false;
    private boolean stop = false;

    private BoardGame<Integer> controller;
    private IWindow window;

    public Network() throws IOException {
        this.thread = new Thread(this);
        this.thread.setName("Jeu de dames: Network");
        this.thread.start();

        ArrayList<Integer> params = new ArrayList<>();
        params.add(PieceSquareColor.WHITE.toInt());
        sendMsg(NetworkMessage.MsgType.Color, params, null);
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket("127.0.0.1", 1234);
            this.socket.setKeepAlive(true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), false);

            while (!stop) {
                NetworkMessage msg = gson.fromJson(in.readLine(), NetworkMessage.class);
                if (opponentUUID != null && !msg.getSenderUUID().equals(opponentUUID)) {
                    continue;
                }
                switch (msg.getMessageType()) {
                    case Color -> {
                        if (msg.getParams().get(0) == this.controller.getCurrentGamerColor().toInt()) {
                            //this.controller.setCurrentGamerColor(PieceSquareColor.fromInt(msg.getParams().get(0)).getNext());
                            System.out.println("we are " + this.controller.getCurrentGamerColor().getNext().toString());
                            updateWindowTitle(this.controller.getCurrentGamerColor().getNext());
                            opponentColor = this.controller.getCurrentGamerColor();
                            sendMsg(NetworkMessage.MsgType.ColorACK, null, null);
                            continue;
                        }
                        ArrayList<Integer> params = new ArrayList<>();
                        params.add(PieceSquareColor.BLACK.toInt());
                        sendMsg(NetworkMessage.MsgType.Color, params, null);
                        this.opponentUUID = msg.getSenderUUID();
                        this.partyReady = true;
                    }
                    case ColorACK -> {
                        opponentUUID = msg.getSenderUUID();
                        System.out.println("we are " + this.controller.getCurrentGamerColor().toString());
                        updateWindowTitle(this.controller.getCurrentGamerColor());
                        opponentColor = this.controller.getCurrentGamerColor().getNext();
                    }
                    case MoveCapturePromote -> {
                        OutputModelData<Integer> omd = msg.getOutputModelData();
                        if (omd.isMoveDone) {
                            Platform.runLater(() -> controller.moveCapturePromote(omd.toMovePieceIndex, omd.targetSquareIndex));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(NetworkMessage.MsgType msgType, List<Integer> params, OutputModelData<Integer> omd) throws IOException {
        NetworkMessage msg = new NetworkMessage(msgType, params, omd);
        out.write(gson.toJson(msg));
        out.write('\n');
        out.flush();
    }

    public boolean isPartyReady() {
        return partyReady;
    }

    public void setController(BoardGame<Integer> controller) {
        this.controller = controller;
    }

    public void stop() {
        this.thread.interrupt();
    }

    public void setWindow(IWindow window) {
        this.window = window;
    }

    private void updateWindowTitle(PieceSquareColor color) {
        switch (color) {

            case WHITE -> {
                this.window.setWindowTitle("Jeu de dames - Joueur Blanc");
            }
            case BLACK -> {
                this.window.setWindowTitle("Jeu de dames - Joueur Noir");
            }
        }
    }

    public PieceSquareColor getOpponentColor() {
        return opponentColor;
    }

    public PieceSquareColor getThisPlayerColor() {
        return opponentColor.getNext();
    }
}
