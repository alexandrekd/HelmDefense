package fr.helmdefense.model.entities.attacker.boss;

import fr.helmdefense.model.entities.utils.Location;

public class AngmarWitchKing extends Boss {
		
	public AngmarWitchKing(int x, int y, String name) {
		super(new Location(x, y),"angmar-witch-king" + name);
	}
	
	public AngmarWitchKing(Location location, String name) {
		super(location, "angmar-witch-king"+name);
	}

}
