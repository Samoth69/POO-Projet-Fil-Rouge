package model;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import nutsAndBolts.PieceSquareColor;

/**
 * @author francoise.perrin
 * <p>
 * Cete classe fabrique et stocke toutes les PieceModel du Model dans une collection
 * elle est donc responsable de rechercher et mettre � jour les PieceModel (leur position)
 * En r�alit�, elle d�l�gue � une fabrique le soin de fabriquer les bonnes PieceModel
 * avec les bonnes coordonn�es
 * <p>
 * En revanche, elle n'est pas responsable des algorithme m�tiers li�s au d�placement des pi�ces
 * (responsabilit� de la classe Model)
 */
public class ModelImplementor implements Promotable {

    // la collection de pi�ces en jeu - m�lange noires et blanches
    //private Collection<PieceModel> pieces ;
    private final HashMap<Coord, AbstractPieceModel> pieces = new HashMap<>();

    public ModelImplementor() {
        super();

        for (AbstractPieceModel pm : ModelFactory.createPieceModelCollection()) {
            this.pieces.put(new Coord(pm.getColonne(), pm.getLigne()), pm);
        }
    }

    public PieceSquareColor getPieceColor(Coord coord) {
        PieceModel pm = this.pieces.get(coord);
        if (pm != null) {
            return pm.getPieceColor();
        } else {
            return null;
        }
    }

    public boolean isPiecehere(Coord coord) {
        return this.pieces.containsKey(coord);
    }

    public boolean isMovePieceOk(Coord initCoord, Coord targetCoord, boolean isPieceToTake) {
        boolean isMovePieceOk = false;

        PieceModel pm = this.pieces.get(initCoord);
        if (pm != null) {
            isMovePieceOk = pm.isMoveOk(targetCoord, isPieceToTake);
        }

        return isMovePieceOk;
    }

    /**
     * @param initCoord   coordonn�es de d�part
     * @param targetCoord coordon�es d'arriver
     * @return vrai si le d�placement est effectu�, faux si targetcoord est d�j� occup� par un pion ou initcoord ne contiens pas de pion
     * @author Thomas Violent
     */
    public boolean movePiece(Coord initCoord, Coord targetCoord) {

        boolean isMovePieceDone = false;

        AbstractPieceModel pm = findPiece(initCoord);
        if (pm != null) {
            this.pieces.remove(initCoord);
            pm.move(targetCoord);
            this.pieces.put(targetCoord, pm);
            isMovePieceDone = true;

            if (pm.isPromotable()) {
                this.promote();
            }
        }

        return isMovePieceDone;
    }

    public void removePiece(Coord pieceToTakeCoord) {
        this.pieces.remove(pieceToTakeCoord);
    }


    public List<Coord> getCoordsOnItinerary(Coord initCoord, Coord targetCoord) {
        return findPiece(initCoord).getCoordsOnItinerary(targetCoord);
    }


    /**
     * @param coord
     * @return la pi�ce qui se trouve aux coordonn�es indiqu�es. return null if not found
     */
    AbstractPieceModel findPiece(Coord coord) {        // TODO : mettre en "private" apr�s test unitaires
        return this.pieces.get(coord);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     *
     * La m�thode toStrong() retourne une repr�sentation
     * de la liste de pi�ces sous forme d'un tableau 2D
     *
     */
    public String toString() {


        String st = "";
        String[][] damier = new String[ModelConfig.LENGTH][ModelConfig.LENGTH];

        // cr�ation d'un tableau 2D avec les noms des pi�ces � partir de la liste de pi�ces
        for (PieceModel piece : this.pieces.values()) {

            PieceSquareColor color = piece.getPieceColor();
            String stColor = (PieceSquareColor.WHITE.equals(color) ? "--B--" : "--N--");

            int col = piece.getColonne() - 'a';
            int lig = piece.getLigne() - 1;
            damier[lig][col] = stColor;
        }

        // Affichage du tableau formatt�
        st = "     a      b      c      d      e      f      g      h      i      j\n";
        for (int lig = 9; lig >= 0; lig--) {
            st += (lig + 1) + "  ";
            for (int col = 0; col <= 9; col++) {
                String stColor = damier[lig][col];
                if (stColor != null) {
                    st += stColor + "  ";
                } else {
                    st += "-----  ";
                }
            }
            st += "\n";
        }

        return "\nDamier du model \n" + st;
    }

    @Override
    public boolean isPromotable() {
        return false;
    }

    @Override
    public void promote() {
        HashSet<PawnModel> toPromoteList = new HashSet<>();
        for (AbstractPieceModel pm : pieces.values()) {
            if (pm.isPromotable()) {
                toPromoteList.add((PawnModel) pm);
            }
        }

        for (PawnModel pm : toPromoteList) {
            QueenModel qm = new QueenModel(new Coord(pm.getColonne(), pm.getLigne()), pm.getPieceColor());
            this.pieces.replace(new Coord(pm.getColonne(), pm.getLigne()), qm);
        }
    }
}
