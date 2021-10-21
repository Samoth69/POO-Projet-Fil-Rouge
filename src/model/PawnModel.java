package model;


import java.util.LinkedList;
import java.util.List;

import nutsAndBolts.PieceSquareColor;

public class PawnModel extends AbstractPieceModel {

    public PawnModel(Coord coord, PieceSquareColor pieceColor) {
        super(coord, pieceColor);
    }

    @Override
    public boolean isMoveOk(Coord targetCoord, boolean isPieceToCapture) {
        boolean ret = false;

        if (isPieceToCapture) {
            // on accepte un mouvement de 2 case en diagonale dans tous les sens
            if (Math.abs(targetCoord.getColonne() - this.getColonne()) == 2 && Math.abs(targetCoord.getLigne() - this.getLigne()) == 2) {
                ret = true;
            }
        } else {
            // si le mouvement est bien de une colone (dans un sens ou l'autre)
            if (Math.abs(targetCoord.getColonne() - this.getColonne()) == 1) {
                // si pièce noir, un déplacement est accepté en bas uniquement
                if (this.pieceColor == PieceSquareColor.BLACK && targetCoord.getLigne() - this.getLigne() == -1) {
                    ret = true;
                }
                // si la pièce est blanche, un déplacement est accepté en haut uniquement
                else if (this.pieceColor == PieceSquareColor.WHITE && targetCoord.getLigne() - this.getLigne() == 1) {
                    ret = true;
                }
            }
        }

        return ret;
    }

    @Override
    public boolean isPromotable() {
        if (this.getPieceColor() == PieceSquareColor.BLACK) {
            return this.getLigne() == ModelConfig.LENGTH;
        } else {
            return this.getLigne() == 0;
        }
    }
}

