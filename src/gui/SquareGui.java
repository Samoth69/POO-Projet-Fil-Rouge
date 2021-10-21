package gui;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nutsAndBolts.PieceSquareColor;

/**
 * @author francoiseperrin
 * <p>
 * Classe d'affichage des carrés du damier
 * leur couleur est initialisé par les couleurs par défaut du jeu
 */
class SquareGui extends BorderPane implements CheckersSquareGui {

    private final int col, ligne;
    private final PieceSquareColor squareColor;

    public SquareGui(int col, int ligne, PieceSquareColor pieceColor) {
        super();

        this.col = col;
        this.ligne = ligne;
        this.squareColor = pieceColor;

        // la couleur est définie par les valeurs par défaut de configuration
        Color color = PieceSquareColor.BLACK.equals(squareColor) ? GuiConfig.CASEBLACK : GuiConfig.CASEWHITE;
        super.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        super.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    /**
     * Retourne l'indice du carré sur la grille (N° de 0 à 99)
     */
    @Override
    public int getSquareCoord() {
        int index = -1;
        Pane parent = (Pane) this.getParent();
        index = parent.getChildren().indexOf(this);
        return index;
    }

    public int getCol() {
        return col;
    }

    public int getLigne() {
        return ligne;
    }

    public PieceSquareColor getSquareColor() {
        return squareColor;
    }
}
