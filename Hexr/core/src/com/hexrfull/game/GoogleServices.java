package com.hexrfull.game;

public interface GoogleServices {
	public void signIn();
	public void signOut();
	public void rateGame();
	public void submitScore(long score);
	public void showScores();
	public void unlockTrophy(int index);
	public void viewTrophies();
	public boolean isSignedIn();
}
