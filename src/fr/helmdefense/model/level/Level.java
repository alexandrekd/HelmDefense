package fr.helmdefense.model.level;

import java.util.ArrayList;
import java.util.List;

import fr.helmdefense.model.actions.ActionHandler;
import fr.helmdefense.model.actions.ActionListener;
import fr.helmdefense.model.actions.entity.EntitySpawnAction;
import fr.helmdefense.model.actions.game.GameAttackerPassedAction;
import fr.helmdefense.model.actions.game.GameLooseAction;
import fr.helmdefense.model.actions.game.GameNewWaveAction;
import fr.helmdefense.model.actions.game.GameTickAction;
import fr.helmdefense.model.actions.game.GameWinAction;
import fr.helmdefense.model.actions.utils.Actions;
import fr.helmdefense.model.entities.Entity;
import fr.helmdefense.model.entities.EntitySide;
import fr.helmdefense.model.entities.living.LivingEntity;
import fr.helmdefense.model.entities.living.LivingEntityType;
import fr.helmdefense.model.entities.living.special.Door;
import fr.helmdefense.model.entities.projectile.ProjectileType;
import fr.helmdefense.model.entities.utils.DamageCause;
import fr.helmdefense.model.map.GameMap;
import fr.helmdefense.utils.yaml.YAMLLoader;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class Level implements ActionListener {
	private String name;
	private GameMap map;
	private ObservableSet<Entity> entities;
	private List<Door> doors;
	private List<Wave> waves;
	private int currentWave;
	private GameLoop gameloop;
	private Inventory inv;
	private ReadOnlyIntegerWrapper purseProperty;
	private Difficulty difficulty;
	private ReadOnlyIntegerWrapper livesProperty;
	private int totalLives;
	
	public Level(String name, GameMap map, List<Door> doors, List<Wave> waves, int startMoney, int lives) {
		this.name = name;
		this.map = map;
		this.entities = FXCollections.observableSet();
		SetChangeListener<Entity> scl = c -> {
			if (c.wasAdded())
				c.getElementAdded().triggerAbilities(new EntitySpawnAction(c.getElementAdded()));
		};
		this.entities.addListener(scl);
		this.doors = doors;
		this.waves = waves;
		this.currentWave = -1;
		this.gameloop = new GameLoop(ticks -> {
			Actions.trigger(new GameTickAction(this, ticks));
		});
		this.inv = new Inventory();
		this.purseProperty = new ReadOnlyIntegerWrapper(startMoney);
		this.setDifficulty(Difficulty.DEFAULT);
		this.totalLives = lives;
		this.livesProperty = new ReadOnlyIntegerWrapper(lives);
	}
	
	public void startLoop() {
		for (Door door : this.doors)
			door.spawn(this);
		
		this.gameloop.start();
		
		Actions.registerListeners(this);
	}
	
	public void end() {
		this.gameloop.stop();
		List<Entity> list = new ArrayList<Entity>(this.entities);
		for (Entity e : list)
			e.dispawn();
		
		for (LivingEntityType type : LivingEntityType.values())
			if (type.getData() != null)
				type.getData().resetTiers();
		for (ProjectileType type : ProjectileType.values())
			if (type.getData() != null)
				type.getData().resetTiers();
		
		Actions.unregisterAllListeners();
	}
	
	@ActionHandler
	public void onTick(GameTickAction action) {
		if (this.currentWave != -1) {
			if (! this.waves.get(this.currentWave).isEnded())
				return;
		}
		else {
			if (action.getTicks() == Wave.TICKS_BEFORE_FIRST_WAVE)
				this.startWave(null, this.waves.get(0));
			return;
		}
		
		if (action.getTicks() - this.waves.get(this.currentWave).getEndTick() == Wave.TICKS_BETWEEN_EACH_WAVE) {
			this.startWave(this.waves.get(this.currentWave), this.currentWave + 1 < this.waves.size() ? this.waves.get(this.currentWave + 1) : null);
		}
	}
	
	@ActionHandler
	public void onAttackerPass(GameAttackerPassedAction action) {
		if (action.getAttacker().getType().getSubType() == LivingEntityType.SubType.CLASSIC)
			this.livesProperty.set(this.getLives() - 1);
		else
			this.livesProperty.set(-1);
		
		if (this.getLives() < 0) {
			GameLooseAction loose = new GameLooseAction(this);
			
			this.gameloop.stop();
			
			Actions.trigger(loose);
		}
	}
	
	private void startWave(Wave o, Wave n) {
		GameNewWaveAction wave = new GameNewWaveAction(this, o, n);
		
		if (n != null) {
			n.start(this);
			this.currentWave++;
		}
		else {
			GameWinAction action = new GameWinAction(this);
			
			Actions.trigger(action);
			
			Actions.unregisterListeners(this);
		}
		
		Actions.trigger(wave);
	}
	
	public String getName() {
		return this.name;
	}
	
	public GameMap getMap() {
		return this.map;
	}
	
	public ObservableSet<Entity> getEntities() {
		return this.entities;
	}
	
	public int getTotalDoors() {
		return this.doors.size();
	}
	
	public int getAliveDoors() {
		return (int) this.doors.stream().filter(door -> door.isAlive()).count();
	}
	
	public List<Wave> getWaves() {
		return this.waves;
	}
	
	public Wave getCurrentWave() {
		return this.currentWave < 0 || this.currentWave >= this.waves.size() ? null : this.waves.get(this.currentWave);
	}
	
	public GameLoop getGameloop() {
		return this.gameloop;
	}
	
	public Inventory getInv() {
		return this.inv;
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
	
	public Difficulty getDifficulty() {
		return this.difficulty;
	}
	
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty == null ? Difficulty.DEFAULT : difficulty;
		for (LivingEntityType type : LivingEntityType.values())
			if (type.getSide() == EntitySide.ATTACKER)
				type.getData().setTier(this.difficulty.getTier(), this);
	}
	
	public int getLives() {
		return this.livesProperty.get();
	}
	
	public ReadOnlyIntegerProperty livesProperty() {
		return this.livesProperty.getReadOnlyProperty();
	}
	
	public int getTotalLives() {
		return this.totalLives;
	}
	
	@Override
	public String toString() {
		return "Level [name=" + name + ", map=" + map + ", entities=" + entities + ", waves=" + waves + ", gameloop="
				+ gameloop + ", inv=" + inv + ", purseProperty=" + purseProperty + "]";
	}

	public static Level load(String name) {
		return YAMLLoader.loadLevel(name);
	}
	
	public static class EndDamageCause implements DamageCause {
		private static EndDamageCause instance;
		
		private EndDamageCause() {}
		
		public static EndDamageCause getInstance() {
			if (instance == null)
				instance = new EndDamageCause();
			
			return instance;
		}
		
		@Override
		public void attack(LivingEntity victim) {
			victim.looseHp(victim.getHp(), this, true);
		}
		
		public static void attackWithInstance(LivingEntity victim) {
			getInstance().attack(victim);
		}
	}
}