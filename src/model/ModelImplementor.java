package model;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import nutsAndBolts.PieceSquareColor;

/**
 * @author francoise.perrin
 * <p>
 * Cete classe fabrique et stocke toutes les PieceModel du Model dans une collection
 * elle est donc responsable de rechercher et mettre à jour les PieceModel (leur position)
 * En réalité, elle délègue à une fabrique le soin de fabriquer les bonnes PieceModel
 * avec les bonnes coordonnées
 * <p>
 * En revanche, elle n'est pas responsable des algorithme métiers liés au déplacement des pièces
 * (responsabilité de la classe Model)
 */
public class ModelImplementor implements Promotable {

    // la collection de pièces en jeu - mélange noires et blanches
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
     * @param initCoord   coordonnées de départ
     * @param targetCoord coordonées d'arriver
     * @return vrai si le déplacement est effectué, faux si targetcoord est déjà occupé par un pion ou initcoord ne contiens pas de pion
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
     * @return la pièce qui se trouve aux coordonnées indiquées. return null if not found
     */
    AbstractPieceModel findPiece(Coord coord) {        // TODO : mettre en "private" après test unitaires
        return this.pieces.get(coord);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     *
     * La méthode toStrong() retourne une représentation
     * de la liste de pièces sous forme d'un tableau 2D
     *
     */
    public String toString() {


        String st = "";
        String[][] damier = new String[ModelConfig.LENGTH][ModelConfig.LENGTH];

        // création d'un tableau 2D avec les noms des pièces à partir de la liste de pièces
        for (PieceModel piece : this.pieces.values()) {

            PieceSquareColor color = piece.getPieceColor();
            String stColor = (PieceSquareColor.WHITE.equals(color) ? "--B--" : "--N--");

            int col = piece.getColonne() - 'a';
            int lig = piece.getLigne() - 1;
            damier[lig][col] = stColor;
        }

        // Affichage du tableau formatté
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
