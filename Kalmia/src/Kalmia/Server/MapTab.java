package Kalmia.Server;

import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;


public class MapTab extends Tab{
	private String path;
	private ImageView background,map;
	public MapTab(String p){
		StackPane root = new StackPane();
		root.setAlignment(Pos.CENTER);
		path = p;
		background = new ImageView(new Image("file:"+ path + "map_background.png"));
		map = new ImageView(new Image("file:"+ path + "Building_Map.png"));
		setText("Map");
		setClosable(false);
		
		background.setPreserveRatio(true);
		background.setSmooth(true);
		background.setCache(true);
		background.setFitWidth(1000);
		map.setPreserveRatio(true);
		map.setSmooth(true);
		map.setCache(true);
		map.setFitWidth(1700);
		map.setTranslateX(-500);
		map.setTranslateY(-200);
		map.getFitHeight();
		map.getFitWidth();
		
		
		//root.getChildren().add();
		root.getChildren().addAll(background,map);
		setContent(root);
		background.setFitWidth(root.getWidth());
		//this.getGraphic().getScene()=scene;
	}
}