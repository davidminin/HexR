package com.hexrfull.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MainGame implements ApplicationListener{
	    
	public enum ID {
		BLUE, GREEN, RED, YELLOW, PURPLE, TEAL, NONE, BOMB
	}

	// Takes in a lower case string value of an id and returns the id equivelant
	public ID StringToID(String str){
		if(str.equals("blue"))
			return ID.BLUE;
		
		else if(str.equals("green"))
			return ID.GREEN;
		
		else if(str.equals("red"))
			return ID.RED;
		
		else if(str.equals("yellow"))
			return ID.YELLOW;
		
		else if(str.equals("purple"))
			return ID.PURPLE;
		
		else if(str.equals("none"))
			return ID.NONE;
		
		else if(str.equals("teal"))
			return ID.TEAL;
		
		else if(str.equals("bomb"))
			return ID.BOMB;
		
		System.out.println(str);
		return null;
	}
	
	// Takes in a id and returns the lower case string equivelent
	public String IDToString(ID id){
		switch(id){
			case BLUE:
				return "blue";
			
			case RED:
				return "red";
				
			case GREEN:
				return "green";
				
			case YELLOW:
				return "yellow";
				
			case PURPLE:
				return "purple";
				
			case NONE:
				return "none";
				
			case BOMB:
				return "bomb";
				
			case TEAL:
				return "teal";
		
			default:
				System.out.println(id + "");
				return null;
		}
	}
	
	// Coordinate class simply used by hexs
	public class Coordinate {
		private int X;
		private int Y;
		
		public Coordinate(int x, int y){
			X = x;
			Y = y;
		}
		
		public int getX(){
			return X;
		}
		
		public int getY(){
			return Y;
		}
	}
	
	// ClickBox class, object used to easily click on shapes from their respective origins
	public class ClickBox extends Actor {
		public Shape shape;
		public int spawnX;
		public int spawnY;
		
		// Constructor
		public ClickBox(int x, int y, int xBound, int yBound){
			setBounds(x, y, xBound, yBound);
			spawnX = x + xBound / 2;
			spawnY = y + yBound / 2 - (int)(IMG_SIZE * 0.85);
		}
		
		// Reinitializes the shape into the grid box
		public void NewShape(Shape s){
			shape = s;
			stage.addActor(shape);
			toFront();
		}
		
		// Uses the type of a shape to reinitialize that kind of shape into the grid box
		public void CreateTypeShape(String type){
			int num = Integer.valueOf(type.substring(0, 1));
			type = type.substring(1);
			
			for(int i = 0; i < shape.units.length; i++){
				shape.units[i].remove();
			}
			shape.remove();
			
			NewShape(new Shape(spawnX, IMG_SIZE, this, type, num));
		}
		
		@Override
		public Actor hit(float x, float y, boolean touchable){
        	// If this Actor is hidden or untouchable, it can't be hit
            if(!this.isVisible() || this.getTouchable() == Touchable.disabled || !Gdx.input.isTouched())
                return null;
            
            // If other box is activated this clickbox cannot be hit
            if(boxOne.shape.activated == null || boxTwo.shape.activated == null ||
            		boxOne.shape.activated || boxTwo.shape.activated)
            	return null;
            
            x = Gdx.input.getX();
            y = Gdx.input.getY();
            
            // Click cannot collide with any of the bottom three hex's in the grid
            if(GetGridHex(3, 13).CollisionCheck(x, y) || GetGridHex(4, 14).CollisionCheck(x, y) ||
            		GetGridHex(5, 13).CollisionCheck(x, y))
            	return null;
            
            // Rectangle collision detection
            if(x >= getX() && x <= getX() + getWidth() && y >= getY() && y <= getY() + getHeight()){
            	// Activates the clickbox if collision detection passes
                shape.activated = true;
                setX(Gdx.graphics.getWidth() + 1);
                
                // Brings the shape and its hexs to the front
                for(int i = 0; i < shape.units.length; i++){
                	shape.units[i].toFront();}
                shape.toFront();
                
                return this;
            }
            
            return null;
        }
	}
	
	// Hex class, object used to create grid and shapes
	public class Hex extends Actor { 
        // Coordinate used to determine location of the hex
		Coordinate cor;
		
		// Identification of what type of hex it is
        ID id;
        
        // Variables used in sprite animation
        Rectangle srcRect;
        Boolean animate;
        int frameCount = 0;
        
        // Constructor
        public Hex(ID newId, float x, float y, int imgBounds, Coordinate c){
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
        
        @Override
        public void draw(Batch batch, float alpha){
        	if(animate == null && frameCount > 0 && id != ID.NONE){ // if the shape is animating from the spawn and is colored, do texture region drawing
        		TextureRegion reg = new TextureRegion(hexSheet, IMG_SIZE + 1, (int)srcRect.y, IMG_SIZE, IMG_SIZE);
        		float percent = (frameCount) / 18f;
    			int size = (int)(getWidth() * percent);
    			
    			batch.draw(reg, getX() + getWidth() / 2 - size / 2, getY() + getHeight() / 2 - size / 2, size, size);
        	}
        	
        	else if(frameCount > 0) // only draw if frameCount is above zero
        		batch.draw(hexSheet, getX(), getY(), (int)srcRect.x, (int)srcRect.y, (int)srcRect.width, (int)srcRect.height);
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
    		srcRect.x = hexSheet.getWidth();
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
	
	// Shape class, object used to manage a collection of hexs that form a shape
	public class Shape extends Actor{
		// Holds the parent ClickBox
		public ClickBox box;
		
		// Holds the starting x and y values of the shape
		float startingX;
		float startingY;
		
		// Holds the horizontal offset used in centering the shape on clicks
		int xOffset;
		
		// Used in determining what function shape should preform
		Boolean activated = false;
		
		// Contains all the unique hexs in the shape
		Hex[] units;
		
		// Contains the string version of the shape
		String type;
		
		// Constructor
		public Shape(float x, int imgBounds, ClickBox c){
        	startingX = x;
        	box = c;
        	startingY = box.spawnY;
        	
        	// Sets the type of shape 
        	CreateRandomShape(imgBounds);

        	setBounds(startingX, startingY, imgBounds, imgBounds);
        	startingX -= xOffset;
        	
        	if(box == boxOne) // slide from the left
        		setAllX(-imgBounds * 3.5f);
        	
        	else // slide from the right
        		setAllX(Gdx.graphics.getWidth() + imgBounds * 3.5f);
        	
        	// Brings all the units of the shape to the front of the actors
        	for(int i = 0; i < units.length; i++){
				stage.addActor(units[i]);
				units[i].toFront();
			}
        }
		
		// Constructor with a type and override that is passed through
		public Shape(float x, int imgBounds, ClickBox c, String t, int override){
        	startingX = x;
        	box = c;
        	type = t;
        	startingY = box.spawnY;
        	Random rand = new Random();
        	
        	// 9 is used as a tell that -1 should be used instead
        	if(override == 9)
        		override = -1;
        	
        	// Creates a bomb or a single
        	if(type.equals("Single"))
        		CreateSingle(imgBounds, rand, override);
        	// Creates a specific double
        	else if(type.equals("Double"))
        		CreateDouble(imgBounds, rand, override);
        	// Creates a specific triforce
        	else if(type.equals("Triforce"))
        		CreateTriforce(imgBounds, rand, override);
        	// Creates a specific quadra
        	else if(type.equals("Quadra"))
        		CreateQuadra(imgBounds, rand, override);
        	// Creates a specific penta
        	else if(type.equals("Square"))
        		CreateSquare(imgBounds, rand, override);
        	// Creates a SuperHex
        	else if(type.equals("SuperHex"))
        		CreateSuperHex(imgBounds, override);
        	else
        		System.out.println("Error: '" + type + "'");

        	setBounds(startingX, startingY, imgBounds, imgBounds);
        	startingX -= xOffset;
        	
        	if(box == boxOne) // slide from the left
        		setAllX(-imgBounds * 3.5f);
        	
        	else // slide from the right
        		setAllX(Gdx.graphics.getWidth() + imgBounds * 3.5f);
        	
        	// Brings all the units of the shape to the front of the actors
        	for(int i = 0; i < units.length; i++){
				stage.addActor(units[i]);
				units[i].toFront();
			}
        }
		
		// Sets the shape to any possible random shape
		private void CreateRandomShape(int imgBounds){
			Random rand = new Random();
			int val = rand.nextInt(100) + 1;
			
			// Chooses the random shape
			if(val <= spawnRates[0]){
				CreateSingle(imgBounds, rand, -1);
				ChangeSpawnRates(0);
			}
			else if(val <= spawnRates[1] + spawnRates[0]){
				CreateDouble(imgBounds, rand, -1);
				ChangeSpawnRates(1);
			}
			else if(val <= spawnRates[2] + spawnRates[1] + spawnRates[0]){
				CreateTriforce(imgBounds, rand, -1);
				ChangeSpawnRates(2);
			}
			else if(val <= spawnRates[3] + spawnRates[2] + spawnRates[1] + spawnRates[0]){
				CreateQuadra(imgBounds, rand, -1);
				ChangeSpawnRates(3);
			}
			else if (val <= spawnRates [4] + spawnRates[3] + spawnRates[2] + spawnRates[1] + spawnRates[0]){
				CreateSquare(imgBounds, rand, -1);
				ChangeSpawnRates(4);
			}
			else{
				CreateSuperHex(imgBounds, 0);
				ChangeSpawnRates(5);
			}
		}
		
		// Creates a single hex shape including the bomb
		private void CreateSingle(int imgBounds, Random rand, int override){
			units = new Hex[1];
        	xOffset = imgBounds / 2;
        	int num = rand.nextInt(4);
        	startingY += imgBounds / 2;
        	
        	if(points < 200)
        		num = 1;
        	
        	if(override != -1) // spawns a specific shape based on the override
        		num = override;
        	
        	type = num + "Single";
        	switch (num){
        		// Creates a bomb
        		case 0:
        			units[0] = new Hex(ID.BOMB, startingX, startingY, imgBounds, new Coordinate(0,0));
        			break;
        		
        		// Otherwise creates a normal single hex
        		default:
        			units[0] = new Hex(ID.BLUE, startingX, startingY, imgBounds, new Coordinate(0,0));
        			break;
        	}
		}
		
		// Creates an ultra hex shape
		private void CreateSuperHex(int imgBounds, int ignoreThis){
			units = new Hex[7];
			ID id = ID.BLUE;
        	xOffset = imgBounds / 2;
        	type = "0SuperHex";
        	startingY += (int)(imgBounds * 1.5) + 1;
        	
			units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
			units[1] = new Hex(id, CalculateStartX(imgBounds, -1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(-1,-1)); 
			units[2] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(0,-2)); 
			units[3] = new Hex(id, CalculateStartX(imgBounds, 1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(1,-1)); 
			units[4] = new Hex(id, CalculateStartX(imgBounds, -1), CalculateStartY(imgBounds, -3), imgBounds, new Coordinate(-1,-3)); 
			units[5] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -4), imgBounds, new Coordinate(0,-4)); 
			units[6] = new Hex(id, CalculateStartX(imgBounds, 1), CalculateStartY(imgBounds, -3), imgBounds, new Coordinate(1,-3)); 
		}
		
		// Creates a double hexed line
		private void CreateDouble(int imgBounds, Random rand, int override){
			units = new Hex[2];
			ID id = ID.PURPLE;
			int val = rand.nextInt(3);
			
			if(override != -1) // spawns a specific shape based on the override
        		val = override;
			
			type = val + "Double";
			switch(val){
			case 0: // Diagonally right wise
				startingY += (int)(imgBounds * 0.75);
	        	xOffset = (int)(imgBounds * 0.175);
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0)); 
				units[1] = new Hex(id, CalculateStartX(imgBounds, -1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(-1,-1)); 
				break;
				
			case 1: // Diagonally left wise
				startingY += (int)(imgBounds * 0.75); 
				xOffset = (int)(imgBounds * 0.825);
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
				units[1] = new Hex(id, CalculateStartX(imgBounds, 1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(1,-1)); 
				break;
				
			case 2: // Vertical
				startingY += imgBounds + 2;
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0)); 
	        	xOffset = imgBounds / 2;
				units[1] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(0,-2)); 
				break;
			}
		}
		
		// Creates a triforce of hexs
		private void CreateTriforce(int imgBounds, Random rand, int override){
			units = new Hex[3];
			ID id = ID.YELLOW;
			int val = rand.nextInt(2);
			startingY += imgBounds + 2;
			
			units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
			units[1] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(0,-2));
			
			if(override != -1) // spawns a specific shape based on the override
        		val = override;
			
			type = val + "Triforce";
			switch(val){
			case 0: // Tilted right
				xOffset = (int)(imgBounds * 0.175);
				units[2] = new Hex(id, CalculateStartX(imgBounds, -1), CalculateStartY(imgBounds, -1), imgBounds , new Coordinate(-1,-1));
				break;
				
			case 1: // Tilted left
				xOffset = (int)(imgBounds * 0.825);
				units[2] = new Hex(id, CalculateStartX(imgBounds, 1), CalculateStartY(imgBounds, -1), imgBounds , new Coordinate(1,-1));
				break;
			}
		}
		
		// Creates a quadra hex line
		private void CreateQuadra(int imgBounds, Random rand, int override){
			units = new Hex[4];
			ID id = ID.RED;
			int val = rand.nextInt(3);
			
			if(override != -1) // spawns a specific shape based on the override
        		val = override;
			
			type = val + "Quadra";
			switch(val){
			case 0: // Diagonally right wise
				xOffset = (int)(-imgBounds * 0.825 + 10 * ratio);
				startingY += (int)(1.25 * imgBounds) + 1;
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
				units[1] = new Hex(id, CalculateStartX(imgBounds, -1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(-1,-1)); 
				units[2] = new Hex(id, CalculateStartX(imgBounds, -2), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(-2,-2)); 
				units[3] = new Hex(id, CalculateStartX(imgBounds, -3), CalculateStartY(imgBounds, -3), imgBounds, new Coordinate(-3,-3));
				break;
				
			case 1: // Diagonally left wise
				xOffset = (int)(imgBounds * 0.825 * 2 + 2 * ratio);
				startingY += (int)(1.25 * imgBounds) + 1;
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
				units[1] = new Hex(id, CalculateStartX(imgBounds, 1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(1,-1)); 
				units[2] = new Hex(id, CalculateStartX(imgBounds, 2), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(2,-2)); 
				units[3] = new Hex(id, CalculateStartX(imgBounds, 3), CalculateStartY(imgBounds, -3), imgBounds, new Coordinate(3,-3));
				break;
				
			case 2: // Vertical
				xOffset = imgBounds / 2;
				startingY += 2 * imgBounds + 6;
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
				units[1] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(0,-2)); 
				units[2] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -4), imgBounds, new Coordinate(0,-4)); 
				units[3] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -6), imgBounds, new Coordinate(0,-6));
				break;
			}
		}
		
		// Creates a bone like shape with 5 hexs
		public void CreateSquare(int imgBounds, Random rand, int override){
			units = new Hex[4];
			ID id = ID.GREEN;
			int val = rand.nextInt(3);
			
			if(override != -1) // spawns a specific shape based on the override
        		val = override;
			
			type = val + "Square";
			switch(val){
			case 0: // diagonally down right
				xOffset = (int)(imgBounds * 0.175);
				startingY += (int)(imgBounds * 1.25);
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
				units[1] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(0,-2)); 
				units[2] = new Hex(id, CalculateStartX(imgBounds, -1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(-1,-1));
				units[3] = new Hex(id, CalculateStartX(imgBounds, -1), CalculateStartY(imgBounds, -3), imgBounds, new Coordinate(-1,-3));
				break;
				
			case 1: // diagonally down left
				xOffset = (int)(imgBounds * 0.825);
				startingY += (int)(imgBounds * 1.25);
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
				units[1] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(0,-2)); 
				units[2] = new Hex(id, CalculateStartX(imgBounds, 1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(1,-1));
				units[3] = new Hex(id, CalculateStartX(imgBounds, 1), CalculateStartY(imgBounds, -3), imgBounds, new Coordinate(1,-3)); 
				break;
				
			default:
				xOffset = imgBounds / 2;
				startingY += imgBounds;
				units[0] = new Hex(id, startingX, startingY, imgBounds, new Coordinate(0,0));
				units[1] = new Hex(id, CalculateStartX(imgBounds, 0), CalculateStartY(imgBounds, -2), imgBounds, new Coordinate(0,-2)); 
				units[2] = new Hex(id, CalculateStartX(imgBounds, -1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(-1,-1));
				units[3] = new Hex(id, CalculateStartX(imgBounds, 1), CalculateStartY(imgBounds, -1), imgBounds, new Coordinate(1,-1)); 
				break;
			}
		}
		
		@Override
		public void act(float delta){
			// Shape is entering the grid
			if(activated == null || !activated){
				// Shape is not at starting X/Y
				if((startingX - 01 > getX() || getX() > startingX + 0.1) || 
	            		(startingY - 01 > getY() || getY() > startingY + 0.1)){
	            	originate();
	            }
				// Shape has entered the grid
				else if(activated == null){
					// Loops through all the hexs in the shape an applies their id to the grid
					for(int i = 0; i < units.length; i++){
						GetGridHex(units[i].cor.X, units[i].cor.Y).setID(units[i].id);
					}
					
					// Updates grid
					UpdateGrid(this);

					// Bomb Power Up
					if(units[0].id == ID.BOMB)
						Explode(GetGridHex(units[0].cor.X, units[0].cor.Y));
					
					// Reinitializes itself as a new shape
					if(timesToDup > 0){ 
						box.CreateTypeShape(9 + type.substring(1));
						timesToDup--;
					}
					else
						box.NewShape(CreateShape(box.spawnX, IMG_SIZE, box));
					
					WriteGameFile(false);
					
					// Remove the shape and its hexs
					for(int j = 0; j < units.length; j++){
						units[j].remove();
					}
					this.remove();

					// Trophy Unlock
					if(rank == 3 && boxOne.shape.units[0].id == ID.BOMB && boxTwo.shape.units[0].id == ID.BOMB){
						unlockTrophyIndex = 11;
					}
				}
			}
			// Shape is activated and is following cursor
			else{
            	setAllX(Gdx.input.getX() - xOffset);
            	setAllY(Gdx.input.getY() - (int)(IMG_SIZE * 1.25));
            }
		}
		
		// Computes the hexs coordinates to draw positions, used for initializing the hex position
		private float CalculateStartX(int b, int corX){
			int space = (int)(b / 40 * Gdx.graphics.getDensity() / 2);
			float value = (float)(corX * (0.75 * b + space + 4));
			
			return value + startingX;
		}
		
		// Computes the hexs coordinates to draw positions, used for initializing the hex position
		private float CalculateStartY(int b, int corY){
			int space = (int)(b / 40 * Gdx.graphics.getDensity() / 2);
			float value = (float)(corY * (b * 0.9 / 2 + space));
			
			return value + startingY;
		}
		
		// Moves the shape to its starting positions along with its units
		public void originate(){
			// Calculate x and y difference
			float xDif = startingX - getX();
			float yDif = startingY - getY();
			
			//Move the shape closer to the point
			if(activated == null){
				setAllX(getX() + xDif / 2);
				setAllY(getY() + yDif / 2);
			}
			else{
				setAllX(getX() + xDif / 7);
				setAllY(getY() + yDif / 7);
			}
		}
		
		// Sets the X values of the shape and all of its units
		public void setAllX(float givenX){
			float dif = getX() - givenX;
			setX(givenX);
			
			for(int i = 0; i < units.length; i++){
				units[i].setX(units[i].getX() - dif);
			}
		}
		
		// Sets the Y values of the shape and all of its units
		public void setAllY(float givenY){
			float dif = getY() - givenY;
			setY(givenY);
			
			for(int i = 0; i < units.length; i++){
				units[i].setY(units[i].getY() - dif);
			}
		}
	}
	
	// ------------------------------------------------------- \\
	// --------------- START OF SCENE 2D CLASS --------------- \\
	// ------------------------------------------------------- \\
	
	Stage stage;
	BitmapFont font;
	ClickBox boxOne, boxTwo;
	TrophyBanner banner;
	boolean paused, gameOver;
    public int[] spawnRates = new int[6];
    public int IMG_SIZE, points, timesToDup, pointGoal, unlockTrophyIndex, rank, unlockAmount;
    public double goalRatio;
    float ratio;
    String extension;
    
    // Sprite sheet containing the animations needed for the hexagons
    // Colors in order - Gray, Blue, Red, Yellow, Green, Purple
    public Texture hexSheet;
	
	// Dictionary of Dictionary of Hexs, Use y coordinate than x coordinate to retrieve the scene actor
	Map<Integer, Map<Integer, Actor>> dict = new HashMap<Integer, Map<Integer, Actor>>();
	
    @Override
    public void create() {  
    	font = new BitmapFont(true);
    	font.setColor(Color.BLACK);
    	String repr = Gdx.graphics.getWidth() + "*" + Gdx.graphics.getHeight();
    	ratio = DetermineRatio(repr);
    	rank = 1;
    	paused = false;
    	gameOver = false;
    	unlockTrophyIndex = -1;
    	
    	for(int i = 0; i < 6; i++){
    		spawnRates[i] = 20;
    	}
    	spawnRates[5] = 0;
    	points = 0;
    	timesToDup = 0;
    	pointGoal = 1500;
    	goalRatio = 1.7D;
    	
    	float screenWidth = Gdx.graphics.getWidth();
    	float screenHeight = Gdx.graphics.getHeight();
    	
    	OrthographicCamera camera = new OrthographicCamera(screenWidth / ratio, screenHeight / ratio);
    	camera.setToOrtho(true);
    	
    	stage = new Stage(new ExtendViewport(screenWidth, screenHeight, camera));
    	Gdx.input.setInputProcessor(stage);
        Hex myHex;
        extension = "";
        
        // Array that gives the coordinates where a hex shouldn't be drawn
        int[][] emptyLoc = { {0,0}, {1,1}, {2,0}, {6,0}, {7,1}, {8,0}, {0,2}, {8,2}, {0,14}, 
        		{1,15}, {7,15}, {8,14}, {0,16}, {1,17}, {2,16}, {3,17}, {5,17}, {6,16}, {7,17}, {8,16}}; 
        int index = 0;

        // Attempts to find the right texture sizes
        if(Gdx.graphics.getHeight() - 16 * 82 > 0 && Gdx.graphics.getWidth() - 8 * 82 > 0){
        	extension = "(xl)";
        	IMG_SIZE = 82;
        }
        else if(Gdx.graphics.getHeight() - 16 * 72 > 0 && Gdx.graphics.getWidth() - 8 * 72 > 0){
        	extension = "(l)";
        	IMG_SIZE = 72;
        }
        else if(Gdx.graphics.getHeight() - 16 * 62 > 0 && Gdx.graphics.getWidth() - 8 * 62 > 0){
        	extension = "(h)";
        	IMG_SIZE = 62;
        }
        else if(Gdx.graphics.getHeight() - 16 * 42 > 0 && Gdx.graphics.getWidth() - 8 * 42 > 0){
        	extension = "(m)";
        	IMG_SIZE = 42;
        }
        else{
        	extension = "(s)";
        	IMG_SIZE = 30;
        }
        
        extension = "(l)";
        IMG_SIZE = 82;
        
		// Initializes variables
		int space = (int)(IMG_SIZE / 40 * ratio);
		hexSheet = new Texture(Gdx.files.internal("HexSheet" + extension + ".png"), true);
		
		// Determines the horizontal and vertical offsets needed to center the grid
		float centralizeX = (int)((screenWidth - 9 * (space + 4 + IMG_SIZE * 0.75)) / 2) - 8;
		float centralizeY = (int)((screenHeight - 10 * (space + IMG_SIZE)) / 2);
		
		if(ratio == Gdx.graphics.getDensity() / 3f)
			centralizeX -= 4;
		
        // Populates the grid from top row moving down
        for(int y = 0; y < 9; y++){
        	
    		// Fills first dictionary
    		dict.put(y * 2, new HashMap<Integer, Actor>());
    		dict.put(y * 2 + 1, new HashMap<Integer, Actor>());
    		
        	for(int x = 0; x < 9; x++){
        		// Temp variables used in grid creation
            	int corY = y * 2;
    			float yOffset = centralizeY;
        		if(x % 2 == 1){
        			yOffset += IMG_SIZE * 0.93 / 2;
        			corY += 1;
        		}
        		
        		// Index is in the empty section
        		if(index != emptyLoc.length && emptyLoc[index][0] == x && emptyLoc[index][1] == corY){
        			index += 1;}
        		
        		// Index is in the normal grid and creates the hex
        		else{
	        		myHex = new Hex(ID.NONE, (float)(x * (IMG_SIZE * 0.75 + space + 4) + centralizeX), 
	        				(float)(y * (IMG_SIZE * 0.93 + space) + yOffset), IMG_SIZE, 
	        				new Coordinate(x, corY));
	                stage.addActor(myHex);
	                
	                // Fills second dictionary
	                dict.get(corY).put(x, myHex);
        		}
        	}
        }
        
        // Used in clickBox creation
        int height = (int)(IMG_SIZE * 4.5);
        int width = (int)(screenWidth/ 2);
        
        // Creates the first click box
        boxOne = new ClickBox(0, (int)(screenHeight- height), width, height);
        boxOne.NewShape(CreateShape(width / 2, IMG_SIZE, boxOne));
        boxOne.setTouchable(Touchable.enabled);
        stage.addActor(boxOne);
        
        // Creates second click box
        boxTwo = new ClickBox(width, (int)(screenHeight- height), width - 1, height);
        boxTwo.NewShape(CreateShape(width * 1.5f, IMG_SIZE, boxTwo));
        boxTwo.setTouchable(Touchable.enabled);
        stage.addActor(boxTwo);
        
        ReadGameFile();
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
    
    // Touch listener used to pause the game
    public InputListener pause = new InputListener(){
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			pause();
            return true;
		}
    };
    
    // Called on a shape when the user has just released the screen
    private void ShapeRelease(ClickBox b){
        b.setX(b.spawnX - b.getWidth() / 2);            
		Shape target = b.shape;
        target.activated = false;

        // Holds all the hexs in the grid
        Hex[] grid = GetGridArray();
        
        for(int j = 0; j < grid.length; j++){
        	// Finds a hexagon in the grid that the center point of the first unit hex 
        	// in the shape collides with
        	if(grid[j].CollisionCheck(target.units[0].getX() + IMG_SIZE / 2, 
        			target.units[0].getY() + IMG_SIZE / 2)){
        		// Variables used in looping
        		boolean canFit = true;
        		int i = 0;
        		
        		// Preform shape collision check with every hex in the 
        		while( i < target.units.length && canFit){
    				Hex h = GetGridHex(target.units[i].cor.X + grid[j].cor.X,
        					target.units[i].cor.Y + grid[j].cor.Y);
    				
    				// Cant fit if the hex is filled or doesn't exist
        			if(h == null || h.id != ID.NONE){ 
        				canFit = false; 
        			}
        			
        			i += 1;
        		}
        		
        		// If the shape passed the test, set up placement variables
        		if(canFit){
        			target.startingX = GetGridHex(grid[j].cor.X, grid[j].cor.Y).getX();
        			target.startingY = GetGridHex(grid[j].cor.X, grid[j].cor.Y).getY();
        			target.activated = null;
        			
        			// Sets all the cor values of the shape
        			for(int k = 0; k < target.units.length; k++){
            			target.units[k].cor.X = target.units[k].cor.X + grid[j].cor.X;
            			target.units[k].cor.Y = target.units[k].cor.Y + grid[j].cor.Y;
        			}
        		}
        		break;
        	}
        }
        
        b.toFront();
    }
    
    
    // Creates a random shape and returns it
    public Shape CreateShape(float x, int IMG_SIZE,  ClickBox c){
		Shape shape = new Shape(x, IMG_SIZE, c);
        return shape;
    }
    
    // Creates an explosion and at a given hexagon
    public void Explode(Hex h){
		boolean allYellow = true;
    	h.setID(ID.TEAL);
    	h.srcRect.x = 0;
    	h.animate = true;
    	
    	// Variables needed to explode the rest of the tiles
    	int numHexes = 1;
    	int delay = 3;
    	Coordinate[] cors = { new Coordinate(h.cor.X, h.cor.Y), new Coordinate(-1, -1), new Coordinate(0, -2), 
    			new Coordinate(1, -1), new Coordinate(-1, 1), new Coordinate(0, 2), new Coordinate(1, 1) };
    	
    	// Applies the explosion to the hexes
    	for(int i = 1; i < cors.length; i++){
    		h = GetGridHex(cors[0].X + cors[i].X, cors[0].Y + cors[i].Y);

    		// Animates if it exists and is non empty
    		if(h != null){
    			// Adds to points if the tile wasnt empty
    			if(h.id != ID.NONE)
        			numHexes += 1;
    			
    			if(h.id != ID.YELLOW)
    				allYellow = false;
    			
    			h.setID(ID.TEAL);
    			h.animate = true;
    			h.frameCount += delay;
    			h.srcRect.x = 0;
    		}
    	}
    	
    	// Achievement unlocks
    	if(rank == 1 && numHexes == 2){
    		unlockTrophyIndex = 2;
    	}
    	else if(rank == 2 && numHexes == 1){
    		unlockTrophyIndex = 7;
    	}
		else if(rank == 3 && numHexes == 7){
			unlockTrophyIndex = 8;
		}
		else if(rank == 4 && numHexes == 7 && allYellow){
			unlockTrophyIndex = 13;
		}
		else if(rank == 8){
			unlockTrophyIndex = 29;
			unlockAmount = 10 + (numHexes - 1) * 13;
		}
    	
    	points += 10 + (numHexes - 1) * 13;
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void render() {    
        stage.act(Gdx.graphics.getDeltaTime());
        
        // Determines if a shape was just released and runs release code if it was
        if(!Gdx.app.getInput().isTouched()){
        	if(boxOne.shape.activated != null && boxOne.shape.activated)
        		ShapeRelease(boxOne); 
        	
        	else if(boxTwo.shape.activated != null && boxTwo.shape.activated)
        		ShapeRelease(boxTwo); 
        }
        
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    	stage.getViewport().update(width, height);
    	stage.getCamera().position.set(stage.getCamera().viewportWidth / 2, stage.getCamera().viewportHeight / 2, 0);
    }

    @Override
    public void pause() {
    	boxOne.setTouchable(Touchable.disabled);
    	boxTwo.setTouchable(Touchable.disabled);
    	paused = true;
    }

    @Override
    public void resume() {
    	boxOne.setTouchable(Touchable.enabled);
    	boxTwo.setTouchable(Touchable.enabled);
    	paused = false;
    }
    
    // Alters the spawn rates of shapes based on the given index that is decrementing
    public void ChangeSpawnRates(int index){
    	// Holds the amount the spawnRates will fluctuate by
    	int change = 1;
    	if(index == 5) // is doubled when the index is the ultra hex
    		change = 2;
    	
    	// Applies the change in spawn rates
    	for(int i = 0; i < spawnRates.length; i++){
    		if(i != index)
    			spawnRates[i] += change;
    		else
    			spawnRates[i] -= change * 5;
    	}
    }
    
    // Returns the hexs in the grid as an array
    public Hex[] GetGridArray(){
    	Hex[] grid = new Hex[61]; 
    	int index = 0;
        
        // Populates grid using the dictionary
        for (int y = 0; y < 18; y++){
        	Object[] temp = dict.get(y).values().toArray();
        	
        	// Loops through each hex in the temp array
        	for(int x = 0; x < temp.length; x++){
        		grid[index] = (Hex)temp[x];
        		index += 1;
        	}
        }
        
        return grid;
    }
    
    // Returns a specific hexagon with given dictionary keys
    public Hex GetGridHex(int x, int y){
    	// Attempts to return the hex
    	try{ 
    		return ((Hex)dict.get(y).get(x)); 
    	}
		// Catches error when given bad keys into dictionary
		catch(NullPointerException ne){ 
			return null; 
		}
    }
    
    // Function called when the game is done
    public void EndGame(){
    	WriteGameFile(true);
    	gameOver = true;
    	pause();
    }
    
    // Updates the grid with the newely placed shape
    public void UpdateGrid(Shape s){
    	ArrayList<Hex> startPos = new ArrayList<Hex>();
    	ArrayList<String> type = new ArrayList<String>();
    	int startGoal = 1500;
    	
    	// Gets all the lines that need to be popped
    	GetLineStart(s.units, startPos, type);
    	ArrayList<ArrayList<Hex>> lines = GetLinesPop(startPos, type);
    	int numHexes = 0;
    	
    	//Pops the lines that need to be popped
    	for(int i = 0; i < lines.size(); i++){
    		startPos.clear();
    		PopLine(lines.get(i), s, startPos);
    		numHexes += lines.get(i).size();
    	}
    	
    	points += (int)(numHexes * 13 * lines.size());
    	
    	// Starts duplication if point goal is reached
    	if(points >= pointGoal){
    		if(goalRatio > 0.5) // duplication rate increases as goalRatio goes down until its linear
    			goalRatio -= 0.02;
    		
    		pointGoal += (int)(startGoal * goalRatio);
	    	timesToDup = 2;
    	}
    	
    	// Achievement unlocks
    	if(rank == 1){
    		unlockTrophyIndex = 0;
    		unlockAmount = lines.size();
    		
    		if(lines.size() >= 3){
        		unlockTrophyIndex = 1;
				unlockAmount = 1;
        	}
    	}
    	// Rank 2 unlocks
    	else if(rank == 2){ 
    		if(s.units[0].id == ID.BOMB && lines.size() >= 2){
    			unlockTrophyIndex = 4;
    		}
    		else if(s.units.length == 7 && lines.size() >= 2){
    			unlockTrophyIndex = 6;
    		}
    	}
    	// Rank 3 unlocks
		else if(rank == 3 && lines.size() == 1 && lines.get(0).size() == 5){ 
			int[] checks = countLineColors((Hex[]) lines.get(0).toArray());

			// Rainbow achievement unlocked
			if(checks[0] == 1 && checks[1] == 1 && checks[2] == 1 && checks[3] == 1 && checks[4] == 1){
				unlockTrophyIndex = 9;
			}
		}
    	// Rank 4 unlocks
		else if(rank == 4 && lines.size() >= 5){ 
			unlockTrophyIndex = 15;
		}
    	// Rank 5 unlocks
		else if(rank == 5){ 
			if(lines.size() >= 4 && s.units[0].id == ID.RED){
				unlockTrophyIndex = 19;
			}
			else{
				for(int i = 0; i < lines.size(); i++){
					if(lines.get(i).size() == 9){
						int[] checks = countLineColors((Hex[]) lines.get(i).toArray());

						if(checks[0] == 9)
							unlockTrophyIndex = 16;
						else if(checks[2] == 9)
							unlockTrophyIndex = 17;
						else if(checks[3] == 9)
							unlockTrophyIndex = 18;
					}
				}
			}
		}
    	// Rank 6 unlocks
		else if(rank == 6){ 
			if(lines.size() >= 4 && points - (int)(numHexes * 13 * lines.size()) == 0){
				unlockTrophyIndex = 20;
			}
			else if(lines.size() == 2){
				boolean allYellow = true;

				// Determines if both lines are completely yellow
				for(int i = 0; i < 2; i++){
					Hex[] line = (Hex[]) lines.get(i).toArray();
					for(int j = 0; j < line.length; j++){
						if(line[j].id != ID.YELLOW){
							allYellow = false;
						}
					}
				}

				if(allYellow){
					unlockTrophyIndex = 22;
				}
			}
			else if(lines.size() >= 2){
				int count = 0;

				for(int i = 0; i < lines.size(); i++){
					if(lines.get(i).size() == 9){
						count++;
					}
				}

				if(count >= 2){
					unlockTrophyIndex = 23;
				}
			}
		}
    	// Rank 7 unlocks
		else if(rank == 7){ 
			if(lines.size() >= 5){
				unlockTrophyIndex = 25;
			}
			else if(s.units[0].id == ID.BOMB && lines.size() == 3){
				unlockTrophyIndex = 26;
			}
			else if(lines.size() == 2 && lines.get(0).size() == 5 && lines.get(1).size() == 5){
				int[] checks = countLineColors((Hex[]) lines.get(0).toArray());

				if(checks[0] == 1 && checks[1] == 1 && checks[2] == 1 && checks[3] == 1 && checks[4] == 1){
					checks = countLineColors((Hex[]) lines.get(1).toArray());

					if(checks[0] == 1 && checks[1] == 1 && checks[2] == 1 && checks[3] == 1 && checks[4] == 1){
						unlockTrophyIndex = 27;
					}
				}
			}
		}
    	// Rank 8 unlocks
		else if(rank == 8){ 
			if((boxOne.shape.units.length == 7 && s != boxOne.shape) || (boxTwo.shape.units.length == 7 && s != boxTwo.shape)){
				unlockTrophyIndex = 28;
				unlockAmount = lines.size();
			}
			else if(lines.size() == 3){
				boolean colorsOk = true;

				for(int i = 0; i < 3; i++){
					Hex[] line = (Hex[]) lines.get(i).toArray();
					for(int j = 0; j < line.length; j++){
						if(line[j].id != ID.GREEN && line[j].id != ID.YELLOW){
							colorsOk = false;
						}
					}
				}

				if(colorsOk){
					unlockTrophyIndex = 31;
				}
			}
			else if(lines.size() >= 1){
				int amount = 0;

				for(int i = 0; i < lines.size(); i++){
					if(lines.get(i).size() == 9){
						amount++;
					}
				}

				unlockTrophyIndex = 30;
				unlockAmount = amount;
			}
		}
    	// Rank 9 unlocks
		else if(rank == 9){ 
			if(lines.size() >= 7){
				unlockTrophyIndex = 32;
			}
			else if(lines.size() == 5 && s.units[0].id == ID.PURPLE){
				unlockTrophyIndex = 33;
			}
			else if(lines.size() >= 4 && s.units.length == 7 && s.units[0].cor.getX() == 0 &&
					s.units[0].cor.getY() == 0 ){
				unlockTrophyIndex = 35;
			}
		}
    }

	// Determines how often every color appears in the line
	private int[] countLineColors(Hex[] line){
		int[] checks = {0, 0, 0, 0, 0};

		for(int i = 0; i < line.length; i++){
			switch(line[i].id){
				case BLUE:
					checks[0] += 1;
					break;

				case GREEN:
					checks[1] += 1;
					break;

				case PURPLE:
					checks[2] += 1;
					break;

				case YELLOW:
					checks[3] += 1;
					break;

				case RED:
					checks[4] += 1;
					break;
			}
		}

		return checks;
	}

    // Helper function used to check if a hex is already inside the array
    public boolean ToAddHex(Hex hex, String str, ArrayList<Hex> values, ArrayList<String> types){
    	for(int i =0; i<values.size(); i++){
    		if (values.get(i) == hex && types.get(i) == str){
    				return false;
    		}
    	}
    	
    	return true;
    }
    
    public void GetLineStart(Hex[] hexes, ArrayList<Hex> startPos, ArrayList<String> type){
    	for(int i =0; i<hexes.length; i++){
    		int x = hexes[i].cor.X;
    		int y = hexes[i].cor.Y;
    		//first if statement for vertical lines
    		//&& ToAddHex(GetGridHex(x,4-x), true, startPos, type)
    		if(x <= 4){
    			if(ToAddHex(GetGridHex(x,4-x), "V", startPos, type)){
	    			startPos.add(GetGridHex(x,4 - x));
	    			type.add("V");
    			}
    		}
    		else {
    			if (ToAddHex(GetGridHex(x,x-4), "V", startPos, type)){
	    				startPos.add(GetGridHex(x,x-4));
	    				type.add("V");
    			}
    		}
    		
    		//second if statement for diagonals going up-right
    		if(x + y <= 10 ){
	    		if(ToAddHex(GetGridHex(0, x+y), "U", startPos, type)){
	    			startPos.add(GetGridHex(0, x + y));
	    			type.add("U");
	    		}
    		}
    		else{
    			if(ToAddHex(GetGridHex((x+y-10)/2 - 1, 1 + x+y-(x+y-10)/2), "U", startPos, type)){
    				startPos.add(GetGridHex((x+y-10)/2 - 1, 1 + x+y-(x+y-10)/2));
    				type.add("U");
    			}
    		}
    		// last if statement for down-right diagonals
			if(y-x >= 4){
				if(ToAddHex(GetGridHex(0, y-x), "D", startPos, type)){
					startPos.add(GetGridHex(0, y-x));
					type.add("D");
				}
			}
			else{
				if(x-y ==4){ 
					if(ToAddHex(GetGridHex(4,0), "D", startPos, type) && ToAddHex(GetGridHex(0, y-x), "D", startPos, type)){
						startPos.add(GetGridHex(4,0));
						type.add("D");
					}
				}
				else if(x-y == 2){
					if(ToAddHex(GetGridHex(3,1),"D", startPos, type)){
						startPos.add(GetGridHex(3,1));
						type.add("D");
					}
				}
				
				else if(y == x) {
					if(ToAddHex(GetGridHex(2,2), "D", startPos, type)){
						startPos.add(GetGridHex(2,2));
						type.add("D");
					}
				}
				else {
					if(ToAddHex(GetGridHex(1,3), "D", startPos, type)){
						startPos.add(GetGridHex(1,3));
						type.add("D");
					}	
				}
			}
    	}
    }
    
    // Loops through all the lines and keeps only the filled ones
    public ArrayList<ArrayList<Hex>> GetLinesPop(ArrayList<Hex> hexes, ArrayList<String> types){
    	ArrayList<ArrayList<Hex>> lines = new ArrayList<ArrayList<Hex>>();
    	
    	for(int i =0; i<types.size(); i++){
    		int x = hexes.get(i).cor.X;
    		int y = hexes.get(i).cor.Y;
    		ArrayList<Hex> line = new ArrayList<Hex>();
    		lines.add(line);
    		
    		while(GetGridHex(x,y) != null){
    			if(GetGridHex(x,y).id == ID.NONE) {
			        lines.remove(line);
			        break;
			    }
    			
    			lines.get(lines.indexOf(line)).add(GetGridHex(x,y));
    			
    			if(types.get(i) == "V"){
    				y+=2;
    			}
    			else if(types.get(i) == "U"){
    				x++;
    				y--;
    			}
    			else{
    				x++;
    				y++;
    			}
    		}
    	}
    	
    	return lines;
    }    
    
    // Sets the animation for a filled line on the grid
    // Precondition: temp must be an empty arraylist
    private void PopLine(ArrayList<Hex> line, Shape s, ArrayList<Hex> temp){
    	Boolean flipped = true;
    	int delay = 0;
    	
    	// Loops through the line
    	for(int i = 0; i < line.size(); i++){
    		temp.add(line.get(i));
    		
    		// Top section of the array has been appended
    		if(IsShapeHex(line.get(i), s) && flipped){
    			flipped = false;
    			
    			// Applies the animation delay
				for(int k = temp.size() - 1; k >= 0; k--){
					if(temp.get(k).animate != null && !temp.get(k).animate){
						temp.get(k).frameCount += delay;
						temp.get(k).animate = true;
						temp.get(k).srcRect.x = 0;
						delay += 3;
					}
				}
				delay = 3;
				temp.clear();
    		}
    	}
    	
    	// Delay currently holds the index of the first appearance of a shape hex
    	// Applies animation delay to bottom half
    	for(int n = 0; n < temp.size(); n++){
    		if(IsShapeHex(temp.get(n), s))
    			delay = 0;
    		
    		if(temp.get(n).animate != null && !temp.get(n).animate){
	    		temp.get(n).frameCount += delay;
				temp.get(n).animate = true;
				temp.get(n).srcRect.x = 0;
    		}
    		delay += 3;
    	}
    }
    
    //Helper function used to determine if a hexagon is part of a shape
    public boolean IsShapeHex(Hex h, Shape s){
    	int i = 0;
    	
		// Loops through all the hexs in the shape and determines if the hex is part of it
		while(i < s.units.length){
			// If its the first hex in the shape
			if(h.cor.X == s.units[i].cor.X && h.cor.Y == s.units[i].cor.Y) 
				return true;
			i++;
		}
		
		return false;
    }
	
	// Preforms a randomly animated spawn grid animation
	public void SpawnGrid(){
		Hex[] grid = GetGridArray();
		ArrayList<Hex> remainingHexs = new ArrayList<Hex>();
		Random rand = new Random();
		
		// Populates array list
		for(int i = 0; i < grid.length; i++){
			remainingHexs.add(grid[i]);
		}
		
		// Randomizes the hex spawn order
		for(int j = 0; j < grid.length; j++){
			int index = rand.nextInt(remainingHexs.size());
			remainingHexs.get(index).spawn((int)(j*0.85));
			remainingHexs.remove(index);
		}
	}
    
    // Helper function used to determine if a shape can fit into the grid given a start hex cor
    public boolean CanShapeFit(Shape s, Coordinate c){
    	boolean shapeFits = true;
    	int i = 0;
    	
    	// Loops through all the hexs in the shape and determines if the shapes fits in the grid
		while(shapeFits && i < s.units.length){
			Hex h = GetGridHex(c.X + s.units[i].cor.X, c.Y + s.units[i].cor.Y);
			
			// If the shape doesn't fit, set shapeFits to false
			if(h == null || (h.id != ID.NONE && h.animate != null && !h.animate)) 
				shapeFits = false;
			i++;
		}
		
		return shapeFits;
    }
    
	// Reads the game file from internal storage to load into the game
	public void ReadGameFile(){
		// Attempts to read the file
		try{
			FileHandle file = Gdx.files.local("gameFile.txt");
			String[] lines = file.readString().split(" ");
			
			// Holds if the previos game was finished or not
			boolean newGame = Boolean.parseBoolean(lines[0]);
			
			// Continues reading if the last game was not finished
			if(!newGame){
				points = Integer.valueOf(lines[1]);
				pointGoal = Integer.valueOf(lines[2]);
				goalRatio = Double.valueOf(lines[3]);
				timesToDup = Integer.valueOf(lines[4]);
				boxOne.CreateTypeShape(lines[5]);
				boxTwo.CreateTypeShape(lines[6]);

				// Reads the spawn rates
				for(int j = 0; j < spawnRates.length; j++){
					spawnRates[j] = Integer.valueOf(lines[j + 7]);
				}
				
				Hex[] grid = GetGridArray();
				// Loops through the grid section and applies their id
				for(int i = 0; i < grid.length; i++){
					grid[i].setID(StringToID(lines[i + spawnRates.length + 7]));
				}
			}
		}
		// If the file doesnt exist, copy it to the local folder
		catch(Exception e){ 
			Gdx.files.internal("gameFile.txt").copyTo(Gdx.files.local("gameFile.txt"));
		}
		
	    SpawnGrid();
	}
	
	// Writes the game file into internal storage
	public void WriteGameFile(boolean gameFinished){ 
		FileHandle file = Gdx.files.local("gameFile.txt");
		boolean shapesFit = false;
		
		// Writes if the game has finished or not and overwrites the file
		file.writeString(gameFinished + " ", false);
		
		if(!gameFinished){
			// Writes the points and duplication variablesinto the file
			file.writeString(points + " ", true);
			file.writeString(pointGoal + " ", true);
			file.writeString(goalRatio + " ", true);
			file.writeString(timesToDup + " ", true);
			
			// Writes the two shapes in holding
			file.writeString(boxOne.shape.type + " ", true);
			file.writeString(boxTwo.shape.type + " ", true);
	
			// Writes the spawn rates
			for(int j = 0; j < spawnRates.length; j++){
				file.writeString(spawnRates[j] + " ", true);
			}
			
			Hex[] grid = GetGridArray();
			int count = 0;

			// Writes the ids of the grid as they appear in the grid array
			for(int i = 0; i < grid.length; i++){
				ID id = grid[i].id;
				
				// Fixes case where colors reappear since they change ids after they pop
				if(grid[i].animate == null || grid[i].animate)
					id = ID.NONE;

				// Increments count if the hex is colored
				if(id != ID.NONE)
					count++;

				file.writeString(IDToString(id) + " ", true);
				
				// Attempts to see if atleast one of the current shapes is placeable on the grid
				if(!shapesFit) // Shape One
					shapesFit = CanShapeFit(boxOne.shape, grid[i].cor);
				
				if(!shapesFit) // Shape Two
					shapesFit = CanShapeFit(boxTwo.shape, grid[i].cor);
			}

			// Ends the game if none of the current shape can fit into the grid
			if(!shapesFit){
				EndGame();
			}
			// Trophy unlocks
			else if(rank == 4 && count >= 39 && shapesFit){
				unlockTrophyIndex = 12;
			}
			else if(rank == 4 && count == 0 && points >= 8000){
				unlockTrophyIndex = 14;
			}
			else if(rank == 7 && count == 37 && outerRingEmpty()){
				unlockTrophyIndex = 24;
			}
		}
	}

	// Returns if the outer ring of the grid is empty
	private boolean outerRingEmpty(){ // TODO: FINISH THIS
		return GetGridHex(4, 14).id == ID.NONE;
	}
}

