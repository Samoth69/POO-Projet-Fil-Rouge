package model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import controller.OutputModelData;
import nutsAndBolts.PieceSquareColor;

/**
 * @author francoise.perrin
 * <p>
 * Cette classe gère les aspects métiers du jeu de dame
 * indépendamment de toute vue
 * <p>
 * Elle délègue à son objet ModelImplementor
 * le stockage des PieceModel dans une collection
 * <p>
 * Les pièces sont capables de se déplacer d'une case en diagonale
 * si la case de destination est vide
 * <p>
 * Ne sont pas gérés les prises, les rafles, les dames,
 * <p>
 * N'est pas géré le fait que lorsqu'une prise est possible
 * une autre pièce ne doit pas être jouée
 */
public class Model implements BoardGame<Coord> {

    private PieceSquareColor currentGamerColor;    // couleur du joueur courant

    private ModelImplementor implementor;        // Cet objet sait communiquer avec les PieceModel

    public Model() {
        super();
        this.implementor = new ModelImplementor();
        this.currentGamerColor = ModelConfig.BEGIN_COLOR;

        System.out.println(this);
    }

    @Override
    public String toString() {
        return implementor.toString();
    }


    /**
     * Actions potentielles sur le model : move, capture, promotion pion, rafles
     */
    @Override
    public OutputModelData<Coord> moveCapturePromote(Coord toMovePieceCoord, Coord targetSquareCoord) {

        OutputModelData<Coord> outputModelData = null;

        boolean isMoveDone = false;
        Coord toCapturePieceCoord = null;
        Coord toPromotePieceCoord = null;
        PieceSquareColor toPromotePieceColor = null;
        PieceSquareColor toMovePieceColor = null;

        // Si la pièce est déplaçable (couleur du joueur courant et case arrivée disponible)
        if (this.isPieceMoveable(toMovePieceCoord, targetSquareCoord)) {

            // S'il n'existe pas plusieurs pièces sur le chemin
            if (this.isThereMaxOnePieceOnItinerary(toMovePieceCoord, targetSquareCoord)) {

                //Recherche coord de l'éventuelle pièce à prendre
                toCapturePieceCoord = this.getToCapturePieceCoord(toMovePieceCoord, targetSquareCoord);

                // si le déplacement est légal (en diagonale selon algo pion ou dame)
                boolean isPieceToCapture = toCapturePieceCoord != null;
                if (this.isMovePiecePossible(toMovePieceCoord, targetSquareCoord, isPieceToCapture)) {

                    // déplacement effectif de la pièce
                    this.movePiece(toMovePieceCoord, targetSquareCoord);
                    isMoveDone = true;

                    // suppression effective de la pièce prise
                    this.remove(toCapturePieceCoord);

                    // promotion éventuelle de la pièce après déplacement
                    if (this.implementor.findPiece(targetSquareCoord) instanceof PawnModel pm) {
                        if (pm.isPromotable()) {
                            this.implementor.promote(new Coord(pm));
                            toPromotePieceColor = pm.getPieceColor();
                            toPromotePieceCoord = new Coord(pm);
                        }
                    }

                    // S'il n'y a pas eu de prise
                    // ou si une rafle n'est pas possible alors changement de joueur
                    if (!isPionACapturer(implementor.findPiece(targetSquareCoord))) {    // TODO : Test à changer atelier 4
                        this.switchGamer();
                    }
                }
            }
        }
        System.out.println(this);

        // Constitution objet de données avec toutes les infos nécessaires à la view
        outputModelData = new OutputModelData<Coord>(
                isMoveDone,
                toCapturePieceCoord,
                toPromotePieceCoord,
                toPromotePieceColor);

        return outputModelData;

    }

    /**
     * @param toMovePieceCoord
     * @param targetSquareCoord
     * @return true si la PieceModel à déplacer est de la couleur du joueur courant
     * et que les coordonnées d'arrivées soient dans les limites du tableau
     * et qu'il n'y ait pas de pièce sur la case d'arrivée
     */
    boolean isPieceMoveable(Coord toMovePieceCoord, Coord targetSquareCoord) { // TODO : remettre en "private" après test unitaires
        boolean bool = false;

        // TODO : à compléter atelier 4 pour gérer les rafles

        bool = this.implementor.isPiecehere(toMovePieceCoord)
                && this.implementor.getPieceColor(toMovePieceCoord) == this.currentGamerColor
                && Coord.coordonneesValides(targetSquareCoord)
                && !this.implementor.isPiecehere(targetSquareCoord);

        return bool;
    }

