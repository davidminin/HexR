package com.hexrfull.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

// Trophy banner class used in the event that the player has unlocked a trophy. A trophy
// banner drops down from the top of the screen to notify that the player has unlocked a 
// specific trophy.
public class TrophyBanner extends Button{
	// Counter used to determine how long the banner should stay for
	int timer;
	
	// Holds the index of which trophy got unlocked
	int trophyIndex = 0;
	
	// Holds the name of the trophy being unlocked
	String name;
	
	// Holds the two colors used in the banner gradient
	Color color = new Color(0, 0, 0, 0.85f);
	
	// Constructor
	public TrophyBanner(int x, int y, Rectangle srcRect, Texture bSheet) {
		super(x, y, srcRect, bSheet);
	}
	
	@Override
	public void act(float delta){
		// Shape is not at ending X/Y
		if(endX >= 0 && (endX - 01 > getX() || getX() > endX + 0.1) || 
        		(endY - 01 > getY() || getY() > endY + 0.1)){
        	originate();
        }
		
		// Animate the trophy sheet
		if(timer > 0){
			timer--;
			
			if(timer == 30){
				endY -= (int)getHeight();
			}
		}
	}
	
	// Toggles the banner to either return or to move out
	public void runBanner(Trophy t, int index){
		name = "Trophy '" + t.name + "' Unlocked";
		trophyIndex = index;
		timer = 200;
		endY += (int)getHeight();
		region = t.region;
	}
}
