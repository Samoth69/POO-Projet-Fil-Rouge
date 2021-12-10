package controller.localController;


import controller.InputViewData;
import controller.Mediator;
import controller.OutputModelData;
import gui.CheckersSquareGui;
import gui.View;
import model.BoardGame;
import model.Coord;
import model.ModelConfig;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nutsAndBolts.NetworkMessage;
import nutsAndBolts.PieceSquareColor;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * @author francoiseperrin
 * <p>
 * Le controller a 2 responsabilit�s :
 * - il �coute les clics de souris de l'utilisateur sur la vue
 * - il invoque la m�thode moveCapturePromote() du model
 * si actions (move + prise + promotion) OK sur model alors elles sont propag�es sur view
 * (invoque m�thode moveCapturePromote() de la view)
 * <p>
 * La view et le model ne g�rant pas les coordonn�es des cases de la m�me mani�re
 * le controller assure la conversion :
 * - index de 0 � 99 pour la view
 * - Coord (col, ligne) pour le model ['a'..'j'][10..1]
 */
public class Controller implements Mediator, BoardGame<Integer>, EventHandler<MouseEvent> {

    private BoardGame<Coord> model;
    private View view;
    private Network net;

    // Cette valeur est MAJ chaque fois que l'utilisateur clique sur une pi�ce
    // Elle doit �tre conserv�e pour �tre utilis�e lorsque l'utilisateur clique sur une case
    private int toMovePieceIndex;

    public Controller() {
        this.model = null;
        this.view = null;
        this.setToMovePieceIndex(-1);
    }

    private void setToMovePieceIndex(int toMovePieceIndex) {
        this.toMovePieceIndex = toMovePieceIndex;
    }

    public int getToMovePieceIndex() {
        return toMovePieceIndex;
    }

    //////////////////////////////////////////////////////////////////
    //
    // Controller vu comme un m�diateur entre la view et le model
    //
    //////////////////////////////////////////////////////////////////

    public void setView(View view) {
        this.view = view;
    }

    public void setModel(BoardGame<Coord> model) {
        this.model = model;
    }

    public void setNetwork(Network n) {
        this.net = n;
        this.net.setController(this);
    }

    ////////////////////////////////////////////////////////////////////
    //
    // Controller vu comme un Ecouteur des �v�nement souris sur la view
    //
    ////////////////////////////////////////////////////////////////////

    @Override
    public void handle(MouseEvent mouseEvent) {
        try {
            if (mouseEvent.getSource() instanceof CheckersSquareGui)
                checkersSquareGuiHandle(mouseEvent);
            else
                checkersPieceGuiHandle(mouseEvent);
        } catch (Exception e) {
            // Try - Catch pour emp�cher pgm de planter tant que les interfaces
            // CheckersSquareGui et CheckersPieceGui n'existent pas
            // System.out.println(e);
        }
    }

    /**
     * @param mouseEvent Ecoute les �v�nements sur les PieceGui
     */
    private void checkersPieceGuiHandle(MouseEvent mouseEvent) {

        // Recherche PieceGui s�lectionn�e
        ImageView selectedPiece = (ImageView) mouseEvent.getSource();

        // Recherche et fixe coordonn�e de la pi�ce s�lectionn�e
        CheckersSquareGui parentSquare = (CheckersSquareGui) selectedPiece.getParent();
        this.setToMovePieceIndex(parentSquare.getSquareCoord());

        mouseEvent.consume();
    }

    /**
     * @param mouseEvent Ecoute les �v�nements sur les SquareGui
     */
    private void checkersSquareGuiHandle(MouseEvent mouseEvent) throws IOException {

        // Recherche SquareGUI s�lectionn�
        CheckersSquareGui square = (CheckersSquareGui) mouseEvent.getSource();
        int targetSquareIndex = square.getSquareCoord();

        // vérifie si c'est bien à nous de jouer et non à l'adversaire
        if (this.net.getOpponentColor() != this.getCurrentGamerColor())
        {
            // Le controller va invoquer la m�thode moveCapturePromotion() du model
            // et si le model confirme que la pi�ce a bien �t� d�plac�e �cet endroit,
            // il invoquera une m�thode de la view pour la rafraichir
            OutputModelData<Integer> out = this.moveCapturePromote(this.getToMovePieceIndex(), targetSquareIndex);
            if (out.isMoveDone) {
                net.sendMsg(NetworkMessage.MsgType.MoveCapturePromote, null, out);
            }
        }

        // il n'y a plus de pi�ce � d�placer
        this.setToMovePieceIndex(-1);

        // On �vite que le parent ne r�cup�re l'event
        mouseEvent.consume();
    }


    //////////////////////////////////////////////////////////////////
    //
    // Controller vu comme un Substitut du model
    // il invoque les m�thodes du model
    // apr�s actions de l'utilisateur sur la vue
    //
    //////////////////////////////////////////////////////////////////

    /**
     * Invoque m�thode moveCapturePromote() du model (apr�s transformation des coordonn�es)
     * Si d�placement effectif sur model, invoque m�thode actionOnGui() de la view
     * pour rafraichir affichage en fonction des donn�es retourn�es par le model
     */
    @Override
    public synchronized OutputModelData<Integer> moveCapturePromote(Integer toMovePieceIndex, Integer targetSquareIndex) {

        OutputModelData<Integer> ret = null;
        OutputModelData<Coord> outputModelData = model.moveCapturePromote(transformIndexToCoord(toMovePieceIndex), transformIndexToCoord(targetSquareIndex));

        if (outputModelData.isMoveDone) {
            InputViewData<Integer> ivd = new InputViewData<>(
                    toMovePieceIndex,
                    targetSquareIndex,
                    transformCoordToIndex(outputModelData.capturedPieceCoord),
                    transformCoordToIndex(outputModelData.promotedPieceCoord),
                    outputModelData.promotedPieceColor
            );

            view.actionOnGui(ivd);
        }

        ret = new OutputModelData<>(
                outputModelData.isMoveDone,
                toMovePieceIndex,
                targetSquareIndex,
                transformCoordToIndex(outputModelData.capturedPieceCoord),
                transformCoordToIndex(outputModelData.promotedPieceCoord),
                outputModelData.promotedPieceColor
        );

        return ret;
    }

    @Override
    public void setCurrentGamerColor(PieceSquareColor currentGamerColor) {
        this.model.setCurrentGamerColor(currentGamerColor);
    }

    @Override
    public PieceSquareColor getCurrentGamerColor() {
        return this.model.getCurrentGamerColor();
    }

    /**
     * @param squareIndex index
     * @return les coordonn�es m�tier calcul�es � partir de l'index du SquareGUI sous la PieceGUI
     */
    public static Coord transformIndexToCoord(int squareIndex) {
        int length = ModelConfig.LENGTH;
        char col = (char) ((squareIndex) % length + 'a');
        int ligne = length - (squareIndex) / length;
        return new Coord(col, ligne);
    }

    public static int transformCoordToIndex(Coord coord) {
        int squareIndex = -1;
        int length = ModelConfig.LENGTH;
        if (coord != null) {
            squareIndex = (length - coord.getLigne()) * length + (coord.getColonne() - 'a');
        }
        return squareIndex;
    }


}




