package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import nutsAndBolts.PieceSquareColor;


/**
 * @author francoise.perrin
 * <p>
 * Cette classe est responsable de :
 * créer les cases noires et blanches et les positionner au bon endroit sur le damier
 * créer les pions noirs et blancs en leur affectant une image et les positionner sur leur case initiale
 * promouvoir les pions en dame en changeant leur image
 */
public class GuiFactory {


    /**
     * @param col
     * @param ligne
     * @return Une case noire ou blanche en alternance
     * la case en bas à gauche est noire
     */
    public static BorderPane createSquare(int col, int ligne) {
        SquareGui sgui;

        // sélection de la couleur de la case
        if ((col % 2 == 0 && ligne % 2 == 0) || (col % 2 != 0 && ligne % 2 != 0)) {
            sgui = new SquareGui(col, ligne, PieceSquareColor.WHITE);
        } else {
            sgui = new SquareGui(col, ligne, PieceSquareColor.BLACK);
        }

        return sgui;
    }

    /**
     * @param col
     * @param ligne
     * @return une PieceGui si col/ligne correspond à cases noires
     * des 4 lignes du haut (piece noire) et du bas du damier (piece blanche)
     */
    public static ImageView createPiece(int col, int ligne) {
        PieceGui pgui = null;

        if (!((col % 2 == 0 && ligne % 2 == 0) || (col % 2 != 0 && ligne % 2 != 0))) {
            if (ligne < 4)
                pgui = new PieceGui(col, ligne, PieceSquareColor.BLACK);
            if (ligne > 5)
                pgui = new PieceGui(col, ligne, PieceSquareColor.WHITE);
        }

        return pgui;
    }

    /**
     * @param piece
     * @param promotedPieceColor la promotion consiste à changer l'image de la PieceGui
     */
    public static void PromotePiece(ImageView piece, PieceSquareColor promotedPieceColor) {
        //c'est quoi l'utilité de createImage dans PieceGui qui implémente la Dame
        //sachant que je ne peux pas y accéder car j'ai pas les coordonnées ???
        Image img = null;
        String pieceImageFile = null;
        if (promotedPieceColor == PieceSquareColor.BLACK) {
            pieceImageFile = "DameNoire.png";
        } else {
            pieceImageFile = "DameBlanche.png";
        }

        try {
            img = new Image(new FileInputStream(pieceImageFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        piece = new ImageView(img);
    }


}


