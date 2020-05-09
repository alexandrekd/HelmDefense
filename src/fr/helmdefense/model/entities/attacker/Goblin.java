package fr.helmdefense.model.entities.attacker;

import fr.helmdefense.model.entities.utils.Location;

public class Goblin extends Attacker {

	public Goblin(int x, int y) {
		super(new Location(x, y), "goblin");
	}

	public Goblin(Location loc) {
		super(loc, "goblin");
	}
}
