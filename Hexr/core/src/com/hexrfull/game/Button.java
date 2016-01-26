package com.hexrfull.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Button extends Actor{
	// Holds the ending x,y positions of the button 
	int endX;
	int endY;
	
	// Holds the texture region of the button
	TextureRegion region;
	
	public Button() {
	}
	
	// Constructor
	public Button(int x, int y, Rectangle srcRect, Texture bSheet) {
		endX = x;
		endY = y;
		region = new TextureRegion(bSheet, (int)srcRect.x, (int)srcRect.y, (int)srcRect.width, (int)srcRect.height);
		setBounds(x,y, srcRect.width, srcRect.height);
	}
	
	// Constructor with a scalar to manipulate the button size
	public Button(int x, int y, Rectangle srcRect, Texture bSheet, float scale) {
		endX = x;
		endY = y;
		region = new TextureRegion(bSheet, (int)srcRect.x, (int)srcRect.y, (int)srcRect.width, (int)srcRect.height);
		setBounds(x,y, srcRect.width * scale, srcRect.height * scale);
	}

	@Override
	public void act(float delta){
		// Shape is not at ending X/Y
		if(endX >= 0 && (endX - 01 > getX() || getX() > endX + 0.1) || 
        		(endY - 01 > getY() || getY() > endY + 0.1)){
        	originate();
        }
	}
	
	@Override
    public void draw(Batch batch, float alpha){
		if(region.getRegionX() >= 0)
			batch.draw(region, getX(), getY(), getWidth(), getHeight());
	}
	
	// Moves the shape to its starting positions along with its units
	public void originate(){
		// Calculate x and y difference
		float xDif = endX - getX();
		float yDif = endY - getY();
		
		setX(getX() + xDif / 7);
		setY(getY() + yDif / 7);
	}
}
