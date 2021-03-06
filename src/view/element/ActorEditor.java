package view.element;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import authoring.controller.AuthoringController;
import authoring.model.tree.ActorTreeNode;
import authoring.model.tree.InteractionTreeNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import network.framework.GameWindow;
import network.framework.format.Request;
import network.instances.DataDecorator;
import player.SpriteManager;
import util.Sprite;
import view.actor.PropertyCell;
import view.interactions.InteractionCell;
import view.level.Workspace;
import view.screen.AbstractScreenInterface;

/**
 * @author David
 * 
 *         Allows for modification of a single actor type. Loads the actor
 *         information when they are selected in the actor browser.
 * 
 */
public class ActorEditor extends AbstractDockElement {

	private ActorBrowser browser;
	private AuthoringController controller;
	private Workspace workspace;
	private Sprite image;
	private GridPane contentPane;

	public ActorEditor(GridPane home, String title, AbstractScreenInterface screen, ActorBrowser browser,
			Workspace workspace) {
		super(home, title, screen);
		findResources();
		this.controller = null;
		this.browser = browser;
		this.workspace = workspace;
		for (ListView<String> list : browser.getLists()) {
			list.getSelectionModel().selectedItemProperty().addListener(e -> load());
		}
		makePane();
	}

	@Override
	protected void makePane() {
		pane.setMaxHeight(Double.parseDouble(myResources.getString("height")));
		pane.prefWidthProperty().bind(browser.getPane().widthProperty());
		pane.maxWidthProperty().bind(browser.getPane().widthProperty());
		pane.setAlignment(Pos.CENTER);
		pane.add(titlePane, 0, 0);
		contentPane = new GridPane();
		pane.add(contentPane, 0, 1);
		load();
		showing.setValue(false);
	}

	private void showSelector(String item) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Upload Image:");
		fileChooser.getExtensionFilters()
				.addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(new Stage());
		if (selectedFile != null) {
			String file = selectedFile.toURI().toString();
			String imageName = file.substring(file.lastIndexOf('/'));
			// add copy method
			refresh();
		}
	}

	public void refresh() {
		if (browser.getLists() != null) {
			for (ListView<String> list : browser.getLists()) {
				list.refresh();
			}
		}
		load();
	}

	private void load() {
		if (workspace.getCurrentLevel() == null) {
			controller = null;
		} else {
			this.controller = workspace.getCurrentLevel().getController();
		}
		contentPane.getChildren().clear();
		if (!showing.getValue()) {
			showing.setValue(true);
		}
		String leftItem = browser.getLists().get(0).getSelectionModel().getSelectedItem();
		String rightItem = browser.getLists().get(1).getSelectionModel().getSelectedItem();
		if (controller == null || (leftItem == null && rightItem == null)) {
			Text none = new Text(myResources.getString("none"));
			none.setFont(textFont);
			contentPane.add(none, 0, 1);
		} else if (leftItem != null && rightItem != null) {
			// external trigger
			editInteraction(leftItem, rightItem);
		} else if (leftItem != null) {
			// left list selected
			editActor(leftItem);
		} else {
			// right list selected
			editActor(rightItem);
		}
	}

	private void editInteraction(String leftItem, String rightItem) {
		contentPane.add(makeImage(leftItem), 0, 0);
		contentPane.add(makeInteractionText(leftItem, rightItem), 1, 1);
		contentPane.add(makeImage(rightItem), 2, 0);
		contentPane.add(makeTriggerEditor(leftItem, rightItem), 0, 2);
	}

	private void populateTree (TreeItem<InteractionTreeNode> frontNode, InteractionTreeNode backNode) {
		for (InteractionTreeNode backChild : backNode.children()) {
			if (!backChild.getIdentifier().equals(ActorTreeNode.class.getSimpleName())) {
				TreeItem<InteractionTreeNode> frontChild = new TreeItem<InteractionTreeNode>(backChild);
				frontNode.getChildren().add(frontChild);
				populateTree(frontChild,backChild);
			}
		}
	}
	private TreeView<InteractionTreeNode> makeTriggerEditor(String... items) {
		InteractionTreeNode branch = controller.getLevelConstructor().getTreeConstructor().getActorBaseNode(items);
		TreeItem<InteractionTreeNode> rootItem = new TreeItem<InteractionTreeNode>(branch);
		populateTree(rootItem, branch);

		rootItem.setExpanded(true);
		TreeView<InteractionTreeNode> treeView = new TreeView<InteractionTreeNode>(rootItem);
		treeView.setEditable(true);
		treeView.setCellFactory(new Callback<TreeView<InteractionTreeNode>, TreeCell<InteractionTreeNode>>() {
			@Override
			public TreeCell<InteractionTreeNode> call(TreeView<InteractionTreeNode> p) {
				InteractionCell cell = new InteractionCell(pane, controller, items);
				return cell;
			}
		});
		treeView.setFocusTraversable(false);
		GridPane.setColumnSpan(treeView, 3);
		return treeView;
	}

	private VBox makeInteractionText(String left, String right) {
		VBox box = new VBox();
		Text t1 = new Text(left);
		Text t3 = new Text(right);
		t1.setFont(textFont);
		t3.setFont(textFont);
		ImageView t2 = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("bolt.png")));
		t2.setPreserveRatio(true);
		t2.setSmooth(true);
		t2.setCache(true);
		t2.setFitHeight(Double.parseDouble(myResources.getString("boltheight")));
		box.getChildren().addAll(t1, t2, t3);
		box.setAlignment(Pos.CENTER);
		return box;
	}

	private void editActor(String item) {
		contentPane.add(makeImage(item), 0, 1);
		contentPane.add(makeName(item), 1, 1);
		contentPane.add(makePropertyEditor(item), 1, 2);
		contentPane.add(makeTriggerEditor(item), 0, 3);
	}

	private ListView<String> makePropertyEditor(String item) {
		ObservableList<String> properties = FXCollections.observableArrayList(new ArrayList<String>());
		properties.addAll(controller.getAuthoringActorConstructor().getPropertyList(item));
		ListView<String> list = new ListView<String>(properties);
		list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> list) {
				return new PropertyCell(controller, item, list);
			}
		});
		list.setFocusTraversable(false);
		return list;
	}

//	private ListView<String> makeSelfTriggerEditor(String item) {
//		ObservableList<String> triggers = FXCollections.observableArrayList(new ArrayList<String>());
//		triggers.addAll(controller.getAuthoringActorConstructor().getTriggerList(item));
//		ListView<String> list = new ListView<String>(triggers);
//		list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
//			@Override
//			public ListCell<String> call(ListView<String> list) {
//				return new TriggerCell(controller, item);
//			}
//		});
//		GridPane.setColumnSpan(list, 2);
//		list.setFocusTraversable(false);
//		return list;
//	}

	private ImageView makeImage(String item) {
		/*image = new Sprite(controller.getAuthoringActorConstructor().getDefaultPropertyValue(item, "image"));
		image.play();*/
		image = SpriteManager.createSprite(item, controller.getAuthoringActorConstructor().getDefaultPropertyValue(item, "image"));
		image.setFitHeight(Double.parseDouble(myResources.getString("imagesize")));
		image.setPreserveRatio(true);
		image.setSmooth(true);
		image.setCache(true);
		image.setOnMouseClicked(e -> showSelector(item));
		GridPane.setRowSpan(image, 2);
		return image;
	}

	private TextField makeName(String item) {
		TextField name = new TextField(item);
		name.prefWidthProperty().bind(pane.widthProperty());
		name.setFont(textFont);
		return name;
	}
}
