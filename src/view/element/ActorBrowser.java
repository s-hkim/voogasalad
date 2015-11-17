package view.element;

import java.util.ArrayList;

import authoring.controller.AuthoringController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import view.actor.ActorCell;
import view.screen.AbstractScreenInterface;

public class ActorBrowser extends AbstractDockElement {

	private ListView<String> rightlist;
	private ListView<String> leftlist;
	private ArrayList<ListView<String>> lists;
	private ObservableList<String> actors;
	private BooleanProperty doubleLists;
	private AuthoringController controller;

	public ActorBrowser(GridPane pane, GridPane home, String title, AbstractScreenInterface screen,
			AuthoringController controller) {
		super(pane, home, title, screen);
		doubleLists = new SimpleBooleanProperty(true);
		doubleLists.addListener(e -> toggleDoubleLists(doubleLists.getValue()));
		this.controller = controller;
		makePane();
	}

	@Override
	protected void makePane() {
		GridPane labelPane = makeLabelPane();
		pane.add(labelPane, 0, 0);
		GridPane.setColumnSpan(labelPane, 2);
		actors = FXCollections.observableArrayList(new ArrayList<String>());
		actors.addAll(controller.getAuthoringConfigManager().getActorList());
		rightlist = new ListView<String>(actors);
		leftlist = new ListView<String>(actors);
		pane.add(leftlist, 0, 1);
		pane.add(rightlist, 1, 1);
		pane.setAlignment(Pos.TOP_CENTER);
		leftlist.prefHeightProperty().bind(screen.getScene().heightProperty());
		rightlist.prefHeightProperty().bind(screen.getScene().heightProperty());
		leftlist.setFocusTraversable(false);
		rightlist.setFocusTraversable(false);
		configure(leftlist);
		configure(rightlist);
		lists = new ArrayList<ListView<String>>();
		lists.add(leftlist);
		lists.add(rightlist);
	}

	private void configure(ListView<String> list) {
		list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> list) {
				return new ActorCell(controller);
			}
		});
	}

	public void addNewActor() {
		//actors.add("Actor " + actors.size());
		//TODO: add backend implementation
	}

	public BooleanProperty getDoubleListsProperty() {
		return doubleLists;
	}

	private void toggleDoubleLists(Boolean value) {
		if (value) {
			pane.add(rightlist, 1, 1);
		} else {
			rightlist.getSelectionModel().clearSelection();
			pane.getChildren().remove(rightlist);
		}
	}

	public ArrayList<ListView<String>> getLists() {
		return lists;
	}

}
