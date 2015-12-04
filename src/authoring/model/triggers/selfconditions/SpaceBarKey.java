package authoring.model.triggers.selfconditions;

import authoring.model.actors.Actor;
import authoring.model.tree.Parameters;
import authoring.model.triggers.selftriggers.ASelfTrigger;
import player.InputManager;

public class SpaceBarKey extends ASelfTrigger {

	@Override
	public boolean condition(Parameters parameters, InputManager inputManager, Actor actor) {

		if (inputManager.getValue("SPACE")) {
			return true;
		}
		return false;
	}
}
