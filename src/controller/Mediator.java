package controller;

import controller.localController.Network;
import gui.View;
import model.BoardGame;
import model.Coord;

import java.util.UUID;

/**
 * @author francoise.perrin
 * Le Controller fait le lien entre laView et le Model 
 * qui ne se connaissent pas
 * 
 */
public interface Mediator {

	UUID clientUUID = UUID.randomUUID();

	void setView(View view) ;
	void setModel(BoardGame<Coord> model) ;
	void setNetwork(Network n);
}
