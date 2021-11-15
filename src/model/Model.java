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
 * Cette classe g�re les aspects m�tiers du jeu de dame
 * ind�pendamment de toute vue
 * <p>
 * Elle d�l�gue � son objet ModelImplementor
 * le stockage des PieceModel dans une collection
 * <p>
 * Les pi�ces sont capables de se d�placer d'une case en diagonale
 * si la case de destination est vide
 * <p>
 * Ne sont pas g�r�s les prises, les rafles, les dames,
 * <p>
 * N'est pas g�r� le fait que lorsqu'une prise est possible
 * une autre pi�ce ne doit pas �tre jou�e
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

        // Si la pi�ce est d�pla�able (couleur du joueur courant et case arriv�e disponible)
        if (this.isPieceMoveable(toMovePieceCoord, targetSquareCoord)) {

            // S'il n'existe pas plusieurs pi�ces sur le chemin
            if (this.isThereMaxOnePieceOnItinerary(toMovePieceCoord, targetSquareCoord)) {

                //Recherche coord de l'�ventuelle pi�ce � prendre
                toCapturePieceCoord = this.getToCapturePieceCoord(toMovePieceCoord, targetSquareCoord);

                // si le d�placement est l�gal (en diagonale selon algo pion ou dame)
                boolean isPieceToCapture = toCapturePieceCoord != null;
                if (this.isMovePiecePossible(toMovePieceCoord, targetSquareCoord, isPieceToCapture)) {

                    // d�placement effectif de la pi�ce
                    this.movePiece(toMovePieceCoord, targetSquareCoord);
                    isMoveDone = true;

                    // suppression effective de la pi�ce prise
                    this.remove(toCapturePieceCoord);

                    // promotion �ventuelle de la pi�ce apr�s d�placement
                    if (this.implementor.findPiece(targetSquareCoord) instanceof PawnModel pm) {
                        if (pm.isPromotable()) {
                            this.implementor.promote(new Coord(pm));
                            toPromotePieceColor = pm.getPieceColor();
                            toPromotePieceCoord = new Coord(pm);
                        }
                    }

                    // S'il n'y a pas eu de prise
                    // ou si une rafle n'est pas possible alors changement de joueur
                    if (!isPionACapturer(implementor.findPiece(targetSquareCoord))) {    // TODO : Test � changer atelier 4
                        this.switchGamer();
                    }
                }
            }
        }
        System.out.println(this);

        // Constitution objet de donn�es avec toutes les infos n�cessaires � la view
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
     * @return true si la PieceModel � d�placer est de la couleur du joueur courant
     * et que les coordonn�es d'arriv�es soient dans les limites du tableau
     * et qu'il n'y ait pas de pi�ce sur la case d'arriv�e
     */
    boolean isPieceMoveable(Coord toMovePieceCoord, Coord targetSquareCoord) { // TODO : remettre en "private" apr�s test unitaires
        boolean bool = false;

        // TODO : � compl�ter atelier 4 pour g�rer les rafles

        bool = this.implementor.isPiecehere(toMovePieceCoord)
                && this.implementor.getPieceColor(toMovePieceCoord) == this.currentGamerColor
                && Coord.coordonneesValides(targetSquareCoord)
                && !this.implementor.isPiecehere(targetSquareCoord);

        return bool;
    }

    /**
     * @param start point de d�part (non compris)
     * @param end   point d'arriv�e (non compris)
     * @return pion entre les deux points
     * @author Thomas Violent
     * Renvoie les coordonn�es avec
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
     * @return true s'il n'existe qu'1 seule pi�ce � prendre d'une autre couleur sur la trajectoire
     * ou pas de pi�ce � prendre
     */
    private boolean isThereMaxOnePieceOnItinerary(Coord toMovePieceCoord, Coord targetSquareCoord) {
        return getPawnOnItinerary(toMovePieceCoord, targetSquareCoord).size() <= 1;
    }

    /**
     * @param toMovePieceCoord
     * @param targetSquareCoord
     * @return les coord de la pi�ce � prendre, null sinon
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
     * @return true si le d�placement est l�gal
     * (s'effectue en diagonale, avec ou sans prise)
     * La PieceModel qui se trouve aux coordonn�es pass�es en param�tre
     * est capable de r�pondre �cette question (par l'interm�diare du ModelImplementor)
     */
    boolean isMovePiecePossible(Coord toMovePieceCoord, Coord targetSquareCoord, boolean isPieceToCapture) { // TODO : remettre en "private" apr�s test unitaires
        return this.implementor.isMovePieceOk(toMovePieceCoord, targetSquareCoord, isPieceToCapture);
    }

    /**
     * @param toMovePieceCoord
     * @param targetSquareCoord D�placement effectif de la PieceModel
     */
    void movePiece(Coord toMovePieceCoord, Coord targetSquareCoord) { // TODO : remettre en "private" apr�s test unitaires
        this.implementor.movePiece(toMovePieceCoord, targetSquareCoord);
    }

    /**
     * @param toCapturePieceCoord Suppression effective de la pi�ce captur�e
     */
    private void remove(Coord toCapturePieceCoord) {
        this.implementor.removePiece(toCapturePieceCoord);
    }

    void switchGamer() { // TODO : remettre en "private" apr�s test unitaires
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