    /**
     * @param start point de départ (non compris)
     * @param end   point d'arrivée (non compris)
     * @return pion entre les deux points
     * @author Thomas Violent
     * Renvoie les coordonnées avec
     */
    private List<Coord> getPawnOnItinerary(Coord start, Coord end) {
        ArrayList<Coord> liCo = new ArrayList<>();

        PieceModel pm = implementor.findPiece(start);
        if (pm != null) {
            List<Coord> coords = pm.getCoordsOnItinerary(end);
            for (Coord c : coords) {
                if (implementor.isPiecehere(c)) {
                    liCo.add(c);
                }
            }
        }

        return liCo;
    }

    /**
     * @param toMovePieceCoord
     * @param targetSquareCoord
     * @return true s'il n'existe qu'1 seule pièce à prendre d'une autre couleur sur la trajectoire
     * ou pas de pièce à prendre
     */
    private boolean isThereMaxOnePieceOnItinerary(Coord toMovePieceCoord, Coord targetSquareCoord) {
        return getPawnOnItinerary(toMovePieceCoord, targetSquareCoord).size() <= 1;
    }

    /**
     * @param toMovePieceCoord
     * @param targetSquareCoord
     * @return les coord de la pièce à prendre, null sinon
     */
    private Coord getToCapturePieceCoord(Coord toMovePieceCoord, Coord targetSquareCoord) {
        Coord ret = null;

        List<Coord> liCoord = getPawnOnItinerary(toMovePieceCoord, targetSquareCoord);

        if (!liCoord.isEmpty()) {
            ret = liCoord.get(0);
        }

        return ret;
    }

    /**
     * @param isPieceToCapture
     * @return true si le déplacement est légal
     * (s'effectue en diagonale, avec ou sans prise)
     * La PieceModel qui se trouve aux coordonnées passées en paramètre
     * est capable de répondre à cette question (par l'intermédiare du ModelImplementor)
     */
    boolean isMovePiecePossible(Coord toMovePieceCoord, Coord targetSquareCoord, boolean isPieceToCapture) { // TODO : remettre en "private" après test unitaires
        return this.implementor.isMovePieceOk(toMovePieceCoord, targetSquareCoord, isPieceToCapture);
    }

    /**
     * @param toMovePieceCoord
     * @param targetSquareCoord Déplacement effectif de la PieceModel
     */
    void movePiece(Coord toMovePieceCoord, Coord targetSquareCoord) { // TODO : remettre en "private" après test unitaires
        this.implementor.movePiece(toMovePieceCoord, targetSquareCoord);
    }

    /**
     * @param toCapturePieceCoord Suppression effective de la pièce capturée
     */
    private void remove(Coord toCapturePieceCoord) {
        this.implementor.removePiece(toCapturePieceCoord);
    }

    void switchGamer() { // TODO : remettre en "private" après test unitaires
        this.currentGamerColor = (PieceSquareColor.WHITE).equals(this.currentGamerColor) ?
                PieceSquareColor.BLACK : PieceSquareColor.WHITE;

    }

    public boolean isPieceHere(Coord target) {
        return this.implementor.isPiecehere(target);
    }

    private boolean isPionACapturer(PieceModel pm) {
        //TODO
//        Collection<PieceModel> potentialPawn = new HashSet<>();
//        for (Coord c : pm.getValidCoords()){
//            if (isPieceHere(c)) {
//                potentialPawn.add(implementor.findPiece(c));
//            }
//        }
//
//        PieceModel center = pm;
//
//        for (PieceModel pm : potentialPawn) {
//
//        }
//
//        for (int i = 1; i < ModelConfig.LENGTH; i++) {
//            Coord[] coords = {
//                    new Coord((char) (pm.getColonne() - i), pm.getLigne() - i),
//                    new Coord((char) (pm.getColonne() - i), pm.getLigne() + i),
//                    new Coord((char) (pm.getColonne() + i), pm.getLigne() - i),
//                    new Coord((char) (pm.getColonne() + i), pm.getLigne() + i),
//            };
//            for (int tbi = 0; tbi < 4 && Coord.coordonneesValides(coords[tbi]); tbi++)
//            {
//                Coord c = coords[i];
//                if ()
//            }
//        }
        return false;
    }
}