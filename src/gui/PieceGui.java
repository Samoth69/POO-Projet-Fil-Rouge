package gui;

import nutsAndBolts.PieceSquareColor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * @author francoise.perrin
 * 
 * Cette classe permet de donner une image aux pi�ces
 *
 */

public class PieceGui extends ImageView implements CheckersPieceGui {

	private final PieceSquareColor color;
	private final int col, ligne;

	public PieceGui(Image image, PieceSquareColor pieceColor, int col, int ligne) {
		super(image);
		this.color = pieceColor;
		this.col = col;
		this.ligne = ligne;
	}

	@Override
	public void promote(Image image) {
		this.setImage(image);
	}

	@Override
	public boolean hasSameColorAsGamer(PieceSquareColor gamerColor) {
		return this.color == gamerColor;
	}
	
}