package gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import nutsAndBolts.PieceSquareColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * @author francoise.perrin
 * 
 * Cette classe est responsable de :
 * 		cr�er les cases noires et blanches et les positionner au bon endroit sur le damier
 * 		cr�er les pions noirs et blancs en leur affectant une image et les positionner sur leur case initiale
 *		promouvoir les pions en dame en changeant leur image
 */
public class GuiFactory {

	/**
	 * @param col colonne
	 * @param ligne ligne
	 * @return Une case noire ou blanche en alternance
	 * la case en bas � gauche est noire
	 */
	public static BorderPane createSquare(int col, int ligne) {
		PieceSquareColor squareColor;

		// s�lection de la couleur de la case
		if ((col % 2 == 0 && ligne % 2 == 0) || (col % 2 != 0 && ligne % 2 != 0)) {
			squareColor = PieceSquareColor.WHITE;
		} else {
			squareColor = PieceSquareColor.BLACK;
		}
		return new SquareGui(squareColor);
	}

	/**
	 * @param col colonne
	 * @param ligne ligne
	 * @return une PieceGui si col/ligne correspond � cases noires
	 * des 4 lignes du haut (piece noire) et du bas du damier (piece blanche)
	 */
	public static ImageView createPiece(int col, int ligne) {

		ImageView pieceGui = null;
		Image image;
		PieceSquareColor pieceColor = null;

		if  ( !((col % 2 == 0 && ligne % 2 == 0) || (col % 2 != 0 && ligne % 2 != 0)) ) {
			if (ligne < 4)
				pieceColor = PieceSquareColor.BLACK;
			if (ligne > 5)
				pieceColor = PieceSquareColor.WHITE;

			if (pieceColor != null) {
				image = GuiFactory.createImage(pieceColor, true);
				pieceGui = new PieceGui(image, pieceColor, col, ligne);
			}
		}

		return pieceGui;
	}

	/**
	 * @param piece objet javaFX contenant le pion
	 * @param promotedPieceColor couleur de pièce à promouvoir
	 * la promotion consiste � changer l'image de la PieceGui
	 */
	public static void PromotePiece(ImageView piece, PieceSquareColor promotedPieceColor) {
		piece.setImage(createImage(promotedPieceColor, false));
	}
	
	/**
	 * @param pieceColor couleur de la pièce
	 * @param ispawn si faux, concidère que l'on shouaite créer une queen
	 * @return une image cr��e � partir d'un fichier png
	 */
	private static Image createImage(PieceSquareColor pieceColor, boolean ispawn) {

		Image image = null;
		String pieceImageFile, nomImageFile;
		File g = new File("");

		if (ispawn) {
			nomImageFile = pieceColor == PieceSquareColor.BLACK ? "PionNoir.png" : "PionBlanc.png";
		}
		else {	
			nomImageFile = pieceColor == PieceSquareColor.BLACK ? "DameNoire.png" : "DameBlanche.png";
		}

		pieceImageFile = g.getAbsolutePath()+"/images/" + nomImageFile;	// TODO - attention au chemin
		try {
			image = new Image(new FileInputStream(pieceImageFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return image;
	}


}


