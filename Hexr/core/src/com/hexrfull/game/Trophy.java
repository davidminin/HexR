package com.hexrfull.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Trophy extends Actor{
	// Holds the percentage of completion towards the trophy
	int completion = 0;
	
	// Holds the description on how to obtain the achievement
	String description;
	
	// Holds the name of the trophy
	String name;
	
	// Holds the divident of the trophy size, used in drawing
	public int sizeDivider = 1;
	
	// Holds the texture region of the completed and the uncompleted trophy
	TextureRegion region;
	TextureRegion unfinished;
	
	// Constructor
	public Trophy(String n, String desc, int comp, Rectangle srcRect, Texture tex){
		name = n;
		completion = comp;
		description = desc;
		
		region = new TextureRegion(tex, (int)srcRect.x, (int)srcRect.y, (int)srcRect.width, (int)srcRect.height);
		unfinished = new TextureRegion(tex, 0, 0, (int)srcRect.width, (int)srcRect.height);
		setBounds(0,0, srcRect.width, srcRect.height);
	}
	
	@Override
	public Trophy hit(float x, float y, boolean touchable){
		// If this Actor is hidden or untouchable, it can't be hit
        if(!this.isVisible() || this.getTouchable() == Touchable.disabled || !Gdx.input.isTouched())
            return null;
        
        x = Gdx.input.getX();
        y = Gdx.input.getY();
        		
        if(CollisionCheck(x,y))
        	return this;
        
        return null;
	}
	
	@Override
    public void draw(Batch batch, float alpha){
		if(sizeDivider > 1)
			sizeDivider--;
		
		if(region.getRegionY() >= 0){
			if(isCompleted() && sizeDivider == 1)
				batch.draw(region, getX(), getY(), getWidth() / sizeDivider, getHeight() / sizeDivider);
			
			else if(!isCompleted() && sizeDivider == 1)
				batch.draw(unfinished, getX(), getY(), getWidth(), getHeight());
			
			else if(isCompleted() && sizeDivider <= 20){
				float percent = (20 - sizeDivider) / 20f;
				int size = (int)(getWidth() * percent);
				batch.draw(region, getX() + (getWidth() - size) / 2, getY() + (getHeight() - size) / 2, size, size);
			}
			else if(!isCompleted() && sizeDivider <= 20){
				float percent = (20 - sizeDivider) / 20f;
				int size = (int)(getWidth() * percent);
				batch.draw(unfinished, getX() + (getWidth() - size) / 2, getY() + (getHeight() - size) / 2, size, size);
			}
		}
	}
	
	// Called upon to remove the lock as the icon
	public void BreakLock(){
		unfinished.setRegion(region.getRegionWidth() + 1, 0, region.getRegionWidth(), region.getRegionHeight());
	}
	
	// Returns if the trophy is completed
	public boolean isCompleted(){
		return (completion <= 0);
	}
	
    // Circle collision detection with point (x,y)
    public boolean CollisionCheck(float x, float y){
        // Radius of circle 
        float radius = (float)(getWidth()/2 - getWidth() / 40);
        
        // Get center point of bounding circle, also known as the center of the rectangle
        float centerX = getX() + radius;
        float centerY = getY() + radius;

        // Attempts to avoid collision detection if point is out of bounding square
    	if(Math.abs(x - centerX) > radius || Math.abs(y - centerY) > radius) return false;
        
        // Distance of point from the center of the circle (squared)
        float distance = (float)((centerX - x) * (centerX - x)) 
                + ((centerY - y) * (centerY - y));
        
        // If the distance is less than the circle radius, it's a hit
        return (distance <= radius * radius);
    }
}
