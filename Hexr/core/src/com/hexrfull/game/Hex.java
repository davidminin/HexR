package com.hexrfull.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.hexrfull.game.MainGame.ID;

// Hex class, object used as the main object in the game.
public class Hex extends Actor { 
	// Variables used in drawing the hex
	int IMG_SIZE;
	int hexSheetWidth;
	
	// Coordinate used to determine location of the hex
	Coordinate cor;
		
	// Identification of what type of hex it is
	ID id;
	 
	// Variables used in sprite animation
	Rectangle srcRect;
	Boolean animate;
	int frameCount = 0;
	 
	// Constructor
	public Hex(ID newId, float x, float y, int imgBounds, Coordinate c, int IMG_SIZE, int hexSheetWidth){
		this.IMG_SIZE = IMG_SIZE;
		this.hexSheetWidth = hexSheetWidth;
		setID(newId);
		cor = c;
		setBounds(x, y, imgBounds, imgBounds);
	}
	 
	@Override
	public void act(float delta){
		// Backwards animation for when the grid spawns
		if(animate == null){
			if(frameCount == 18) // done spawning
				animate = false;
			
			else if (frameCount % 2 == 0 && frameCount >= 0) // currently spawning
				srcRect.x -= IMG_SIZE;
			
			frameCount++;
		}
		// Animate the sprite sheet 
		else if(animate){    
			frameCount--; // Decrements the frame count
			
			if(frameCount == 0) // resets the hex
				setID(ID.NONE);
			
			else if(frameCount % 2 == 0 && frameCount <= 19){ // change frame
				if(srcRect.x == 0)
					srcRect.x++;
				
				srcRect.x += IMG_SIZE;
			}	
		}
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
	
	// Draw function used instead of the default, has MainGame pass through the texture sheet
	public void draw(Batch batch, float alpha, Texture hexSheet){
		if(animate == null && frameCount > 0 && id != ID.NONE){ // if the shape is animating from the spawn and is colored, do texture region drawing
			float percent = (frameCount) / 18f;
			int size = (int)(getWidth() * percent);
			
			TextureRegion reg = new TextureRegion(hexSheet, IMG_SIZE + 1, (int)srcRect.y, IMG_SIZE, IMG_SIZE);
			batch.draw(reg, getX() + getWidth() / 2 - size / 2, Gdx.graphics.getHeight() - (getY() + getHeight() / 2 - size / 2), size, size);
		}
		else if(frameCount > 0) { // only draw if frameCount is above zero 
			batch.draw(hexSheet, getX(), Gdx.graphics.getHeight() - getY(), (int)srcRect.x, (int)srcRect.y, (int)srcRect.width, (int)srcRect.height);
		}
	}
	
	// Sets the id of the hex 
	public void setID(ID newId){
		id = newId;
		setImg();
	}
	
	// Sets the hex to preform spawn animation with a delay value for when to pop in
	public void spawn(int delay){
		animate = null;
		frameCount = -delay;
		srcRect.x = hexSheetWidth;
	}
	
	// Sets the srcRect of the hex based on its id and resets animation
	// Colors in order in reference to the spritesheet - Grey, Blue, Red, Yellow, Green
	private void setImg(){
		animate = false;
		frameCount = 19;
		
		switch(id){
		case BLUE:
			srcRect = new Rectangle(IMG_SIZE + 1, IMG_SIZE, IMG_SIZE, IMG_SIZE);
			break;
			
		case RED:
			srcRect = new Rectangle(IMG_SIZE + 1, IMG_SIZE * 2, IMG_SIZE, IMG_SIZE);
			break;
			
		case YELLOW:
			srcRect = new Rectangle(IMG_SIZE + 1, IMG_SIZE * 3, IMG_SIZE, IMG_SIZE);
			break;
	 		
	 	case GREEN:
	 		srcRect = new Rectangle(IMG_SIZE + 1, IMG_SIZE * 4, IMG_SIZE, IMG_SIZE);
	 		break;
	 		
	 	case PURPLE:
	 		srcRect = new Rectangle(IMG_SIZE + 1, IMG_SIZE * 5, IMG_SIZE, IMG_SIZE);
	 		break;
	 		
	 	case TEAL:
	 		srcRect = new Rectangle(IMG_SIZE + 1, IMG_SIZE * 6, IMG_SIZE, IMG_SIZE);
	 		break;
	 		
	 	case BOMB:
	 		srcRect = new Rectangle(0, 0, IMG_SIZE, IMG_SIZE);
	 		break;
	 		
	 	default:
	 		srcRect = new Rectangle(IMG_SIZE + 1, 0, IMG_SIZE, IMG_SIZE);
	 		break;
		}
	}
 }