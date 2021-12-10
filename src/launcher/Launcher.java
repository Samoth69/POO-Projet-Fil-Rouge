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
        // Objet qui gère les aspects métier du jeu de dame :
        ///////////////////////////////////////////////////////////////////////////////////////

        this.model = new Model();


        ///////////////////////////////////////////////////////////////////////////////////////
        // Objet qui contrôle les actions de la vue et les transmet au model
        // il renvoie les réponses du model à  la vue
        ///////////////////////////////////////////////////////////////////////////////////////

        this.controller = new Controller();


        ///////////////////////////////////////////////////////////////////////////////////////
        // Fenêtre dans laquelle se dessine le damier est écoutée par controller
        ///////////////////////////////////////////////////////////////////////////////////////

        this.view = new View(controller);

        this.net = new Network();
        this.net.setWindow(this);

        // Controller doit pouvoir invoquer les méthodes du model
        // il enverra ensuite des instructions à view qui relaiera à son objet Board
        // En mode Client/Server
        // Les actions devront être propagées sur les vues de chaque client et non pas seulement
        // sur celle qui a initié l'action
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

