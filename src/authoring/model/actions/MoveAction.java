package authoring.model.actions;

import authoring.model.actors.Actor;
import authoring.model.properties.Property;

public class MoveAction implements IAction{

	@Override
	public void run(Actor actor) {
		@SuppressWarnings("unchecked")
		Property<Integer> health = (Property<Integer>) actor.getProperties().getComponents().get("health");
		Integer h = health.getValue();
		health.setValue(++h);
		System.out.println("Health: "+health.getValue());
	}

	@Override
	public String getUniqueID() {
		return getClass().getName();
	}
}
