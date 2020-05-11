package fr.helmdefense.model.entities.defenders;

import fr.helmdefense.model.entities.utils.Location;

public class ElvenWarrior extends Elven {
	
	public ElvenWarrior(Location loc) {
		super(loc);
	}
	
	public ElvenWarrior(int x, int y) {
		this(new Location(x,y));
	}

}
