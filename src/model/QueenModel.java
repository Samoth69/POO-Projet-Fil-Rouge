package model;

import nutsAndBolts.PieceSquareColor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author francoiseperrin
 * <p>
 * le mode de d�placement et de prise de la reine est diff�rent de celui du pion
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

        if (Math.abs(targetCoord.getColonne() - this.getColonne()) >= dif && Math.abs(targetCoord.getLigne() - this.getLigne()) >= dif) {
            ret = true;
        }

        return ret;
    }

    @Override
    public Collection<Coord> getValidCoords() {
        HashSet<Coord> ret = new HashSet<>();
        HashSet<Coord> cache = new HashSet<>();

        for (int i = 1; i < ModelConfig.LENGTH; i++) {
            Coord[] coords = {
                    new Coord((char) (this.getColonne() - i), this.getLigne() - i),
                    new Coord((char) (this.getColonne() - i), this.getLigne() + i),
                    new Coord((char) (this.getColonne() + i), this.getLigne() - i),
                    new Coord((char) (this.getColonne() + i), this.getLigne() + i),
            };
            cache.addAll(List.of(coords));
        }


        for (Coord c : cache) {
            if (Coord.isValidCoords(c)) {
                ret.add(c);
            }
        }

        return ret;
    }

}

