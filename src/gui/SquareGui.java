package gui;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import nutsAndBolts.PieceSquareColor;

/**
 * @author francoiseperrin
 * 
 * Classe d'affichage des carr�s du damier
 * leur couleur est initialis� par les couleurs par d�faut du jeu
 *
 */
class SquareGui extends BorderPane implements CheckersSquareGui {

	private final PieceSquareColor color;

	public SquareGui(PieceSquareColor color) {
		super();
		this.color = color;

		// la couleur est définie par les valeurs par défaut de configuration
		Color GioConfcolor = PieceSquareColor.BLACK.equals(color) ? GuiConfig.CASEBLACK : GuiConfig.CASEWHITE;
		this.setBackground(new Background(new BackgroundFill(GioConfcolor, CornerRadii.EMPTY, Insets.EMPTY)));
		this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
	}

	/**
	 *Retourne l'indice du carr� sur la grille (N� de 0 � 99)
	 */
	@Override
	public int getSquareCoord() {
		int index = -1;
		Pane parent = (Pane) this.getParent();
		index = parent.getChildren().indexOf(this);
		return index;
	}

}
