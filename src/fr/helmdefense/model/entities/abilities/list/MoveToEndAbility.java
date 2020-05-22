package fr.helmdefense.model.entities.abilities.list;

import fr.helmdefense.model.actions.ActionHandler;
import fr.helmdefense.model.actions.entity.EntitySpawnAction;
import fr.helmdefense.model.actions.game.GameTickAction;
import fr.helmdefense.model.entities.Entity;
import fr.helmdefense.model.entities.abilities.Ability;
import fr.helmdefense.model.entities.utils.Tier;
import fr.helmdefense.model.entities.utils.coords.Location;
import fr.helmdefense.model.entities.utils.coords.Vector;
import fr.helmdefense.model.level.GameLoop;
import fr.helmdefense.model.map.Cell;

public class MoveToEndAbility extends Ability {
	private Entity entity;
	private Cell movingTo;
	
	public MoveToEndAbility(Tier unlock) {
		super(unlock);
	}
	
	@ActionHandler
	public void onSpawn(EntitySpawnAction action) {
		this.entity = action.getEntity();
		this.movingTo = this.entity.getLevel().getMap().getGraph().getCellAt(this.entity.getLoc()).getNext();
	}
	
	@ActionHandler
	public void onTick(GameTickAction action) {
		if (this.entity == null || this.movingTo == null)
			return;
		
		Location loc = this.entity.getLoc(), l = this.movingTo.getLoc().center();
		Vector v = new Vector(loc, l);
		double d = l.distance(loc);
		v.multiply(this.entity.data().getStats(Tier.TIER_1).getMvtSpd() / GameLoop.TPS / d);
		this.entity.teleport(loc.add(v));
		if (v.length() >= d)
			this.movingTo = this.movingTo.getNext();
	}
}