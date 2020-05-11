package fr.helmdefense.model.level;

import java.util.List;

import fr.helmdefense.model.entities.Entity;
import fr.helmdefense.model.map.GameMap;
import fr.helmdefense.utils.YAMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Level {
	private GameMap map;
	private ObservableList<Entity> entities;
	private List<Wave> waves;
	
	public Level(GameMap map, List<Wave> waves) {
		this.map = map;
		this.entities = FXCollections.observableArrayList();
		this.waves = waves;
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
	
	@Override
	public String toString() {
		return "Level [map=" + map + ", entities=" + entities + ", waves=" + waves + "]";
	}

	public static Level load(String name) {
		return YAMLLoader.loadLevel(name);
	}
}