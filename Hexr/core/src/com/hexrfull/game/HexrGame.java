package com.hexrfull.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;

public class HexrGame extends ApplicationAdapter {
	SpriteBatch batch;
	BitmapFont titleFont;
	BitmapFont bigFont;
	BitmapFont smallFont;
	BitmapFont medFont;
	GlyphLayout layout;
	
	ScreenManager screens;
	float ratio;
	
	GoogleServices googleServices;
	Color colBatch;

	// Constructor
	public HexrGame(GoogleServices gServices){
		super();
		googleServices = gServices;
	}
	
	// Initialize code
	public void create () {	
		screens = new ScreenManager();
		batch = new SpriteBatch();
		layout = new GlyphLayout();
		
		//FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial_narrow_7.ttf"));
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Light.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		
		parameter.size = 30;
		generator.scaleForPixelHeight(30);
		smallFont = generator.generateFont(parameter);
		parameter.size = 45;
		generator.scaleForPixelHeight(45);
		medFont = generator.generateFont(parameter);
		parameter.size = 72;
		generator.scaleForPixelHeight(72);
		bigFont = generator.generateFont(parameter);
		titleFont = generator.generateFont(parameter); 
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
		
		titleFont.setColor(Color.BLACK);
		ReadTrophiesFile();
		screens.SetTrophyLocations();
		SetColors();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(colBatch.r, colBatch.g, colBatch.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Writes the trophy file
		if(screens.writeTrophy){
			WriteTrophiesFile();
			SetColors();
			screens.writeTrophy = false;
		}
		
		// fixes google services for the screen manager
		if(screens.googleServices == null)
			screens.googleServices = googleServices;
		
		TrophyBanner b = screens.mainGame.banner;
		
		screens.render();
		batch.begin();
		
		String header = "" + screens.mainGame.points;
		if(screens.mainGame.gameOver)
			header = "Game Over";
		
		layout.setText(titleFont, header);
		titleFont.draw(batch, header, (int)(Gdx.graphics.getWidth() - layout.width) / 2, 
				(int)(Gdx.graphics.getHeight() - ((int)(screens.mainGame.IMG_SIZE * 0.75) - bigFont.getCapHeight() / 2)));
		
		// Draw trophy banner text
		if(b.timer > 0){
			layout.setText(medFont, b.name);
			medFont.setColor(Color.WHITE);
			medFont.draw(batch, b.name, (Gdx.graphics.getWidth() - layout.width) / 2, (int)(Gdx.graphics.getHeight() - b.getY()
					- ((int)(screens.mainGame.IMG_SIZE * 0.375) + medFont.getCapHeight() / 2)));
			medFont.setColor(Color.BLACK);
		}
		
		// Done like this so that shapes draw over the options bar
		batch.end();
		if(screens.mainGame.gameOver || !screens.mainGame.paused)
			screens.mainGame.render();
		batch.begin();
		
		// Game is in pause menu
		if(screens.mainGame.paused && screens.stage.getActors().size > 8){
			if(screens.inTrophyTab == null){ // options menu
				layout.setText(bigFont, "" + screens.highscore);
				bigFont.draw(batch, "" + screens.highscore, (int)(Gdx.graphics.getWidth() - layout.width) / 2, (int)(screens.trophies[0].getY() + bigFont.getCapHeight() * 1.5f));
			}
			
			else if(screens.inTrophyTab){ //trophy menu
				int completed = 37;
				
				// Adjusts completed variable
				if(screens.rank != 10){
					completed = 0;
					
					// Loops through the trophies in the rank
					for(int i = 0; i < 4; i++){
						int index = (screens.rank - 1) * 4 + i;
						
						// Only increments if index is in range and the trophy is completed
						if(index <= 37 && screens.trophies[index].isCompleted())
							completed++;
					}
				}
				
				layout.setText(bigFont, completed + " / 37  ");
				bigFont.draw(batch, completed + " / 37", (int)(Gdx.graphics.getWidth() - layout.width), 
						(int)(Gdx.graphics.getHeight() - (screens.mainGame.IMG_SIZE * 3 - bigFont.getCapHeight() / 2)));
			

				Trophy t = Trophy.class.cast(screens.stage.getActors().peek());
				
				// A trophy is being viewed at, draw its information
				if(screens.trophySelected){
					float centerX = Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() - screens.mainGame.IMG_SIZE * 2.146f) / 2;
					
					if(t.isCompleted() || t.unfinished.getRegionX() != 1){ // trophy not locked
						String one = "";
						String two = "";
						String[] word = t.description.split(" ");
						int index = 0;
						
						// assembles the two words
						while(index < word.length){
							if(one.length() + word[index].length() < 20)
								one += " " + word[index];
							else
								two += " " + word[index];
							
							index++;
						}
						
						one = one.trim();
						two = two.trim();
						
						layout.setText(medFont, t.name);
						medFont.draw(batch, t.name, (int)(centerX - layout.width / 2), (int)(Gdx.graphics.getHeight() - (t.getY() + medFont.getCapHeight() * 3f / 4f)));
						
						layout.setText(smallFont, one);
						smallFont.draw(batch, one, (int)(centerX - layout.width / 2), (int)(Gdx.graphics.getHeight() - (t.getY() + medFont.getCapHeight() * 2.75f)));
						
						layout.setText(smallFont, two);
						smallFont.draw(batch, two, (int)(centerX - layout.width / 2), (int)(Gdx.graphics.getHeight() - (t.getY() + medFont.getCapHeight() * 3.75f)));
					} 
					
					else{ // trophy is locked
						String one = "Unlock more trophies";
						String two = "to unlock this one";
						
						layout.setText(medFont, "Locked Trophy");
						medFont.draw(batch, "Locked Trophy", (int)(centerX - layout.width / 2), (int)(Gdx.graphics.getHeight() - (t.getY() + medFont.getCapHeight() * 3f / 4f)));
						
						layout.setText(smallFont, one);
						smallFont.draw(batch, one, (int)(centerX - layout.width / 2), (int)(Gdx.graphics.getHeight() - (t.getY() + medFont.getCapHeight() * 2.75f)));
						
						layout.setText(smallFont, two);
						smallFont.draw(batch, two, (int)(centerX - layout.width / 2), (int)(Gdx.graphics.getHeight() - (t.getY() + medFont.getCapHeight() * 3.75f)));
					}
				}
				
				else{ // nothing is selected, display helper text
					float startY = Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() - (screens.mainGame.GetGridHex(4, 14).getY() - screens.mainGame.IMG_SIZE * 7 * 0.955f) / 2 - screens.mainGame.IMG_SIZE - bigFont.getCapHeight());
					
					layout.setText(medFont, "Click on a trophy to see");
					medFont.draw(batch, "Click on a trophy to see", (int)(Gdx.graphics.getWidth() - layout.width) / 2, (int)startY); 
					layout.setText(medFont, "its information");
					medFont.draw(batch, "its information", (int)(Gdx.graphics.getWidth() - layout.width) / 2, (int)(startY - bigFont.getCapHeight())); 
				}
			}
			
			else{ // skins menu
				float startY = Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() - (screens.mainGame.GetGridHex(4, 14).getY() - screens.mainGame.IMG_SIZE * 7 * 0.955f) / 2 - screens.mainGame.IMG_SIZE - bigFont.getCapHeight());
				
				if(screens.tempSkinIndex != screens.skinIndex){ // skin is locked
					medFont.setColor(Color.RED);
					layout.setText(medFont, "This Skin");
					medFont.draw(batch, "This Skin", (int)(Gdx.graphics.getWidth() - layout.width) / 2, (int)startY); 
					layout.setText(medFont, "is currently locked.");
					medFont.draw(batch, "is currently locked.", (int)(Gdx.graphics.getWidth() - layout.width) / 2, (int)(startY - bigFont.getCapHeight())); 
				}
				else{
					medFont.setColor(Color.GREEN);
					layout.setText(medFont, "This Skin");
					medFont.draw(batch, "This Skin", (int)(Gdx.graphics.getWidth() - layout.width) / 2, (int)startY); 
					layout.setText(medFont, "has been applied.");
					medFont.draw(batch, "has been applied.", (int)(Gdx.graphics.getWidth() - layout.width) / 2, (int)(startY - bigFont.getCapHeight())); 
				}
				
				// Resets the colors
				SetColors();
			}
		}
		
		batch.end();
	}
	
	// Sets the font and batch color based on the screens skin
	private void SetColors(){
		int index = screens.tempSkinIndex;
		
		switch(index){
			default: // Default skin
				colBatch = Color.WHITE;
				bigFont.setColor(Color.BLACK);
				medFont.setColor(Color.BLACK);
				smallFont.setColor(Color.BLACK);
				break;
				
			case 1: // Gem skin
				colBatch = new Color(41/255f, 0, 41/255f, 1);
				bigFont.setColor(Color.WHITE);
				medFont.setColor(Color.WHITE);
				smallFont.setColor(Color.WHITE);
				break;
				
			case 2: // Wood skin
				colBatch = new Color(64/255f, 34/255f, 24/255f, 1);
				bigFont.setColor(Color.WHITE);
				medFont.setColor(Color.WHITE);
				smallFont.setColor(Color.WHITE);
				break;
				
			case 3: // Cube skin
				colBatch = new Color(30/255f, 0, 30/255f, 1);
				bigFont.setColor(Color.WHITE);
				medFont.setColor(Color.WHITE);
				smallFont.setColor(Color.WHITE);
				break;
		}
	}
	
	// Reads from the trophy file the current state of the trophy ladder
	private void ReadTrophiesFile(){
		FileHandle file = Gdx.files.internal("ladderFile.txt");
		String[] text;
		
		// Attempts to read the file
		try{
			file = Gdx.files.local("ladderFile.txt");
			text = file.readString().split("\n");
		}
		// If the file doesnt exist, copy it to the local folder and read it then
		catch(Exception e){ 
			Gdx.files.internal("ladderFile.txt").copyTo(Gdx.files.local("ladderFile.txt"));
			text = file.readString().split("\n");
		}
		
		int size = screens.mainGame.IMG_SIZE;
		int rank = 1;
		boolean uncompletedTrophy = false;
		
		// Sets highscore and sound options up
		String[] line = text[0].split("~");
		screens.highscore = Integer.valueOf(line[0]);
		screens.soundOn = Boolean.valueOf(line[1]);
		screens.skinIndex = Integer.valueOf(line[2]);
		screens.tempSkinIndex = screens.skinIndex;
		
		// Loops through all the trophies in the file, text.length will be always 38
		for(int i = 1; i < text.length; i++){
			line = text[i].split("~");
			
			String name = line[0];
			String desc = line[1];
			int complet = Integer.valueOf(line[2]);

			name = name.trim();
			desc = desc.trim();
			
			if(complet != 100)
				uncompletedTrophy = true;
			
			screens.trophies[i - 1] = new Trophy(name, desc, complet, new Rectangle(1 + (size + 1) * ((i - 1) % 4), size * ((i + 3) / 4), size, size), screens.trophySheet);
			
			// Determines the rank
			if(!uncompletedTrophy && i % 4 == 0){
				rank = i / 4;
			}
		}
		
		screens.rank = rank;
		screens.mainGame.rank = rank;
		screens.DelockTrophies();
	}
	
	// Writes to the trophy file the current state of the trophy ladder
	private void WriteTrophiesFile(){
		FileHandle file = Gdx.files.local("ladderFile.txt");
		Trophy[] trophies = screens.trophies;
		
		// Clears the file of any text
		file.writeString("", false);
		
		file.writeString(screens.highscore + "~" + screens.soundOn + "~" + screens.skinIndex + "~\n", true);
		
		// Loops through all the trophies
		for(int i = 0; i < trophies.length; i++){
			String name = trophies[i].name;
			String desc = trophies[i].description;
			int complet = trophies[i].completion;
			
			// Writes the trophy into the file
			file.writeString(name + " ~" + desc + " ~" + complet + "~\n", true);
		}
	}
	
    // Determines the ratio difference between the original image size and the one needed 
    public float DetermineRatio(){
    	// Base case where resolution is appropriate for dpi
    	float ratio =  Gdx.graphics.getDensity() / 2;
    	String repr = Gdx.graphics.getWidth() + "*" + Gdx.graphics.getHeight();
    	
    	// Sometimes dpi is strange with given resolution
    	if((repr.equals("480*800") || repr.equals("480*854")) && Gdx.graphics.getDensity() <= 1.15) 
    		ratio = Gdx.graphics.getDensity() / 3;
    	
    	// Iphone ratio fixes
    	if(repr.equals("640*1136") || repr.equals("640*960")) 
    		ratio = Gdx.graphics.getDensity() / 3;
    	
    	return ratio * Gdx.graphics.getWidth() / 720;
    }
}


