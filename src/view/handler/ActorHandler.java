package view.handler;

import authoring.controller.AuthoringController;
import authoring.model.actors.Actor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import view.map.MapViewManager;
import view.visual.AbstractVisual;

/**
 * This class essentially creates a) the context menu of an actor's imageview to enable on-screen editing
 * and b) creates the filter states that then occur when a certain editing choice is made by the user.
 * 
 * @author Bridget
 *
 */
public class ActorHandler extends AbstractVisual {
	private AuthoringController myController;
	private MapViewManager viewManager;
	private ToolBar myToolbar;
	private Label defaultLabel;
	
	public ActorHandler(Group layout, AuthoringController ac, ToolBar tb) {
		myController = ac;
		viewManager = new MapViewManager(layout);
		myToolbar = tb;
		findResources();
		defaultLabel = makeLabel(myResources.getString("defaultPrompt"));
		restoreToolbar();
	}
	
	public void addActor(Actor a, double x, double y) {
		ActorView av = new ActorView(a, x, y);
		addActor(av, x, y);
	}
	
	public void addActor(ActorView av, double x, double y) {
		ImageView image = av.getImageView();
		ContextMenu cm = makeContextMenu(av);
		image.setOnContextMenuRequested(e -> {
			cm.show(image, e.getScreenX(), e.getScreenY());
		});
		viewManager.addElements(image);
	}
	
	private ContextMenu makeContextMenu(ActorView a) {
		ContextMenu cm = new ContextMenu();
		MenuItem moveActor = makeMenuItem(myResources.getString("move"), event -> moveActorFilter(a));
		MenuItem copyActor = makeMenuItem(myResources.getString("copy"), event -> copyActor(a));
		MenuItem rotateActor = makeMenuItem(myResources.getString("rotate"), event -> rotateActor(a));
		MenuItem resizeActor = makeMenuItem(myResources.getString("resize"), event -> resizeActor(a));
		MenuItem deleteActor = makeMenuItem(myResources.getString("delete"), event -> removeActor(a));
		MenuItem editParam = makeMenuItem(myResources.getString("editparams"), event -> editParams());
		MenuItem editShip = makeMenuItem(myResources.getString("editship"), event -> editShip());

		cm.getItems().addAll(moveActor, copyActor, rotateActor, resizeActor, deleteActor, editParam, editShip);

		return cm;
	}

	private MenuItem makeMenuItem(String title, EventHandler<ActionEvent> handler) {
		MenuItem res = new MenuItem(title);
		res.setOnAction(handler);
		return res;
	}

	private void moveActorFilter(ActorView a) {	
		double origX = a.getXCoor();
		double origY = a.getYCoor();
		// create the filter
		Rectangle r = makeFilterRectangle(e -> dragDrop(a, e));
		Button undo = makeButton(myResources.getString("restore"), e -> a.restoreXY(origX, origY));
		Pane spacer = makeSpacer();
		Button finish = makeFinishButton(r); 
		replaceToolbar(makeLabel(myResources.getString("moveInstru")), spacer, undo, finish);
	}
	
	private void dragDrop(ActorView a, MouseEvent e) {
		a.setXCoor(e.getX());
		a.setYCoor(e.getY());
	}
	
	private void copyActor(ActorView a) {
		ActorView aCopy = new ActorView(a);
		addActor(aCopy, aCopy.getWidth()/2, aCopy.getHeight()/2);
		moveActorFilter(aCopy);
	}
	
	private void rotateActor(ActorView a) {
		Node currNode = a.getImageView();
		double heading = currNode.getRotate();
//		TODO: currNode.setOnRotate(e -> rotateActor(e));
		
		// makeFilter
		Button enterVal = makeButton(myResources.getString("actual"), e -> rotateDialog(a, true));
		Button enterVal2 = makeButton(myResources.getString("relative"), e -> rotateDialog(a, false));
		Separator s = new Separator();
		int rotateInc = Integer.parseInt(myResources.getString("rotateIncrement"));
		Button left = makeButton(makeImage(myResources.getString("left")), e -> rotateRight(a, -1*rotateInc));
		Button right = makeButton(makeImage(myResources.getString("right")), e -> rotateRight(a, rotateInc));
		Button reset = makeButton(myResources.getString("restore"), e -> a.setRotation(heading));
		Button finish = makeFinishButton();
		Pane spacer = makeSpacer();
//		EventHandler<ActionEvent> finishHandle = event -> { TODO:?
//			viewManager.removeElements(hello); 
//			currNode.removeEventHandler(RotateEvent.ANY, e -> rotateActor(e));
//		};
		replaceToolbar(makeLabel(myResources.getString("rotateInstru")), enterVal, enterVal2, s, 
				left, right, spacer, reset, finish);
	}
	
	private void rotateRight(ActorView a, double i) {
		a.setRotation(a.getRotation() + i);
	}

