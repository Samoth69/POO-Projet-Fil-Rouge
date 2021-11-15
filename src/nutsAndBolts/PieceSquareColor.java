package nutsAndBolts;



public enum PieceSquareColor {

	WHITE,
	BLACK;

	/**
	 * Return the next parameter for the provided enum
	 * @param psc color
	 * @return the next one on the enum list
	 */
	public static PieceSquareColor getNext(PieceSquareColor psc)
	{
		if (psc == WHITE)
			return BLACK;
		else
			return WHITE;
	}

	/**
	 *
	 * @return The next color for this object
	 */
	public PieceSquareColor getNext()
	{
		return getNext(this);
	}
}
