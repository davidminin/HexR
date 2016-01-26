package com.hexrfull.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class ScreenManager {

	public String state = "Main Menu";
	Boolean inTrophyTab = true; // variable used to tell which tab in the paused menu the game is in
	Boolean trophySelected = false;
	public boolean gamePurchased = false;
	public boolean soundOn = true;
	public boolean writeTrophy = true; // variable used to tell main java class to write trophy file
	public boolean gameOver = false;
	public int highscore = 0;
	public int skinIndex = 0;
	int tempSkinIndex = 0;
	
	GoogleServices googleServices;
	public boolean useGoogleService;
	public String googleAction;
	
	public Texture buttonSheet;
	public Texture trophySheet;
	public Texture[] skinSheets = new Texture[5];
    ShapeRenderer renderer;
	
    public Button highScoreButton;
	public Trophy[] trophies = new Trophy[37];
	public int rank = -1;
	
	public MainGame mainGame;
	public Stage stage;
	
	
	// Holds all the possible screens 
	public ScreenManager() {
    	String repr = Gdx.graphics.getWidth() + "*" + Gdx.graphics.getHeight();
    	float ratio = DetermineRatio(repr);
    	OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth() / ratio, Gdx.graphics.getHeight() / ratio);
    	camera.setToOrtho(true);
    	stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));

		state = "Main Game";
    	renderer = new ShapeRenderer();
		mainGame = new MainGame();
		
		mainGame.create();
    	String extension = mainGame.extension + ".png";
    	
    	buttonSheet = new Texture(Gdx.files.internal("ButtonSheet" + extension), true);
    	trophySheet = new Texture(Gdx.files.internal("TrophySheet" + extension), true);
		buttonSheet.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		trophySheet.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		
    	skinSheets[0] = new Texture(Gdx.files.internal("HexSheet" + extension), true);
    	skinSheets[1] = new Texture(Gdx.files.internal("HexSheetGems" + extension), true);
    	skinSheets[2] = new Texture(Gdx.files.internal("HexSheetWood" + extension), true);
    	skinSheets[3] = new Texture(Gdx.files.internal("HexSheetCube" + extension), true);
    	
		skinSheets[0].setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		skinSheets[1].setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		skinSheets[2].setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		skinSheets[3].setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		
		CreateOptionBarButtons(true);
		mainGame.stage.addActor(mainGame.banner);
		UpdateSkin();
	}
	
	// Determines which screen to render
	public void render(){
		gameOver = mainGame.gameOver;
		Gdx.gl.glEnable(GL20.GL_BLEND);
    	renderer.begin(ShapeType.Filled);
    	
    	Color start = new Color(129/255f, 181/255f, 210/255f, 1f);
    	Color end = new Color(125/255f, 213/255f, 229/255f, 1f);
    	
    	// Draw options bar at the top of the screen
    	renderer.setColor(new Color(191/255f, 221/255f, 235/255f, 1f));
    	renderer.rect(0, Gdx.graphics.getHeight() - (int)(mainGame.IMG_SIZE * 1.5), Gdx.graphics.getWidth(), (int)(mainGame.IMG_SIZE * 1.5),
    			end, end, start, start);
        
    	
    	// Draws the trophy banner background if applicable
    	TrophyBanner b = mainGame.banner;
    	if(b.timer > 0){
    		renderer.setColor(b.color);
    		renderer.rect(0, Gdx.graphics.getHeight() - b.getY() - b.getHeight() -1, Gdx.graphics.getWidth(), b.getHeight() + 1);
    	}
    	
    	renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        // High score is beat
		if(mainGame.points > highscore){
			highscore = mainGame.points;
			writeTrophy = true;
		}
        
		// Trophy Unlocks
		if(mainGame.gameOver && rank == 1){
			mainGame.unlockTrophyIndex = 3;
		}
		else if(mainGame.gameOver && rank == 1 && mainGame.points >= 5000){
			mainGame.unlockTrophyIndex = 5;
		}
		else if(mainGame.gameOver && rank == 2 && mainGame.points == 0){
			mainGame.unlockTrophyIndex = 10;
		}
		else if(mainGame.gameOver && rank == 5 && mainGame.points >= 15000){
			mainGame.unlockTrophyIndex = 21;
		}
		else if(mainGame.gameOver && rank == 8 && mainGame.points >= 25000){
			mainGame.unlockTrophyIndex = 34;
		}
		else if(rank == 10 && !trophies[trophies.length - 1].isCompleted()){
			mainGame.unlockTrophyIndex = 36;
		}
		
		// Runs trophy unlock code
		if(mainGame.unlockTrophyIndex != -1){
			int index = mainGame.unlockTrophyIndex;
			
			// Only checks if the trophy has not previously been completed
			if(!trophies[index].isCompleted()){
				trophies[index].completion -= mainGame.unlockAmount;
				
				if(trophies[index].isCompleted()){
					UnlockTrophy(mainGame.unlockTrophyIndex);
					
					try{
						googleServices.unlockTrophy(mainGame.unlockTrophyIndex);
					}
					catch(Exception e){
					}
				}
			}
			
			mainGame.unlockTrophyIndex = -1;
			mainGame.unlockAmount = 1;
			mainGame.rank = rank;
		} 
		
		// Draw option menu tabs
		if(mainGame.paused && !mainGame.gameOver){
			Gdx.gl.glEnable(GL20.GL_BLEND);
	    	renderer.begin(ShapeType.Filled);
	    	
	    	start.set(new Color(106/255f, 195/255f, 227/255f, 1f));
	    	
	    	if(inTrophyTab == null){ // in the settings menu
	    		renderer.rect(2 * Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() - (int)(mainGame.IMG_SIZE * 2.5), Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE);
	    		renderer.rect(0, Gdx.graphics.getHeight() - (int)(mainGame.IMG_SIZE * 2.5), 2 * Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE,
	    				end, end, start, start);
	    		
	    		renderer.setColor(new Color(0.7f, 0.7f, 0.7f, 0.3f));
	    		renderer.circle(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + mainGame.IMG_SIZE / 8.2f + 2, mainGame.IMG_SIZE * 3.8f);
	    	}
	    	
	    	else if(inTrophyTab){ // in the trophy menu
	    		renderer.rect(0, Gdx.graphics.getHeight() - (int)(mainGame.IMG_SIZE * 2.5), Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE);
	    		renderer.rect(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() - (int)(mainGame.IMG_SIZE * 2.5), 2 * Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE,
	    				end, end, start, start);
	    		
	    		// Draw selected trophy background
	    		if(trophySelected){
	    			float startY = (mainGame.GetGridHex(4, 14).getY() - mainGame.IMG_SIZE * 7 * 0.955f) / 2;
	    			
	    			renderer.setColor(Color.BLACK);
	    			renderer.rect(mainGame.IMG_SIZE - 3, startY - 3, Gdx.graphics.getWidth() - mainGame.IMG_SIZE * 2 + 6, mainGame.IMG_SIZE * 2 + 6);
	    			
	    			Color c = GetBackgroundColor();
	    			renderer.setColor(new Color((0.65f + c.r)/2f,(0.65f + c.g)/2f, (0.65f + c.b)/2f, 1));
	    			renderer.rect(mainGame.IMG_SIZE, startY, Gdx.graphics.getWidth() - mainGame.IMG_SIZE * 2, mainGame.IMG_SIZE * 2);
	    		}
	    	}
	    	
	    	else{ // in the skin menu
	    		renderer.rect(0, Gdx.graphics.getHeight() - (int)(mainGame.IMG_SIZE * 2.5), Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE,
	    				end, end, start, start);
	    		renderer.rect(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() - (int)(mainGame.IMG_SIZE * 2.5), Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE);
	    		renderer.rect(2 * Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() - (int)(mainGame.IMG_SIZE * 2.5), Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE,
	    				end, end, start, start);
	    	}
	    	
	    	renderer.end();
	        Gdx.gl.glDisable(GL20.GL_BLEND);
		}
	
		stage.act();
		stage.draw();
	}
	
	// Determines the ratio difference between the original image size and the one needed 
    public float DetermineRatio(String r){
    	// Base case where resolution is appropriate for dpi
    	float ratio =  Gdx.graphics.getDensity() / 2;
    	String repr = r;
    	
    	// Sometimes dpi is strange with given resolution
    	if((repr.equals("480*800") || repr.equals("480*854")) && Gdx.graphics.getDensity() <= 1.15) 
    		ratio = Gdx.graphics.getDensity() / 3;
    	
    	// Iphone quick fixes
    	if(repr.equals("640*1136") || repr.equals("640*960")) 
    		ratio = Gdx.graphics.getDensity() / 3;
    	 
    	return ratio;
    }
    
    // Updates the skin in the main game
    private void UpdateSkin(){
    	mainGame.hexSheet = skinSheets[tempSkinIndex];
    	writeTrophy = true;
    }
    
    // -----------------------------------------------
    // -----------------------------------------------
    //               Button Related Code
    // -----------------------------------------------
    // -----------------------------------------------
    
    // Touch listener used to make all buttons go off screen
    private InputListener toggleMenu = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			Actor[] buttons = stage.getActors().toArray();
			tempSkinIndex = skinIndex;
			
			// Pause the game or go to the game is over
			if(mainGame.gameOver || !mainGame.paused){
				if(mainGame.gameOver){
					mainGame = new MainGame();
					mainGame.create();
					CreateOptionBarButtons(true);
					mainGame.stage.addActor(mainGame.banner);
					UpdateSkin();
				}
				
				mainGame.pause();
				Gdx.input.setInputProcessor(stage);
				
				// add all the tab hitboxes
				Button t = new Button(0, (int)(mainGame.IMG_SIZE * 1.5), new Rectangle(-1, 0,Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE), buttonSheet);
				t.addListener(openTrophy);
				t.addListener(resetSkinIndex);
				stage.addActor(t); // add the left tab
				t = new Button(Gdx.graphics.getWidth() / 3, (int)(mainGame.IMG_SIZE * 1.5), new Rectangle(-1, 0,Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE), buttonSheet);
				t.addListener(openSkins);
				t.addListener(resetSkinIndex);
				stage.addActor(t); // add the middle tab
				t = new Button(2 * Gdx.graphics.getWidth() / 3, (int)(mainGame.IMG_SIZE * 1.5), new Rectangle(-1, 0,Gdx.graphics.getWidth() / 3, mainGame.IMG_SIZE), buttonSheet);
				t.addListener(openSettings);
				t.addListener(resetSkinIndex);
				stage.addActor(t); // add the right tab
				
				int center = Gdx.graphics.getWidth() / 3 / 2;
				
				// add icons to the tabs
				t = new Button(center - mainGame.IMG_SIZE / 2, (int)(mainGame.IMG_SIZE * 1.5), new Rectangle(mainGame.IMG_SIZE * 3 + 3, 0,mainGame.IMG_SIZE, mainGame.IMG_SIZE), buttonSheet);
				t.setTouchable(Touchable.disabled);
				stage.addActor(t); // trophy icon
				t = new Button(2 * Gdx.graphics.getWidth() / 3  - center - mainGame.IMG_SIZE / 2, (int)(mainGame.IMG_SIZE * 1.5), new Rectangle(mainGame.IMG_SIZE * 2 + 2, 0,mainGame.IMG_SIZE, mainGame.IMG_SIZE), buttonSheet);
				t.setTouchable(Touchable.disabled);
				stage.addActor(t); // skin icon
				t = new Button(Gdx.graphics.getWidth() - center - mainGame.IMG_SIZE / 2, (int)(mainGame.IMG_SIZE * 1.5), new Rectangle(mainGame.IMG_SIZE * 3 + 3, 0,mainGame.IMG_SIZE, mainGame.IMG_SIZE), buttonSheet);
				t.setTouchable(Touchable.disabled);
				stage.addActor(t); // setting icon
				
				// add the tutorial button
				//t = new Button(mainGame.IMG_SIZE / 2, Gdx.graphics.getHeight() - mainGame.IMG_SIZE - mainGame.IMG_SIZE / 2, new Rectangle(0, mainGame.IMG_SIZE + 1,mainGame.IMG_SIZE, mainGame.IMG_SIZE), buttonSheet);
				//t.addListener(startTutorial);
				//stage.addActor(t);
				
				// add the sound icon
//				Button c = new Button(Gdx.graphics.getWidth() - mainGame.IMG_SIZE - mainGame.IMG_SIZE / 2, Gdx.graphics.getHeight() - mainGame.IMG_SIZE - mainGame.IMG_SIZE / 2, 
//						new Rectangle(mainGame.IMG_SIZE * 2 + 2,mainGame.IMG_SIZE + 1,mainGame.IMG_SIZE, mainGame.IMG_SIZE), buttonSheet);
//				
//				if(!soundOn)
//					c.region = new TextureRegion(buttonSheet,  c.region.getRegionX() + mainGame.IMG_SIZE,  c.region.getRegionY(), mainGame.IMG_SIZE, mainGame.IMG_SIZE);
//				
//				c.addListener(soundToggle);
//				stage.addActor(c);

				CreateOptionBarButtons(false);
				
				if(inTrophyTab == null) // start up the settings tab
					CreateSettingsScreen();
				
				else if(inTrophyTab) // start up the trophy tab
					CreateTrophyScreen();
					
				else // start up the skins tab
					CreateSkinScreen();
			}
			else {// Otherwise return to the main game screen
				for(int i = 0; i < buttons.length; i++){
					buttons[i].remove();
				}
				
				Gdx.input.setInputProcessor(mainGame.stage);
				mainGame.resume();
				mainGame.SpawnGrid();
				trophySelected = false;
				UpdateSkin();
			}
			
			return true;
		}
    };
    
    
    // Touch listener used to reset the grid
    private InputListener resetGrid = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			mainGame.WriteGameFile(true);
	    	mainGame.stage.cancelTouchFocus(mainGame.stage.getActors().peek());
			mainGame.create();
			UpdateSkin();

			Gdx.input.setInputProcessor(mainGame.stage);
			CreateOptionBarButtons(true);
			mainGame.stage.addActor(mainGame.banner);
			mainGame.rank = rank;
            return true; 
        }
    };
    
    
    // Touch listener used to toggle the music
    private InputListener startTutorial = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			
            return true; 
        }
    };
    
    
    // Touch listener used for trophy selection
    private InputListener selectTrophy = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if(trophySelected){
				stage.getActors().pop();
			}
			
			float startY = Gdx.graphics.getHeight() - (mainGame.GetGridHex(4, 14).getY() - mainGame.IMG_SIZE * 7 * 0.955f) / 2 - mainGame.IMG_SIZE * 2;
			Trophy t = Trophy.class.cast(event.getListenerActor());
			Trophy n = new Trophy(t.name, t.description, t.completion, new Rectangle(t.region.getRegionX(),
					t.region.getRegionY(), mainGame.IMG_SIZE, mainGame.IMG_SIZE), trophySheet);
			
			if(!t.isCompleted())
				n.unfinished = t.unfinished;
			
			n.setBounds(0, 0, mainGame.IMG_SIZE * 2, mainGame.IMG_SIZE * 2);
			n.setPosition(mainGame.IMG_SIZE + mainGame.IMG_SIZE / 4.1f, startY);
			n.setTouchable(Touchable.disabled);
			stage.addActor(n);
			
			trophySelected = true;
            return true; 
        }
    };
    
    
    // Touch listener used to toggle the sound
    private InputListener soundToggle = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			writeTrophy = true;
			
			if(soundOn){ // Sound is currently on and must be turned off
				soundOn = false;
				Button b = Button.class.cast(event.getListenerActor());
				b.region = new TextureRegion(buttonSheet,  b.region.getRegionX() + mainGame.IMG_SIZE,  b.region.getRegionY(), mainGame.IMG_SIZE, mainGame.IMG_SIZE);
			
			}
			else{ // Sound is currently off and must be turned on
				soundOn = true;
				Button b = Button.class.cast(event.getListenerActor());
				b.region = new TextureRegion(buttonSheet,  b.region.getRegionX() - mainGame.IMG_SIZE,  b.region.getRegionY(), mainGame.IMG_SIZE, mainGame.IMG_SIZE);
			}
            return true; 
        }
    };
    
    // Shares the highscore
    private InputListener share = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			googleServices.submitScore(mainGame.points);
			return true; 
        }
    };
    
    // Opens the leaderboards
    private InputListener openLeaderboards = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			x = Gdx.input.getX();
			y = Gdx.input.getY();
			
			// Radius of circle 
            float radius = mainGame.IMG_SIZE * 3.8f;
            
            // Get center point of bounding circle, also known as the center of the rectangle
            float centerX = Gdx.graphics.getWidth() / 2;
            float centerY = Gdx.graphics.getHeight() / 2 + mainGame.IMG_SIZE / 8.2f + 2;

            // Attempts to avoid collision detection if point is out of bounding square
        	if(Math.abs(x - centerX) > radius || Math.abs(y - centerY) > radius) return false;
            
            // Distance of point from the center of the circle (squared)
            float distance = (float)((centerX - x) * (centerX - x)) 
                    + ((centerY - y) * (centerY - y));
            
            // It is within the circle
            if(distance <= radius * radius){
            	googleServices.showScores();
            }
			return true; 
        }
    };
    
    
    // Switches to the next skin
    private InputListener skinNext = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if(tempSkinIndex == 0)
				skinIndex = 1;
			else if(tempSkinIndex == 1 && rank >= 3)
				skinIndex = 2;
			else if(tempSkinIndex == 2 && rank >= 6)
				skinIndex = 0;
			else if(tempSkinIndex == 3)
				skinIndex = 0;
			
			
			if(tempSkinIndex == 3)
				tempSkinIndex = 0;
			else
				tempSkinIndex++;
	
			UpdateSkin();
			CreateSkinScreen();
			return true; 
        }
    };
    
    // Switches to the previous skin
    private InputListener skinBack = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if(tempSkinIndex == 0 && rank >= 6) 
				skinIndex = 3;
			else if(tempSkinIndex == 1)
				skinIndex = 0;
			else if(tempSkinIndex == 2)
				skinIndex = 1;
			else if(tempSkinIndex == 3 && rank >= 3)
				skinIndex = 2;
			
			if(tempSkinIndex != 0)
				tempSkinIndex--;
			else
				tempSkinIndex = 3;
			
			UpdateSkin();
			CreateSkinScreen();
			return true; 
        }
    };
    
    
    // Touch listener that opens the trophy screen
    private InputListener openTrophy = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if(inTrophyTab == null || !inTrophyTab)
				CreateTrophyScreen();
			return true; 
        }
    };
    
    
    // Touch listener than opens the trophy screen
    private InputListener openSkins = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if(inTrophyTab == null || inTrophyTab)
				CreateSkinScreen();
			return true; 
        }
    };
    
    // Touch listener than opens the trophy screen
    private InputListener openSettings = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			if(inTrophyTab != null)
				CreateSettingsScreen();
			return true; 
        }
    };
    
    // Touch listener than resets the temporary skin index
    private InputListener resetSkinIndex = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			tempSkinIndex = skinIndex;
			UpdateSkin();
			return true; 
        }
    };
    
    
    // Creates the settings screen
    private void CreateSettingsScreen(){
    	inTrophyTab = null;
    	trophySelected = false;
    	Actor[] buttons = stage.getActors().toArray();
    	
    	// Remove the other buttons
    	for(int i = 8; i < buttons.length; i++){
			buttons[i].remove();
		}
    	
    	// Adds the regional hit box for the leader boards
    	Button t = new Button(Gdx.graphics.getWidth() / 2 - (int)(mainGame.IMG_SIZE * 3.8f), (int)(Gdx.graphics.getHeight() / 2 + mainGame.IMG_SIZE / 8.2f + 2 
    			- (int)(mainGame.IMG_SIZE * 3.8f)), new Rectangle(-1, 0, mainGame.IMG_SIZE * 7.6f, mainGame.IMG_SIZE * 7.6f), buttonSheet);
    	t.addListener(openLeaderboards);
    	stage.addActor(t);
    	
    	// Adds the hex crown
    	ArrayList<Trophy> remainingHexs = new ArrayList<Trophy>();
		Random rand = new Random();
		
		// Populates array list
		for(int i = 0; i < trophies.length; i++){ 
			Trophy h = new Trophy("", "", 0, new Rectangle(mainGame.IMG_SIZE + 1, GetSettingSrcY(i), mainGame.IMG_SIZE, mainGame.IMG_SIZE), skinSheets[skinIndex]);
			h.setPosition(trophies[i].getX(), trophies[i].getY());
			remainingHexs.add(h);
		}
		
		// Randomizes the hex spawn order and add them to the stage
		for(int j = 0; j < trophies.length; j++){
			int index = rand.nextInt(remainingHexs.size());
			remainingHexs.get(index).sizeDivider += 18 + (int)(j*0.85);
			remainingHexs.get(index).setTouchable(Touchable.disabled);
			stage.addActor(remainingHexs.get(index));
			remainingHexs.remove(index);
		}
    }
    
    // Creates a crown pattern with the hexs by returning the corresponding srcY 
    private int GetSettingSrcY(int index){
    	if(index == 29) // red
    		return mainGame.IMG_SIZE * 2;
    	
    	else if(index == 19 || index == 13) // blue
    		return mainGame.IMG_SIZE;
    	
    	// return the grey hex srcY
    	else if(index == 0 || index == 4 || index == 34 || index == 5 || index == 11 ||
    			index == 8 || index == 6 || index == 7 || index == 20 || index == 21)
    		return -mainGame.IMG_SIZE;
    	
    	// return the yellow hex srcY
    	return mainGame.IMG_SIZE * 3;
    }
    
    // Creates the trophy screen
    private void CreateTrophyScreen(){
    	inTrophyTab = true;
    	Actor[] buttons = stage.getActors().toArray();
    	
    	// Remove the other buttons
    	for(int i = 8; i < buttons.length; i++){
			buttons[i].remove();
		}
    
		ArrayList<Trophy> remainingTrophies = new ArrayList<Trophy>();
		Random rand = new Random();
		
		// Populates array list
		for(int i = 0; i < trophies.length; i++){
			remainingTrophies.add(trophies[i]);
		}
		
		// Randomizes the trophy spawn order and add them to the stage
		for(int j = 0; j < trophies.length; j++){
			int index = rand.nextInt(remainingTrophies.size());
			remainingTrophies.get(index).sizeDivider = 18 + (int)(j*0.85);
			
			if(remainingTrophies.get(index).getListeners().size == 0)
				remainingTrophies.get(index).addListener(selectTrophy);
    		
			stage.addActor(remainingTrophies.get(index));
			remainingTrophies.remove(index);
		}
    }
    
    
    // Creates the skin screen
    private void CreateSkinScreen(){
    	inTrophyTab = false;
    	trophySelected = false;
    	Actor[] buttons = stage.getActors().toArray();
    	
    	// Remove the other buttons
    	for(int i = 8; i < buttons.length; i++){
			buttons[i].remove();
		}
    	
    	ArrayList<Trophy> remainingHexs = new ArrayList<Trophy>();
		Random rand = new Random();
		
		// Populates array list
		for(int i = 0; i < trophies.length; i++){
			int multiplier = rand.nextInt(7);
			if(multiplier == 6)
				multiplier = 0;
			
			Trophy h = new Trophy("", "", 0, new Rectangle(mainGame.IMG_SIZE + 1, mainGame.IMG_SIZE * multiplier, mainGame.IMG_SIZE, mainGame.IMG_SIZE), skinSheets[tempSkinIndex]);
			h.setPosition(trophies[i].getX(), trophies[i].getY());
			remainingHexs.add(h); 
		}
		
		// Randomizes the hex spawn order and add them to the stage
		for(int j = 0; j < trophies.length; j++){
			int index = rand.nextInt(remainingHexs.size());
			remainingHexs.get(index).sizeDivider += 18 + (int)(j*0.85);
			remainingHexs.get(index).setTouchable(Touchable.disabled);
			stage.addActor(remainingHexs.get(index));
			remainingHexs.remove(index);
		}
		
		// Add skin buttons
		Button b = new Button(5, (int)trophies[36].getY() - mainGame.IMG_SIZE / 2, new Rectangle(mainGame.IMG_SIZE * 4 + 4, 0, mainGame.IMG_SIZE, mainGame.IMG_SIZE * 2), buttonSheet);
		b.addListener(skinBack);
		b.region.flip(true, false);
		stage.addActor(b);
		
		b = new Button(Gdx.graphics.getWidth() - 5 - mainGame.IMG_SIZE, (int)trophies[36].getY() - mainGame.IMG_SIZE / 2, new Rectangle(mainGame.IMG_SIZE * 4 + 4, 0, mainGame.IMG_SIZE, mainGame.IMG_SIZE * 2), buttonSheet);
		b.addListener(skinNext);
		stage.addActor(b);
    }
    
    // Adds the two buttons to the options bar
    private void CreateOptionBarButtons(boolean inMain){
    	// Adds a menu button to stage
		Button b = new Button(Gdx.graphics.getWidth() - mainGame.IMG_SIZE - mainGame.IMG_SIZE / 3, mainGame.IMG_SIZE / 4, new Rectangle(0,0,mainGame.IMG_SIZE, mainGame.IMG_SIZE), buttonSheet);
	    b.setTouchable(Touchable.enabled);
	    
	    if(!mainGame.gameOver)
	    	b.addListener(toggleMenu);
	    else{
	    	b.addListener(share);
	    	b.region = new TextureRegion(buttonSheet, b.region.getRegionX(), b.region.getRegionY() + mainGame.IMG_SIZE, mainGame.IMG_SIZE, mainGame.IMG_SIZE);
	    }
	    
	    if(inMain)
	    	mainGame.stage.addActor(b); 
	    else
	    	stage.addActor(b);
	    
	    // Adds a reset button to stage
 		b = new Button(mainGame.IMG_SIZE / 3, mainGame.IMG_SIZE / 4, new Rectangle(mainGame.IMG_SIZE + 1,0,mainGame.IMG_SIZE, mainGame.IMG_SIZE), buttonSheet);
 	    b.setTouchable(Touchable.enabled);
 	    b.addListener(resetGrid);
 	    
 	   if(inMain)
	    	mainGame.stage.addActor(b); 
	    else{
	    	b.addListener(toggleMenu);
	    	stage.addActor(b);
	    }
 	   
 	   // Trophy banner
 	   if(inMain){
		   TrophyBanner t = new TrophyBanner(0, (int)(-mainGame.IMG_SIZE * 1.5), new Rectangle(-1, 0,Gdx.graphics.getWidth(), (int)(mainGame.IMG_SIZE * 1.5)), buttonSheet);
		   t.setTouchable(Touchable.enabled);
		   mainGame.banner = t;
 	   }
    }
    
    
    // Unlocks a trophy at a given index
    private void UnlockTrophy(int index){
    	boolean rankComplete = true;
    	writeTrophy = true;
    	
    	// determines if the rank is complete
    	for(int i = 0; i < 4; i++){
    		int j = (rank - 1) * 4 + i;
    		
    		if(rankComplete && !trophies[j].isCompleted())
    			rankComplete = false;
    	}
    	
    	// completes the rank if needed 
    	if(rankComplete){
    		rank++;
    		mainGame.rank++;
    		DelockTrophies();
    	}
    	
    	// Trophy banner code
    	TrophyBanner b = mainGame.banner;
    	if(b.timer == 0){
    		b.clearListeners();
    		b.addListener(bannerListenerPre);
    		b.addListener(toggleMenu);
    		b.addListener(bannerListenerPost);
    		b.runBanner(trophies[index]);
			b.toFront();
    	}
    }
    
    // Touch listener than is called for the banner before the toggle menu
    private InputListener bannerListenerPre = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			trophySelected = true;
			inTrophyTab = true;
			return true;
		}
    };
    
    // Touch listener than is called for the banner after the toggle menu
    private InputListener bannerListenerPost = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			TrophyBanner b = (TrophyBanner)event.getListenerActor();
			if(b.timer > 20)
				b.endY -= (int)b.getHeight(); 
			b.timer = 0;
			
			float startY = Gdx.graphics.getHeight() - (mainGame.GetGridHex(4, 14).getY() - mainGame.IMG_SIZE * 7 * 0.955f) / 2 - mainGame.IMG_SIZE * 2;
			Trophy t = trophies[b.trophyIndex];
			Trophy n = new Trophy(t.name, t.description, t.completion, new Rectangle(t.region.getRegionX(),
					t.region.getRegionY(), mainGame.IMG_SIZE, mainGame.IMG_SIZE), trophySheet);
			n.BreakLock();
			
			n.setBounds(0, 0, mainGame.IMG_SIZE * 2, mainGame.IMG_SIZE * 2);
			n.setPosition(mainGame.IMG_SIZE + mainGame.IMG_SIZE / 4.1f, startY);
			n.setTouchable(Touchable.disabled);
			stage.addActor(n);
			
			return true; 
        }
    };
    
    // Loops through the trophies in the current rank and removes the lock icon
    public void DelockTrophies(){
    	if(rank == 10){
    		trophies[36].BreakLock();
    	}
    	else{
			for(int i = 0; i < 4; i++){
				int index = (rank - 1) * 4 + i;
				System.out.println("RegionX: " + trophies[index].unfinished.getRegionX());
				
				if(trophies[index].unfinished.getRegionX() == 0)
					trophies[index].BreakLock();
			}
    	}
    }
    
    
    // Sets the trophy locations based on rank and index
    public void SetTrophyLocations(){
    	float offset = mainGame.IMG_SIZE / 2 + mainGame.IMG_SIZE / 8.2f;
    	UpdateSkin();
    	DelockTrophies();
    	
    	trophies[0].setPosition(mainGame.GetGridHex(4, 14).getX(), mainGame.GetGridHex(4,14).getY() + offset);
    	trophies[1].setPosition(mainGame.GetGridHex(3, 13).getX(), mainGame.GetGridHex(3,13).getY() + offset);
    	trophies[2].setPosition(mainGame.GetGridHex(4, 12).getX(), mainGame.GetGridHex(4,12).getY() + offset);
    	trophies[3].setPosition(mainGame.GetGridHex(5, 13).getX(), mainGame.GetGridHex(5,13).getY() + offset);
    	trophies[4].setPosition(mainGame.GetGridHex(4, 2).getX(), mainGame.GetGridHex(4,2).getY() + offset);
    	trophies[5].setPosition(mainGame.GetGridHex(4, 4).getX(), mainGame.GetGridHex(4,4).getY() + offset);
    	trophies[6].setPosition(mainGame.GetGridHex(3, 3).getX(), mainGame.GetGridHex(3,3).getY() + offset);
    	trophies[7].setPosition(mainGame.GetGridHex(5, 3).getX(), mainGame.GetGridHex(5,3).getY() + offset);
    	trophies[8].setPosition(mainGame.GetGridHex(2, 4).getX(), mainGame.GetGridHex(2,4).getY() + offset);
    	trophies[9].setPosition(mainGame.GetGridHex(1, 5).getX(), mainGame.GetGridHex(1,5).getY() + offset);
    	trophies[10].setPosition(mainGame.GetGridHex(1,7).getX(), mainGame.GetGridHex(1,7).getY() + offset);
    	trophies[11].setPosition(mainGame.GetGridHex(2,6).getX(), mainGame.GetGridHex(2,6).getY() + offset);
    	trophies[12].setPosition(mainGame.GetGridHex(6,12).getX(), mainGame.GetGridHex(6,12).getY() + offset);
    	trophies[13].setPosition(mainGame.GetGridHex(6,10).getX(), mainGame.GetGridHex(6,10).getY() + offset);
    	trophies[14].setPosition(mainGame.GetGridHex(7,11).getX(), mainGame.GetGridHex(7,11).getY() + offset);
    	trophies[15].setPosition(mainGame.GetGridHex(7,9).getX(), mainGame.GetGridHex(7,9).getY() + offset);
    	trophies[16].setPosition(mainGame.GetGridHex(2,12).getX(), mainGame.GetGridHex(2,12).getY() + offset);
    	trophies[17].setPosition(mainGame.GetGridHex(1,11).getX(), mainGame.GetGridHex(1,11).getY() + offset);
    	trophies[18].setPosition(mainGame.GetGridHex(1,9).getX(), mainGame.GetGridHex(1,9).getY() + offset);
    	trophies[19].setPosition(mainGame.GetGridHex(2,10).getX(), mainGame.GetGridHex(2,10).getY() + offset);
    	trophies[20].setPosition(mainGame.GetGridHex(6,4).getX(), mainGame.GetGridHex(6,4).getY() + offset);
    	trophies[21].setPosition(mainGame.GetGridHex(6,6).getX(), mainGame.GetGridHex(6,6).getY() + offset);
    	trophies[22].setPosition(mainGame.GetGridHex(7,5).getX(), mainGame.GetGridHex(7,5).getY() + offset);
    	trophies[23].setPosition(mainGame.GetGridHex(7,7).getX(), mainGame.GetGridHex(7,7).getY() + offset);
    	trophies[24].setPosition(mainGame.GetGridHex(2,8).getX(), mainGame.GetGridHex(2,8).getY() + offset);
    	trophies[25].setPosition(mainGame.GetGridHex(3,5).getX(), mainGame.GetGridHex(3,5).getY() + offset);
    	trophies[26].setPosition(mainGame.GetGridHex(3,7).getX(), mainGame.GetGridHex(3,7).getY() + offset);
    	trophies[27].setPosition(mainGame.GetGridHex(3,9).getX(), mainGame.GetGridHex(3,9).getY() + offset);
    	trophies[28].setPosition(mainGame.GetGridHex(3,11).getX(), mainGame.GetGridHex(3,11).getY() + offset);
    	trophies[29].setPosition(mainGame.GetGridHex(4,10).getX(), mainGame.GetGridHex(4,10).getY() + offset);
    	trophies[30].setPosition(mainGame.GetGridHex(5,11).getX(), mainGame.GetGridHex(5,11).getY() + offset);
    	trophies[31].setPosition(mainGame.GetGridHex(5,9).getX(), mainGame.GetGridHex(5,9).getY() + offset);
    	trophies[32].setPosition(mainGame.GetGridHex(6,8).getX(), mainGame.GetGridHex(6,8).getY() + offset);
    	trophies[33].setPosition(mainGame.GetGridHex(5,7).getX(), mainGame.GetGridHex(5,7).getY() + offset);
    	trophies[34].setPosition(mainGame.GetGridHex(4,6).getX(), mainGame.GetGridHex(4,6).getY() + offset);
    	trophies[35].setPosition(mainGame.GetGridHex(5, 5).getX(), mainGame.GetGridHex(5,5).getY() + offset);
    	trophies[36].setPosition(mainGame.GetGridHex(4,8).getX(), mainGame.GetGridHex(4,8).getY() + offset);
    }
    
    // Sets the font and batch color based on the screens skin
	private Color GetBackgroundColor(){
		switch(skinIndex){
			case 1: // Gem skin
				return new Color(41/255f, 0, 41/255f, 1);
				
			case 2: // Wood skin
				return new Color(64/255f, 34/255f, 24/255f, 1);
				
			case 3: // Cube skin
				return new Color(30/255f, 0, 30/255f, 1);

			default: // Default skin
				return Color.WHITE;
		}
	}
}
