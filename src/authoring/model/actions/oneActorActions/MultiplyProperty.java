package authoring.model.actions.oneActorActions;

import authoring.model.properties.Property;

public class MultiplyProperty extends AChangeProperty {
	@Override
	protected void ExecuteOperation(Double factor, Property<Double> property) {
		property.setValue(property.getValue() * factor);
	}
}
