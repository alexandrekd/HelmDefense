package fr.helmdefense.model.level;

import java.util.List;

import fr.helmdefense.model.actions.entity.EntitySpawnAction;
import fr.helmdefense.model.actions.game.GameTickAction;
import fr.helmdefense.model.actions.utils.Actions;
import fr.helmdefense.model.entities.Entity;
import fr.helmdefense.model.map.GameMap;
import fr.helmdefense.utils.YAMLLoader;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Level {
	private String name;
	private GameMap map;
	private ObservableList<Entity> entities;
	private List<Wave> waves;
	private GameLoop gameloop;
	private Inventory inv;
	private ReadOnlyIntegerWrapper purseProperty;
	
	public Level(String name, GameMap map, List<Wave> waves, int startMoney) {
		this.name = name;
		this.map = map;
		this.entities = FXCollections.observableArrayList();
		ListChangeListener<Entity> lcl = c -> {
			while (c.next())
				if (c.wasAdded())
					for (Entity e : c.getAddedSubList())
						e.triggerAbilities(new EntitySpawnAction(e));
		};
		this.entities.addListener(lcl);
		this.waves = waves;
		this.gameloop = new GameLoop(ticks -> {
			Actions.trigger(new GameTickAction(this, ticks));
		});
		this.inv = new Inventory();
		this.purseProperty = new ReadOnlyIntegerWrapper(startMoney);
	}
	
	public void startLoop() {
		this.gameloop.start();
	}
	
	public String getName() {
		return this.name;
	}
	
	public GameMap getMap() {
		return this.map;
	}
	
	public ObservableList<Entity> getEntities() {
		return this.entities;
	}
	
	public List<Wave> getWaves() {
		return this.waves;
	}
	
	public Inventory getInv() {
		return this.inv;
	}
	
	public boolean overdrawn() {
		return this.getPurse() < 0;
	}
	
	public int getPurse() {
		return this.purseProperty.get();
	}
	
	/**
	 * @return	false if overdrawn, true if money
	 * 			have been debited successfully
	 */
	public boolean debit(int value) {
		if (this.getPurse() - value < 0 || value < 0)
			return false;
		this.purseProperty.setValue(this.getPurse() - value);
		return true;
	}
	
	public void earnCoins(int value) {
		if (value < 0)
			return;
		this.purseProperty.setValue(this.getPurse() + value);
	}
	
	public ReadOnlyIntegerProperty purseProperty() {
		return this.purseProperty.getReadOnlyProperty();
	}
	
	@Override
	public String toString() {
		return "Level [name=" + name + ", map=" + map + ", entities=" + entities + ", waves=" + waves + ", gameloop="
				+ gameloop + ", inv=" + inv + ", purseProperty=" + purseProperty + "]";
	}

	public static Level load(String name) {
		return YAMLLoader.loadLevel(name);
	}
}