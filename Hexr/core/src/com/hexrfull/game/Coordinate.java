package com.hexrfull.game;

// Coordinate class used by hexs to determine their relative location.
public class Coordinate {
	public int X;
	public int Y;
	
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
