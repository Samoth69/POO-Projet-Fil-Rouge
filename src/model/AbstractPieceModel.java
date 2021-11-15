package model;

import nutsAndBolts.PieceSquareColor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractPieceModel implements PieceModel {
    protected Coord coord;
    protected PieceSquareColor pieceColor;

    protected AbstractPieceModel(Coord coord, PieceSquareColor pColor) {
        this.coord = coord;
        this.pieceColor = pColor;
    }

    @Override
    public char getColonne() {
        return coord.getColonne();
    }

    @Override
    public int getLigne() {
        return coord.getLigne();
    }

    @Override
    public boolean hasThisCoord(Coord coord) {
        return this.coord.equals(coord);
    }

    @Override
    public void move(Coord coord) {
        this.coord = coord;
    }

    @Override
    public PieceSquareColor getPieceColor() {
        return pieceColor;
    }

    @Override
    public abstract boolean isMoveOk(Coord targetCoord, boolean isPieceToCapture);

    /**
     * @param targetCoord coord visé
     * @return listes des coords traversé (sans le 'point de départ' MAIS AVEC le 'point d'arrivé')
     * @author Thomas Violent
     * Renvoie les coordonnées traversé entre le pion et le targetCoord
     */
    @Override
    public List<Coord> getCoordsOnItinerary(Coord targetCoord) {
        List<Coord> coordsOnItinery = new LinkedList<>();

        //voir dans le code pour la signification de ces variables
        int difCol, difLigne;
        boolean difColPos, difLignePos;

        //si le déplacement est supérieur à une case en diagonale
        if (Math.abs(this.getColonne() - targetCoord.getColonne()) != 0 && Math.abs(this.getLigne() - targetCoord.getLigne()) != 0) {

            //si le déplacement est de même longueur en colonne et en ligne
            if (Math.abs(this.getLigne() - targetCoord.getLigne()) == Math.abs(this.getColonne() - targetCoord.getColonne())) {

                //différence entre la colonne de départ et visé
                difCol = this.getColonne() - targetCoord.getColonne();

                //différence entre la ligne de départ et visé
                difLigne = this.getLigne() - targetCoord.getLigne();

                //vrai si difCol est positif, faux sinon
                difColPos = difCol >= 0;

                //vrai si difLigne est positif, faux sinon
                difLignePos = difLigne >= 0;

                //itère pour le nombre de case ENTRE le départ et la case visé
                for (int delta = 1; delta < Math.abs(difCol); delta++) {

                    coordsOnItinery.add(new Coord((char) (this.getColonne() + (difColPos ? -delta : delta)),
                            this.getLigne() + (difLignePos ? -delta : delta)));

                }
            }
        }

        return coordsOnItinery;
    }

    /**
     * Return the list of coords that the pawn **COULD** move to
     * It's doesn't check if a pawn is already at a coord
     * @return list of coord that the pawn can move to
     */
    public abstract Collection<Coord> getValidCoords();

    @Override
    public int compareTo(PieceModel o) {
        int ret;
        if (this.getLigne() == o.getLigne()) {
            if (this.getColonne() < o.getColonne()) {
                ret = -1;
            } else if (this.getColonne() > o.getColonne()) {
                ret = 1;
            } else {
                ret = 0;
            }
        } else {
            ret = -Integer.compare(this.getLigne(), o.getLigne());
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPieceModel that = (AbstractPieceModel) o;
        return Objects.equals(coord, that.coord) && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        int ret;

        ret = this.getColonne();
        ret *= 100; //un char vaut au max 3 chiffres
        ret += this.getLigne();
        ret *= ModelConfig.LENGTH;
        ret += this.getPieceColor() == PieceSquareColor.WHITE ? 1 : 2;

        return ret;
    }

    @Override
    public String toString() {
        return "AbstractPieceModel{" +
                "coord=" + coord +
                ", pieceColor=" + pieceColor +
                '}';
    }
}
