package view.element;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import view.screen.CreatorScreen;

public abstract class AbstractDockElement extends AbstractElement {

	protected Stage stage;
	protected Label title;
	protected CreatorScreen screen;
	protected GridPane home;

	public AbstractDockElement(GridPane pane, GridPane home, String title, CreatorScreen screen) {
		super(pane);
		this.screen = screen;
		this.home = home;
		this.title = new Label(title);
		this.title.setFont(headerFont);
		this.title.setOnMouseDragged(me -> {
			screen.getScene().setCursor(Cursor.CLOSED_HAND);
		});
		this.title.setOnMouseReleased(me -> {
			screen.getScene().setCursor(Cursor.DEFAULT);
			Point2D mouseLoc = new Point2D(me.getScreenX(), me.getScreenY());
			Window window = screen.getScene().getWindow();
			Rectangle2D windowBounds = new Rectangle2D(window.getX(), window.getY(), window.getWidth(),
					window.getHeight());
			if (!windowBounds.contains(mouseLoc)) {
				launch(me.getScreenX(), me.getScreenY());
			}
		});
	}

	public void launch(double x, double y) {
		home.getChildren().clear();
		stage = new Stage();
		stage.setScene(new Scene(pane));
		stage.setTitle(title.getText());
		stage.setX(x);
		stage.setY(y);
		stage.show();
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> dock());
	}

	public void dock() {
		if (stage != null) {
			stage.close();
		}
		home.add(pane, 0, 0);
	}

	public GridPane makeLabelPane() {
		GridPane labelPane = new GridPane();
		labelPane.add(title, 0, 0);
		labelPane.setAlignment(Pos.CENTER);
		return labelPane;
	}
}
