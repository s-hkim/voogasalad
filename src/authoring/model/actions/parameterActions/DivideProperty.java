package authoring.model.actions.parameterActions;

import authoring.model.actions.AParameterAction;
import authoring.model.actors.Actor;
import authoring.model.properties.Property;
import engine.State;

public class DivideProperty<V> extends AParameterAction<V> {
	@Override
	public void run(Property<?> property, V value, State state, Actor a) {
		Double divisor = (Double) value;
		property.setValue((Double)property.getValue() / divisor);
	}
}
