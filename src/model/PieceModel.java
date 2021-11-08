package model;


import java.util.List;

import nutsAndBolts.PieceSquareColor;

public interface PieceModel extends Comparable<PieceModel> {
	
	
	/**
	 * @return the coord
	 */
	char getColonne() ;
	int getLigne() ;
	
	/**
	 * @param coord
	 * @return true si la pi�ce est aux coordonn�es pass�es en param�tre
	 */
	boolean hasThisCoord(Coord coord);
	
	/**
	 * @param coord the coord to set
	 * le d�placement d'une pi�ce change ses coordonn�es
	 */
	void move(Coord coord);


	/**
	 * @return the pieceColor
	 */
	PieceSquareColor getPieceColor() ;
	
	
	/**
	 * @param targetCoord
	 * @param isPieceToCapture
	 * @return true si le d�placement est l�gal
	 */
	boolean isMoveOk(Coord targetCoord, boolean isPieceToCapture);

	/**
	 * @param targetCoord
	 * @return liste des coordonn�es des cases travers�es par itin�raire de d�placement
	 */
	List<Coord> getCoordsOnItinerary(Coord targetCoord);

	
}

