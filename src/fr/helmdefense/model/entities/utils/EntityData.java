package fr.helmdefense.model.entities.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.helmdefense.model.entities.abilities.Ability;
import fr.helmdefense.model.entities.utils.coords.Hitbox.Size;

public class EntityData {
	private String name;
	private String path;
	private Size size;
	private Map<Tier, Statistic> stats;
	private Map<Class<? extends Ability>, List<Object>> abilities;
	
	public EntityData(String name, String path, Size size, Map<Tier, Statistic> stats, Map<Class<? extends Ability>, List<Object>> abilities) {
		this.name = name;
		this.path = path;
		this.size = size;
		this.stats = stats;
		this.abilities = abilities;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final String getPath() {
		return this.path;
	}

	public Size getSize() {
		return this.size;
	}
	
	public final Statistic getStats(Tier tier) {
		return this.stats.get(tier);
	}

	public List<Ability> instanciateAbilities() {
		return this.abilities.entrySet().stream()
				.map(e -> {
					try {
						return e.getKey().getConstructor(getClasses(e.getValue())).newInstance(e.getValue().toArray());
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
						e1.printStackTrace();
						return null;
					}
				})
				.collect(Collectors.toList());
	}
	
	private static Class<?>[] getClasses(List<Object> objects) {
		List<Object> list = objects.stream()
				.map(o -> o.getClass())
				.collect(Collectors.toList());
		Class<?>[] array = new Class<?>[list.size()];
		for (int i = 0; i < list.size(); i++)
			array[i] = (Class<?>) list.get(i);
		return array;
	}

	@Override
	public String toString() {
		return "EntityData [name=" + name + ", path=" + path + ", stats=" + stats + ", abilities=" + abilities + "]";
	}
}