package nutsAndBolts;


public enum PieceSquareColor {

    WHITE,
    BLACK;

    /**
     * Return the next parameter for the provided enum
     *
     * @param psc color
     * @return the next one on the enum list
     */
    public static PieceSquareColor getNext(PieceSquareColor psc) {
        if (psc == WHITE)
            return BLACK;
        else
            return WHITE;
    }

    /**
     * @return The next color for this object
     */
    public PieceSquareColor getNext() {
        return getNext(this);
    }

    /**
     * @return int representation of this object
     * should return 1 if white, 2 if black
     */
    public int toInt() {
        return this == WHITE ? 1 : 2;
    }

    /**
     * Create a PieceSquareColor from its int representation
     * @param i value
     * @return corresponding PieceSquareColor object
     */
    public static PieceSquareColor fromInt(int i) {
        if (i == 1) {
            return PieceSquareColor.WHITE;
        } else if (i == 2) {
            return PieceSquareColor.BLACK;
        } else {
            throw new ArrayIndexOutOfBoundsException("value too high or too low");
        }
    }
}
