package model;


import java.util.Objects;

/**
 * @author francoiseperrin
 * <p>
 * Coordonnées des PieceModel
 */
public class Coord implements Comparable<Coord> {

    private final char colonne;    // ['a'..'j']
    private final int ligne;        // [10..1]
    static final int MAX = ModelConfig.LENGTH;    // 10

    public Coord(char colonne, int ligne) {
        super();
        this.colonne = colonne;
        this.ligne = ligne;
    }

    public char getColonne() {
        return colonne;
    }

    public int getLigne() {
        return ligne;
    }


    @Override
    public String toString() {
        return "[" + ligne + "," + colonne + "]";
    }


    /**
     * @param coord
     * @return true si 'a' <= col < 'a'+MAX et 1 < lig <= MAX
     */
    public static boolean coordonnees_valides(Coord coord) {
        return 'a' <= coord.colonne && coord.colonne < ('a' + MAX) && 1 < coord.ligne && coord.ligne <= MAX;
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     *
     * La méthode compareTo() indique comment comparer un objet à l'objet courant
     * selon l'ordre dit naturel
     * Dans cet application, nous décidons que l'ordre naturel est celui
     * correspondant au N° de la case d'un tableau 2D représenté par la Coord
     * ainsi le N° 1 correspond à la Coord ['a', 10], le N° 100 correspond à la Coord ['j', 1]
     */
    @Override
    public int compareTo(Coord o) {
        int curValue = (colonne - 'a') * MAX + MAX - ligne + 1;
        int oValue = (o.colonne - 'a') * MAX + MAX - o.ligne + 1;

        return curValue - oValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return colonne == coord.colonne && ligne == coord.ligne;
    }

    @Override
    public int hashCode() {
        return Objects.hash(colonne, ligne);
    }
}
