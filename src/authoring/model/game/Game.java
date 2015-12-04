package authoring.model.game;

import java.io.Serializable;

import authoring.model.bundles.Bundle;
import authoring.model.level.Level;
import authoring.model.properties.Property;

/**
 * 
 * @author Austin
 *
 */

public class Game implements Serializable {
	private Bundle<Level> myLevelBundle;
	private Bundle<Property<?>> myPropertyBundle;
	
	public Game () {
		myLevelBundle = new Bundle<Level>();
		myPropertyBundle = new Bundle<Property<?>>();
	}
	
	public void addLevel (Level level) {
		myLevelBundle.add(level);
	}
	
	public Level getLevel (String levelName) {
		return myLevelBundle.get(levelName);
	}

	public void addProperty (Property<?> property) {
		myPropertyBundle.add(property);
	}
	
	public Property<?> getProperty (String propertyName) {
		return myPropertyBundle.get(propertyName);
	}
	
	public Bundle<Property<?>> getProperties(){
		return myPropertyBundle;
	}
}
