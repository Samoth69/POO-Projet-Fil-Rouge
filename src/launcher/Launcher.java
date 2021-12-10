package launcher;


import controller.Mediator;
import controller.localController.Controller;
import controller.localController.Network;
import gui.GuiConfig;
import gui.View;
import model.BoardGame;
import model.Coord;
import model.Model;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import static java.lang.Thread.sleep;


public class Launcher extends Application implements IWindow {

    private BoardGame<Coord> model;
    private EventHandler<MouseEvent> controller;
    private View view;
    private Network net;
    private Stage stage;

    public static void main(String[] args) {

        Launcher.launch();
    }

    @Override
    public void init() throws Exception {
        super.init();

        ///////////////////////////////////////////////////////////////////////////////////////
        // Objet qui g�re les aspects m�tier du jeu de dame :
        ///////////////////////////////////////////////////////////////////////////////////////

        this.model = new Model();


        ///////////////////////////////////////////////////////////////////////////////////////
        // Objet qui contr�le les actions de la vue et les transmet au model
        // il renvoie les r�ponses du model � la vue
        ///////////////////////////////////////////////////////////////////////////////////////

        this.controller = new Controller();


        ///////////////////////////////////////////////////////////////////////////////////////
        // Fen�tre dans laquelle se dessine le damier est �cout�e par controller
        ///////////////////////////////////////////////////////////////////////////////////////

        this.view = new View(controller);

        this.net = new Network();
        this.net.setWindow(this);

        // Controller doit pouvoir invoquer les m�thodes du model
        // il enverra ensuite des instructions � view qui relaiera � son objet Board
        // En mode Client/Server
        // Les actions devront �tre propag�es sur les vues de chaque client et non pas seulement
        // sur celle qui a initi� l'action
        ((Mediator) controller).setView(view);
        ((Mediator) controller).setModel(model);
        ((Mediator) controller).setNetwork(net);
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        primaryStage.setScene(new Scene(this.view, GuiConfig.HEIGHT, GuiConfig.HEIGHT));
        primaryStage.setTitle("Jeu de dames - Version 42");
        primaryStage.show();
        stage = primaryStage;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        net.stop();
    }

    @Override
    public void setWindowTitle(String title) {
        //this.stage.setTitle(title);
        System.out.println(title);
    }
}

