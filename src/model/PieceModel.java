package model;


import java.util.List;

import nutsAndBolts.PieceSquareColor;

public interface PieceModel {

    /**
     * @return the coord
     */
    char getColonne();

    int getLigne();

    /**
     * @param coord
     * @return true si la pièce est aux coordonnées passées en paramètre
     */
    boolean hasThisCoord(Coord coord);

    /**
     * @param coord the coord to set
     *              le déplacement d'une pièce change ses coordonnées
     */
    void move(Coord coord);


    /**
     * @return the pieceColor
     */
    PieceSquareColor getPieceColor();


    /**
     * @param targetCoord
     * @param isPieceToCapture
     * @return true si le déplacement est légal
     */
    boolean isMoveOk(Coord targetCoord, boolean isPieceToCapture);

    /**
     * @param targetCoord
     * @return liste des coordonnées des cases traversées par itinéraire de déplacement
     */
    List<Coord> getCoordsOnItinerary(Coord targetCoord);
}

