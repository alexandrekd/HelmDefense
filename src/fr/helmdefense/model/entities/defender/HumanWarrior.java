package fr.helmdefense.model.entities.defender;

import fr.helmdefense.model.entities.utils.Location;

public class HumanWarrior extends Defender {
	
	public HumanWarrior(Location loc) {
		super(loc, "human-warrior");
	}
	
	public HumanWarrior(int x, int y) {
		super(new Location(x,y), "human-warrior");
	}
	
	

}
