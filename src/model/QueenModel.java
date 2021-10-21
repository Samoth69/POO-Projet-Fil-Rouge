package model;


import java.util.LinkedList;
import java.util.List;

import nutsAndBolts.PieceSquareColor;
/**
 * @author francoiseperrin
 *
 *le mode de déplacement et de prise de la reine est différent de celui du pion
 */
public class QueenModel extends AbstractPieceModel {

	public QueenModel(Coord coord, PieceSquareColor pieceColor) {
		super(coord, pieceColor);
	}

	@Override
	public boolean isMoveOk(Coord targetCoord, boolean isPieceToCapture) {
		boolean ret = false;

		int dif = 1;

		if (isPieceToCapture)
			dif = 2;

		if (Math.abs(targetCoord.getColonne() - this.getColonne()) == dif && Math.abs(targetCoord.getLigne() - this.getLigne()) == dif) {
			ret = true;
		}
		
		return ret;
	}

	@Override
	public boolean isPromotable() {
		return false;
	}

}