	private void rotateActor(ActorView a, RotateEvent r) {
		// TODO:
		double initialHeading = a.getImageView().getRotate();
		a.setRotation(initialHeading + r.getAngle()); 
	}

	private void rotateDialog(ActorView a, boolean absolute) {
		double initialHeading = 0;

		if (!absolute) {
			initialHeading = a.getRotation();
		}

		TextInputDialog popup = new TextInputDialog();
		popup.setTitle(myResources.getString("rotate"));
		popup.setHeaderText(myResources.getString("rotateInput"));
		popup.showAndWait();

		String newVal = popup.getEditor().getText();
		try {
			double degrees = Double.parseDouble(newVal);
			a.setRotation(degrees + initialHeading);
		} catch (Exception e) {
			Alert error = new Alert(AlertType.ERROR, myResources.getString("parsedoubleerror"), ButtonType.OK);
			error.showAndWait();
		}
	}
	
	private void resizeActor(ActorView a) {
		int growInc = Integer.parseInt(myResources.getString("growIncrement"));
		Button plus = makeButton(makeImage(myResources.getString("plus")), e -> increaseActorSize(a, growInc));
		Button minus = makeButton(makeImage(myResources.getString("minus")), e -> increaseActorSize(a, -1*growInc));
		Pane spacer = makeSpacer();
		Button finish = makeFinishButton();
		
		replaceToolbar(makeLabel(myResources.getString("resizeInstru")), plus, minus, spacer, finish);
	}
	
	private void increaseActorSize(ActorView a, double increase) {
		a.addDimensions(increase);
	}

	protected void removeActor(ActorView a) {
		viewManager.removeElements(a.getImageView()); // TODO: 
	}
	
	// TODO: ...
	public void removeActor(Node element) {
		viewManager.removeElements(element);
	}
	
	private void editParams() {
		
	}
	
	private void editShip() {
		
	}
	
//	private void makeToolbar(ToolBar hello, String desc, EventHandler<ActionEvent> finishHandler, Node... options) {
//		Label instru = makeLabel(desc);
//		Button exit = makeButton("Finished", finishHandler);
//
//		hello.getItems().addAll(instru, exit);
//		for (int i = 0; i < options.length; i++) {
//			hello.getItems().add(1 + i, options[i]);
//		}
//	}
	
	private Rectangle makeFilterRectangle(EventHandler<MouseEvent> rectHandler) {
		// TODO: size & dimension of Rectangle
		Rectangle rect = new Rectangle(700, 700); 
		double opacity = Double.parseDouble(myResources.getString("opacity"));
		rect.setFill(Color.rgb(255, 0, 0, opacity));
		rect.addEventHandler(MouseEvent.MOUSE_CLICKED, rectHandler);
		viewManager.addElements(rect);
		return rect; 
	}
	
	private Label makeLabel(String desc) {
		Label l = new Label(desc);
		l.setFont(textFont);
		return l;
	}
	
	private Button makeButton(String title, EventHandler<ActionEvent> handler) {
		Button b = new Button(title);
		b.setOnAction(handler);
		b.setFont(textFont);
		return b;
	}
	
	private Button makeButton(ImageView image, EventHandler<ActionEvent> handler) {
		Button b = new Button();
		b.setOnAction(handler);
		b.setGraphic(image);
		return b;
	}
	
	private ImageView makeImage(String s) {
		Image img = new Image(getClass().getClassLoader().getResourceAsStream(s));
		ImageView image = new ImageView(img);
		image.setFitHeight(Double.parseDouble(myResources.getString("height")));
		image.setPreserveRatio(true);
		return image;
	}

	private Button makeFinishButton(Node... elementsToRemove) {
		return makeButton(myResources.getString("finish"), e -> {
			viewManager.removeElements(elementsToRemove);
			restoreToolbar();
		});
	}
	
	private Pane makeSpacer() {
		Pane spacer = new Pane(); 
		HBox.setHgrow(spacer, Priority.SOMETIMES);
		return spacer;
	}
	
	private void replaceToolbar(Node... nodes) {
		myToolbar.getItems().clear();
		for (Node n : nodes) {
			myToolbar.getItems().add(n);
		}
	}

	private void restoreToolbar() {  
		myToolbar.getItems().clear();
		myToolbar.getItems().add(defaultLabel);
	}

	// TODO:
		// input is rgb color code
		// idea is to highlight an actor when an actor is selected - through a double click method?
		// and through a second double click, actor is deselected.
//		private Rectangle selectActor(int red, int green, int blue) {
//			Image image = getImageFromActor(currActor);
//			Rectangle rect = new Rectangle(image.getWidth() + 10, image.getHeight() + 10);
//			rect.setFill(Color.rgb(255, 0, 0, 0.2));
//			return rect;
//		}

	public void updateBackground(Node n) {
		viewManager.updateBackground(n);
	}
}