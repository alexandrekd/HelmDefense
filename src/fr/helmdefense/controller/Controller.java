package fr.helmdefense.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import fr.helmdefense.model.entities.Entity;
import fr.helmdefense.model.entities.attackers.OrcWarrior;
import fr.helmdefense.model.entities.defenders.Archer;
import fr.helmdefense.model.entities.defenders.Catapult;
import fr.helmdefense.model.entities.defenders.ElvenShooter;
import fr.helmdefense.model.entities.defenders.HumanWarrior;
import fr.helmdefense.model.level.Level;
import fr.helmdefense.model.map.GameMap;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class Controller implements Initializable {
	private Level level;
	
	/* Header */
	// Controls buttons
    @FXML
    private Button optionButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button speedButton;
    
    // Title
    @FXML
    private Label levelNameLabel;
    
    // Money
    @FXML
    private Label moneyLabel;
    @FXML
    private ImageView moneyImage;
    
    
    /* Left Entity Infos */
    // Title
    @FXML
    private Label entityNameLabel;
    
    // Health bar
    @FXML
    private Label entityHealthPercentLabel;
    @FXML
    private ProgressBar entityHealthBar;
    @FXML
    private Label EntityHealthBarLabel;
    @FXML
    private Label entityHealthBonusLabel;
    
    // Hp bar
    @FXML
    private Label entityHpBarLabel;
    @FXML
    private ProgressBar entityHpBar;
    @FXML
    private Label entityHpBonusLabel;
    
    // Dmg bar
    @FXML
    private Label entityDmgBarLabel;
    @FXML
    private ProgressBar entityDmgBar;
    @FXML
    private Label entityDmgBonusLabel;
    
    // Mvt spd bar
    @FXML
    private Label entityMvtSpdBarLabel;
    @FXML
    private ProgressBar entityMvtSpdBar;
    @FXML
    private Label entityMvtSpdBonusLabel;
    
    // Atk spd bar
    @FXML
    private Label entityAtkSpdBarLabel;
    @FXML
    private ProgressBar entityAtkSpdBar;
    @FXML
    private Label entityAtkSpdBonusLabel;
    
    // Atk range bar
    @FXML
    private Label entityAtkRangeBarLabel;
    @FXML
    private ProgressBar entityAtkRangeBar;
    @FXML
    private Label entityAtkRangeBonusLabel;
    
    // Dist range bar
    @FXML
    private Label entityDistRangeBarLabel;
    @FXML
    private ProgressBar entityDistRangeBar;
    @FXML
    private Label entityDistRangeBonusLabel;
    
    // Money bar
    @FXML
    private Label entityMoneyLabel;
    @FXML
    private Label entityMoneyBarLabel;
    @FXML
    private ProgressBar entityMoneyBar;
    
    // Description & Abilities
    @FXML
    private TextFlow entityDescText;
    
    /* Center */
    // Board
    @FXML
    private Pane levelPane;
    
    // Map
    @FXML
    private TilePane mapPane;
    
    /* Right Entity Infos */
    // Entity list
    @FXML
    private VBox entityIDCardList;
    
    /* Footer */
    // Right text
    @FXML
    private Label buyInfoLabel;

    @FXML
    void optionButtonAction(ActionEvent event) {
    	System.out.println("Options");
    }

    @FXML
    void pauseButtonAction(ActionEvent event) {
    	System.out.println("Pause");
    }

    @FXML
    void speedButtonAction(ActionEvent event) {
    	System.out.println("Vitesse");
    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.addIDCard(HumanWarrior.class);
		this.addIDCard(Archer.class);
		this.addIDCard(ElvenShooter.class);
		this.addIDCard(Catapult.class);
		
		this.mapPane.setPrefColumns(GameMap.WIDTH);
		this.mapPane.setPrefRows(GameMap.HEIGHT);
		this.level = Level.load("test_level");
		for (int y = 0; y < GameMap.HEIGHT; y++)
			for (int x = 0; x < GameMap.WIDTH; x++)
				this.mapPane.getChildren().add(getImg("maptiles", this.level.getMap().getTile(x, y) + ".png"));
		
		ListChangeListener<Entity> lcl = c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (Entity e : c.getAddedSubList()) {
						ImageView img = getImg("entities", e.getName().replace('.', File.separatorChar) + ".png");
						img.setId(e.getId());
						e.bindX(img.translateXProperty(), x -> x.multiply(64).add(16));
						e.bindY(img.translateYProperty(), y -> y.multiply(64).add(16));
						this.levelPane.getChildren().add(img);
					}
				}
				if (c.wasRemoved()) {
					for (Entity e : c.getRemoved()) {
						this.levelPane.getChildren().remove(this.levelPane.lookup("#" + e.getId()));
					}
				}
			}
		};
		this.level.getEntities().addListener(lcl);
		
		this.level.startLoop();

		new OrcWarrior(0, 5).spawn(this.level);
		new HumanWarrior(2, 4).spawn(this.level);
	}
	
	private void addIDCard(Class<? extends Entity> type) {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../view/EntityIDCard.fxml"));
			loader.setController(new IDCardController(type));
			VBox card = loader.load();
			this.entityIDCardList.getChildren().add(card);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static ImageView getImg(String... paths) {
		return new ImageView(Paths.get(
				Paths.get(System.getProperty("user.dir"), "assets").toString(),
				paths
		).toUri().toString());
	}
}