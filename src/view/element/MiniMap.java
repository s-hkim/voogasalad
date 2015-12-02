package view.element;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MiniMap {
	private StackPane theMiniMap;
	private ImageView theBackground;
	private ImageView miniMapImageView;
	private ScrollPane theMapArea;
	
	private double miniMapWidth;
	private double miniMapHeight;
	
	private double currentScale;
	private double currentOpacity;
	private Rectangle currentRectangle;
	private double currentRectangleXPos;
	private double currentRectangleYPos;

	public MiniMap(ImageView background, ScrollPane mapArea) {
		theMiniMap = new StackPane();
		theBackground = background;
		theMapArea = mapArea;
		currentScale = 1.0;
		currentOpacity = 0.5;
		currentRectangleXPos = 0;
		currentRectangleYPos = 0;
		createMiniMap();
	}

	public void updateMiniMapRectangleOnZoom(double scale) {
		double rectangleScale = 1 / scale;
		currentScale = rectangleScale;
		createMiniMapRectangle(miniMapWidth, miniMapHeight, currentScale, currentOpacity);
		addMiniMapRectangle(currentRectangleXPos, currentRectangleYPos);
	}

	public void updateMiniMapOpacity(double opacity) {
		miniMapImageView.setOpacity(opacity);
		currentRectangle.setStroke(Color.rgb(255, 0, 0, opacity));;
	}
	
	public void updateMiniMapBackground(ImageView background) {
		//Not sure if this will properly update it
		miniMapImageView = background;
	}

	private void createMiniMap() {
		miniMapImageView = new ImageView(theBackground.getImage());
		miniMapImageView.setOpacity(currentOpacity);
		miniMapImageView.setFitWidth(200);
		miniMapImageView.setPreserveRatio(true);
		StackPane.setAlignment(miniMapImageView, Pos.BOTTOM_RIGHT);
		theMiniMap.getChildren().add(miniMapImageView);
		miniMapWidth = (double) miniMapImageView.getBoundsInParent().getWidth();
		miniMapHeight = (double) miniMapImageView.getBoundsInParent().getHeight();

		createMiniMapRectangle(miniMapWidth, miniMapHeight, currentScale, currentOpacity);
		addMiniMapRectangle(currentRectangleXPos, currentRectangleYPos);
	}
	
	private void createMiniMapRectangle(double width, double height, double scale, double opacity) {
		Rectangle rect = new Rectangle(width * scale, height * scale);
		rect.setFill(Color.TRANSPARENT);
		rect.setStroke(Color.rgb(255, 0, 0, opacity));
		rect.setStrokeWidth(3);
		StackPane.setAlignment(rect, Pos.TOP_LEFT);
		currentRectangle = rect;
	}
	
	private void addMiniMapRectangle(double xPos, double yPos) {
		if(theMiniMap.getChildren().size() > 1) {
			theMiniMap.getChildren().remove(1);
		}
		currentRectangle.setTranslateX(xPos);
		currentRectangle.setTranslateY(yPos);
		theMiniMap.getChildren().add(currentRectangle);
	}

	public void updateMiniMapRectangleOnHorizontalPan(double new_value) {
		 double maxHorizontalMovement = miniMapWidth * (1 - currentScale);
		 double newRectangleXPos = new_value * maxHorizontalMovement;
		 currentRectangleXPos = newRectangleXPos;
		 addMiniMapRectangle(currentRectangleXPos, currentRectangleYPos);
	}

	public void updateMiniMapRectangleOnVerticalPan(double new_value) {
		double maxVerticalMovement = miniMapHeight * (1 - currentScale);
		double newRectangleYPos = new_value * maxVerticalMovement;
		currentRectangleYPos = newRectangleYPos;
		addMiniMapRectangle(currentRectangleXPos, currentRectangleYPos);
	}

	public StackPane getMiniMap() {
		return theMiniMap;
	}
}
