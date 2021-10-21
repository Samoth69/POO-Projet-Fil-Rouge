package gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nutsAndBolts.PieceSquareColor;


/**
 * @author francoise.perrin
 * 
 * Cette classe permet de donner une image aux pièces
 *
 */

public class PieceGui extends ImageView implements CheckersPieceGui {

	private final PieceSquareColor color;

	public PieceGui(Image image, PieceSquareColor pieceColor) {
		super(image);
		this.color = pieceColor;
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