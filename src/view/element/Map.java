package view.element;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import view.screen.AbstractScreen;

public class Map extends AbstractElement {
	private AbstractScreen screen;
	private Group contentGroup;
	private Group zoomGroup;

	private StackPane layout;
	private ScrollPane mapArea;
	private MapZoomSlider slider;
	private Image backgroundImage;
	private Rectangle map;

	public Map(GridPane pane, AbstractScreen screen) {
		super(pane);
		findResources();
		this.screen = screen;

		// Use a StackPane so we can layer things on top of one another, like an
		// actor
		// over a background tile. Note that you can probably use something else
		// if a
		// StackPane is not appropriate, such as a Canvas.
		layout = new StackPane();
		mapArea = new ScrollPane();
		makePane();

	}

	public void addMapElements(Node... elements) {
		layout.getChildren().addAll(elements);
	}

	public void createTheMap() {
		createMapArea();
		slider = new MapZoomSlider(zoomGroup, Double.valueOf(myResources.getString("sliderwidth")));
		slider.createTheSlider();
		pane.add(mapArea, 0, 0);
		pane.add(slider.getTheSlider(), 0, 1);
	}

	private void createMapArea() {
		createGroups();
		createScrollPane();
	}

	private void createScrollPane() {
		mapArea = new ScrollPane();

		// Hide the vertical and horizontal scrollbars, make the pane pannable
		mapArea.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapArea.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapArea.setPannable(true);

		// Bind the preferred size of the scroll area to the size of the scene
		mapArea.prefWidthProperty().bind(screen.getScene().widthProperty());
		mapArea.prefHeightProperty().bind(screen.getScene().heightProperty());

		mapArea.setContent(contentGroup);
	}

	private void createGroups() {
		// Groups all the nodes that will appear on the map and allows them to
		// be
		// collectively rescaled using a Scale.
		zoomGroup = new Group();

		// Contains the zoomGroup that will then be displayed on the map
		contentGroup = new Group();

		contentGroup.getChildren().add(zoomGroup);
		zoomGroup.getChildren().add(layout);
	}

	public void setMapTransform(Scale scaleTransform) {
		zoomGroup.getTransforms().clear();
		zoomGroup.getTransforms().add(scaleTransform);
	}

	@Override
	protected void makePane() {
		// Test Narnia map image
		backgroundImage = new Image("http://www.narniaweb.com/wp-content/uploads/2009/08/NarniaMap.jpg");
		ImageView background = new ImageView(backgroundImage);

		// Test white rectangle
		map = new Rectangle();
		map.widthProperty().bind(screen.getScene().widthProperty());
		map.heightProperty().bind(screen.getScene().heightProperty());
		map.setFill(Color.WHITE);

		// Add any elements you want to appear on the map using this method
		addMapElements(map);

		// Create the map after adding elements you want
		createTheMap();
	}
}
