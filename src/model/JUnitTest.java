package model;

import model.Coord;
import model.ModelImplementor;
import model.PawnModel;
import model.PieceModel;
import nutsAndBolts.PieceSquareColor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JUnitTest {

    /*@org.junit.jupiter.api.Test
    void getCoordsOnItinerary() {
    }*/

    @org.junit.jupiter.api.Test
    void testCoord() {
        Coord c1 = new Coord('a', 7);
        Coord c2 = new Coord('b', 3);

        assertTrue(Coord.coordonnees_valides(c1));
        assertFalse(Coord.coordonnees_valides(new Coord('w', 9)));
        assertFalse(Coord.coordonnees_valides(new Coord('b', 11)));
        assertNotEquals(c1, c2);
        assertEquals(c1, new Coord('a', 7));
        assertNotEquals(c1, new String("Erreur"));
        assertTrue(c1.compareTo(c2) < 0);
        assertEquals(0, c1.compareTo(new Coord('a', 7)));
    }

    @org.junit.jupiter.api.Test
    void testPawnModel() {
        PieceModel pieceModel1 = new PawnModel(new Coord('a', 7), PieceSquareColor.BLACK);
        PieceModel pieceModel2 = new PawnModel(new Coord('b', 4), PieceSquareColor.WHITE);
        PieceModel pieceModel3 = new PawnModel(new Coord('e', 7), PieceSquareColor.BLACK);

        pieceModel1.move(new Coord('b', 6));

        assertTrue(pieceModel2.isMoveOk(new Coord('c', 5), false));
        assertTrue(pieceModel3.isMoveOk(new Coord('d', 6), false));

        pieceModel2.move(new Coord('c', 5));
        pieceModel3.move(new Coord('d', 6));

        assertTrue(pieceModel2.isMoveOk(new Coord('e', 7), true));
        assertFalse(pieceModel2.isMoveOk(new Coord('d', 6), true));
        assertFalse(pieceModel2.isMoveOk(new Coord('b', 6), true));
        assertFalse(pieceModel2.isMoveOk(new Coord('e', 7), false));
        assertTrue(pieceModel2.hasThisCoord(new Coord('c', 5)));

        /////////////////////////////////
        // pieceModel 1 est en B6 !!!!!!
        /////////////////////////////////

        List<Coord> li = pieceModel1.getCoordsOnItinerary(new Coord('f', 8));
        assertTrue(li.isEmpty());

        li = pieceModel1.getCoordsOnItinerary(new Coord('c', 7));
        assertTrue(li.isEmpty());

        li = pieceModel1.getCoordsOnItinerary(new Coord('b', 10));
        assertTrue(li.isEmpty());

        li = pieceModel1.getCoordsOnItinerary(new Coord('f', 6));
        assertTrue(li.isEmpty());

        li = pieceModel1.getCoordsOnItinerary(new Coord('d', 8));
        assertEquals(1, li.size());
        assertTrue(li.contains(new Coord('c', 7)));

        li = pieceModel1.getCoordsOnItinerary(new Coord('f', 10));
        assertEquals(3, li.size());
        assertTrue(li.contains(new Coord('c', 7)));
        assertTrue(li.contains(new Coord('d', 8)));
        assertTrue(li.contains(new Coord('e', 9)));
    }

    @org.junit.jupiter.api.Test
    void testModelImplementor() {
        ModelImplementor modelImpl = new ModelImplementor();
        PieceModel pm;

        pm = modelImpl.findPiece(new Coord('b', 4));
        assertEquals(4, pm.getLigne());
        assertEquals('b', pm.getColonne());
        assertEquals(PieceSquareColor.WHITE, pm.getPieceColor());
        assertNull(modelImpl.findPiece(new Coord('b', 6)));

        assertEquals(PieceSquareColor.WHITE, modelImpl.getPieceColor(new Coord('b', 4)));
        assertNull(modelImpl.getPieceColor(new Coord('b', 6)));

        assertTrue(modelImpl.isPiecehere(new Coord('b', 4)));
        assertFalse(modelImpl.isPiecehere(new Coord('b', 6)));

        assertTrue(modelImpl.isMovePieceOk(new Coord('b', 4), new Coord('c', 5), false));
        assertTrue(modelImpl.movePiece(new Coord('b', 4), new Coord('c', 5)));
    }

    @org.junit.jupiter.api.Test
    void testModel() {
        Model model = new Model();    // constructeur crée model et l'affiche

        assertTrue(model.isPieceMoveable(new Coord('b', 4), new Coord('c', 5)));
        assertFalse(model.isPieceMoveable(new Coord('c', 7), new Coord('d', 6))); // false, tour des blancs
        assertFalse(model.isPieceMoveable(new Coord('c', 3), new Coord('d', 4)));    // false, case occupée
        assertFalse(model.isPieceMoveable(new Coord('b', 4), new Coord('w', 12)));    // false, hors damier

        assertTrue(model.isMovePiecePossible(new Coord('b', 4), new Coord('c', 5), false));    // true

        model.movePiece(new Coord('b', 4), new Coord('c', 5));    // move OK visible sur affichage
        assertTrue(model.isPieceHere(new Coord('c', 5)));

        model.switchGamer(); // Changement joueur - c'est au tour des noirs
        assertTrue(model.isPieceMoveable(new Coord('c', 7), new Coord('d', 6)));    // true c'est bien au joueur noir de jouer
        assertFalse(model.isMovePiecePossible(new Coord('c', 7), new Coord('c', 6), false));    // false pas déplacement en diagonale

        // on recrée un objet Model sur lequel aucun mouvement n'a été effectué
        // pour tester méthode publique moveCapturePromote()
        // normalement elle doit être fonctionnelle sans modification si vous avez bien testé tout ce qui précède
        model = new Model();

        model.moveCapturePromote(new Coord('b', 4), new Coord('c', 5));
        assertTrue(model.isPieceHere(new Coord('c', 5)));

        model.moveCapturePromote(new Coord('e', 7), new Coord('d', 6));
        assertTrue(model.isPieceHere(new Coord('d', 6)));

        // sachant que les deux moveCapturePromote ci-dessous ne doivent pas fonctionner,
        // le tostring qui affiche le tableau du model ne dois pas bouger
        String before = model.toString();

        // vérifie si les pions noir et blanc sont bien présent dans la sortie.
        // si le test bloque ici, vérifier que la fonction tostring de Model affiche
        // bien les pions sur le plateau de jeu
        assertTrue(before.contains("B"));
        assertTrue(before.contains("N"));

        model.moveCapturePromote(new Coord('c', 5), new Coord('e', 7));
        assertNotEquals(before, model.toString());

        model.moveCapturePromote(new Coord('h', 4), new Coord('h', 5));
        assertNotEquals(before, model.toString());
    }
}