package authoring.model.actions.oneActorActions;

import authoring.files.properties.ActorProperties;
import authoring.model.actions.AOneActorAction;
import authoring.model.actors.Actor;
import authoring.model.properties.Property;
import authoring.model.tree.Parameters;
import engine.State;
import player.InputManager;

public class RotateClockwise extends AOneActorAction {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -401773162575161667L;

	@SuppressWarnings("rawtypes")
	@Override
	public void run(InputManager inputManeger, Parameters parameters, State state, Actor actor) {
		Double rotation = 20.0;
		Property<Double> angle = (Property<Double>) actor.getProperty(ActorProperties.ANGLE.getKey());
		angle.setValue((angle.getValue() - rotation) % 360);		
	}
}
