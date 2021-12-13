package model;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import controller.OutputModelData;
import controller.localController.Controller;
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

                //vrai si une pi�ce � �t� captur� par l'utilisateur
                boolean isPieceToCapture = toCapturePieceCoord != null;

                HashSet<Coord> potentialPawnToCapture = new HashSet<>(isPionACapturer(toMovePieceCoord));

                if (this.isMovePiecePossible(toMovePieceCoord, targetSquareCoord, potentialPawnToCapture.size() > 0)) {
                    // s'il n'y a pas de pion � capturer
                    // ou qu'il y a un pion � capturer que l'utilisateur � choisi
                    if (potentialPawnToCapture.size() == 0 || potentialPawnToCapture.contains(toCapturePieceCoord)) {
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

                        HashSet<Coord> pawnOnDest = new HashSet<>(isPionACapturer(targetSquareCoord));
                        pawnOnDest.remove(toCapturePieceCoord);
                        if (!isPieceToCapture || pawnOnDest.size() <= 0) {
                            this.switchGamer();
                            System.out.println("SWITCH GAMER");
                        }
                    }
                }


            }
        }
        System.out.println(this);

        // Constitution objet de donn�es avec toutes les infos n�cessaires � la view
        outputModelData = new OutputModelData<Coord>(
                isMoveDone,
                toMovePieceCoord,
                targetSquareCoord,
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
                && Coord.isValidCoords(targetSquareCoord)
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

    /**
     * Cherche si un pion autour de la source peut �tre captur�
     *
     * @param source point de d�part
     * @return coordonn�es du ou des pions � capturer aux alentours
     */
    public Set<Coord> isPionACapturer(Coord source) {
        HashSet<Coord> ret = new HashSet<>();
        Coord[] pts = new Coord[]{
                new Coord((char) (source.getColonne() - 1), source.getLigne() - 1),
                new Coord((char) (source.getColonne() - 1), source.getLigne() + 1),
                new Coord((char) (source.getColonne() + 1), source.getLigne() - 1),
                new Coord((char) (source.getColonne() + 1), source.getLigne() + 1),
        };

        int orientation = 0;

        for (Coord c : pts) {
            if (c.isValidCoords()) {
                if (implementor.isPiecehere(c)) {
                    Coord nextCoord;
                    PieceModel pm = implementor.findPiece(c);

                    if (pm.getPieceColor() != currentGamerColor) {
                        switch (orientation) {
                            case 0 -> nextCoord = new Coord((char) (c.getColonne() - 1), c.getLigne() - 1);
                            case 1 -> nextCoord = new Coord((char) (c.getColonne() - 1), c.getLigne() + 1);
                            case 2 -> nextCoord = new Coord((char) (c.getColonne() + 1), c.getLigne() - 1);
                            case 3 -> nextCoord = new Coord((char) (c.getColonne() + 1), c.getLigne() + 1);
                            default -> throw new IllegalStateException("Unexpected value: " + orientation);
                        }
                        // si les coords sont valides
                        // ET que cette coord est vide (pas de pion � cet endroit)
                        if (nextCoord.isValidCoords() && !implementor.isPiecehere(nextCoord)) {
                            ret.add(c);
                        }
                    }

                }
            }
            orientation++;
        }

        return ret;
    }

    public PieceSquareColor getCurrentGamerColor() {
        return currentGamerColor;
    }

    public void setCurrentGamerColor(PieceSquareColor currentGamerColor) {
        this.currentGamerColor = currentGamerColor;
    }
}