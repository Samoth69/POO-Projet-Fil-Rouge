package model;

import nutsAndBolts.PieceSquareColor;

public class PawnModel extends AbstractPieceModel implements Promotable {

	private final int direction;
	
	public PawnModel(Coord coord, PieceSquareColor pieceColor) {
		super(coord, pieceColor);
		this.direction = PieceSquareColor.BLACK.equals(this.getPieceColor()) ? -1 : 1;
	}

	@Override
	public boolean isMoveOk(Coord targetCoord, boolean isPieceToCapture) {
		boolean ret = false;

		int colDistance = targetCoord.getColonne() - this.getColonne();
		int ligDistance = targetCoord.getLigne() - this.getLigne();
		int deltaLig = (int) Math.signum(ligDistance);
		
		// Cas d'un d�placement en diagonale
		if (Math.abs(colDistance) == Math.abs(ligDistance)){
			
			// sans prise
			if (!isPieceToCapture) {
				if (deltaLig == this.direction && Math.abs(colDistance) == 1) {
					ret = true;
				}
			}
			// avec prise
			else {
				if (Math.abs(colDistance) == 2) {
					ret = true;
				}
			}
		}
		return ret;
	}

	@Override
	public boolean isPromotable() {
		boolean ret;
		if (this.getPieceColor() == PieceSquareColor.BLACK)
		{
			ret = this.getLigne() == 1; //le tableau va de 1..LENGTH
		}
		else //PieceSquareColor.WHITE
		{
			ret = this.getLigne() == ModelConfig.LENGTH;
		}
		return ret;
	}

	@Override
	public void promote() {
		//ne fait rien
	}

}

