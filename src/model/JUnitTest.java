package model;

import nutsAndBolts.PieceSquareColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JUnitTest {

    @Test
    void testPieceSquareColor()
    {
        assertNotEquals(PieceSquareColor.WHITE, PieceSquareColor.BLACK);

        //check if GetNext work as expected,
        PieceSquareColor psc = PieceSquareColor.BLACK;
        assertEquals(psc.getNext(), PieceSquareColor.WHITE);

        psc = PieceSquareColor.WHITE;
        assertEquals(psc.getNext(), PieceSquareColor.BLACK);
    }

    @Test
    void testCoord() {
        Coord c1 = new Coord('a', 7);
        Coord c2 = new Coord('b', 3);

        assertTrue(Coord.isValidCoords(c1));
        assertFalse(Coord.isValidCoords(new Coord('w', 9)));
        assertFalse(Coord.isValidCoords(new Coord('b', 11)));
        assertNotEquals(c1, c2);
        assertEquals(c1, new Coord('a', 7));
        assertNotEquals(c1, new String("Erreur"));
        assertTrue(c1.compareTo(c2) < 0);
        assertEquals(0, c1.compareTo(new Coord('a', 7)));
        assertEquals(c1, new Coord('a', 7));
        assertNotEquals(c1, new Coord('f', 12));
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertEquals(c1.hashCode(), new Coord('a', 7).hashCode());
    }

    @Test
    void testPawnModel() {
        PieceModel pieceModel1 = new PawnModel(new Coord('a', 7), PieceSquareColor.BLACK);
        PieceModel pieceModel2 = new PawnModel(new Coord('b', 4), PieceSquareColor.WHITE);
        PieceModel pieceModel3 = new PawnModel(new Coord('e', 7), PieceSquareColor.BLACK);

        assertTrue(pieceModel1.hasThisCoord(new Coord('a', 7)));
        assertTrue(pieceModel2.hasThisCoord(new Coord('b', 4)));
        assertTrue(pieceModel3.hasThisCoord(new Coord('e', 7)));

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

        assertEquals(4, pieceModel2.getValidCoords().size());

        assertNotEquals(pieceModel1, pieceModel2);
        assertNotEquals(pieceModel2, pieceModel3);
        assertNotEquals(pieceModel1, pieceModel3);

        PieceModel pieceModel4 = new PawnModel(new Coord('d', 6), PieceSquareColor.BLACK);
        assertEquals(pieceModel3, pieceModel4);
        assertNotEquals(pieceModel1, pieceModel4);
        assertNotEquals(pieceModel2, pieceModel4);
    }

    @Test
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

        assertTrue(modelImpl.removePiece(new Coord(pm)));
        assertNull(modelImpl.findPiece(new Coord(pm)));
    }

    @Test
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

        assertEquals(PieceSquareColor.WHITE, model.getCurrentGamerColor());
        model.moveCapturePromote(new Coord('b', 4), new Coord('c', 5));
        assertTrue(model.isPieceHere(new Coord('c', 5)));

        assertEquals(PieceSquareColor.BLACK, model.getCurrentGamerColor());
        model.moveCapturePromote(new Coord('e', 7), new Coord('d', 6));
        assertTrue(model.isPieceHere(new Coord('d', 6)));

        assertEquals(PieceSquareColor.WHITE, model.getCurrentGamerColor());
        // sachant que les deux moveCapturePromote ci-dessous ne doivent pas fonctionner,
        // le tostring qui affiche le tableau du model ne dois pas bouger
        String before = model.toString();

        ArrayList<Coord> setPionACapturer = new ArrayList<>(model.isPionACapturer(new Coord('c', 5)));

        assertEquals(1, setPionACapturer.size());
        assertEquals(new Coord('d', 6), setPionACapturer.get(0));

        // vérifie si les pions noir et blanc sont bien présent dans la sortie.
        // si le test bloque ici, vérifier que la fonction tostring de Model affiche
        // bien les pions sur le plateau de jeu
        assertTrue(before.contains("B"));
        assertTrue(before.contains("N"));

        assertEquals(PieceSquareColor.WHITE, model.getCurrentGamerColor());

        model.moveCapturePromote(new Coord('c', 5), new Coord('e', 7));
        assertNotEquals(before, model.toString());

        before = model.toString();
        model.moveCapturePromote(new Coord('h', 4), new Coord('h', 5));
        assertEquals(before, model.toString());
    }

    @Test
    void testQueenModel() {
        QueenModel qmb = new QueenModel(new Coord('d', 4), PieceSquareColor.BLACK);
        QueenModel qmw = new QueenModel(new Coord('g', 8), PieceSquareColor.WHITE);

        //test déplacement en haut et en bas valide
        assertTrue(qmb.isMoveOk(new Coord('c', 5), false));
        assertTrue(qmb.isMoveOk(new Coord('e', 3), false));

        assertTrue(qmw.isMoveOk(new Coord('f', 9), false));
        assertTrue(qmw.isMoveOk(new Coord('h', 7), false));

        //déplacements invalide (déplacement de une case avec un pion à capturé)
        assertFalse(qmb.isMoveOk(new Coord('c', 5), true));
        assertFalse(qmb.isMoveOk(new Coord('e', 3), true));

        assertFalse(qmw.isMoveOk(new Coord('f', 9), true));
        assertFalse(qmw.isMoveOk(new Coord('h', 7), true));

        //déplacement de deux case avec un pion à prendre
        assertTrue(qmb.isMoveOk(new Coord('b', 6), true));
        assertTrue(qmb.isMoveOk(new Coord('f', 2), true));

        assertTrue(qmw.isMoveOk(new Coord('e', 10), true));
        assertTrue(qmw.isMoveOk(new Coord('i', 6), true));

        assertEquals(15, qmb.getValidCoords().size());

        assertTrue(qmb.isMoveOk(new Coord('a', 1), true));
        assertTrue(qmb.isMoveOk(new Coord('a', 1), false));

        assertFalse(qmb.isMoveOk(new Coord('b', 1), true));
        assertFalse(qmb.isMoveOk(new Coord('b', 1), false));
    }
}