package com.hexrfull.game.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.hexrfull.game.GoogleServices;
import com.hexrfull.game.HexrGame;

public class AndroidLauncher extends AndroidApplication implements GoogleServices{
	private GameHelper _gameHelper;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create the GameHelper.
		_gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		_gameHelper.enableDebugLog(false);

		GameHelperListener gameHelperListener = new GameHelper.GameHelperListener(){
		@Override
		public void onSignInSucceeded(){
		}

		@Override
		public void onSignInFailed(){
		
		}};

		_gameHelper.setup(gameHelperListener);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new HexrGame(this), config);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		_gameHelper.onStart(this);
	}

	@Override
	protected void onStop(){
		super.onStop();
		_gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		_gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void signIn(){
		try{
			runOnUiThread(new Runnable(){
				//@Override
				public void run(){
					_gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut(){
		try{
			runOnUiThread(new Runnable(){
				//@Override
				public void run(){
					_gameHelper.signOut();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void rateGame(){
		// Replace the end of the URL with the package of your game
		String str ="https://play.google.com/store/apps/details?id=org.fortheloss.plunderperil";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void submitScore(long score){
		try{
			if(isSignedIn()){
				Games.Leaderboards.submitScore(_gameHelper.getApiClient(), getString(R.string.leaderboard_id), score);
				startActivityForResult(Games.Leaderboards.getLeaderboardIntent(_gameHelper.getApiClient(), getString(R.string.leaderboard_id)), 12345);
			}
		}
		catch(Exception e){ // Maybe sign in here then redirect to submitting score?
			signIn();
		}
	}

	@Override
	public void showScores(){
		if (isSignedIn() == true){
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(_gameHelper.getApiClient(), getString(R.string.leaderboard_id)), 12345);
		}
		else{ // Maybe sign in here then redirect to showing scores?
			signIn();
		}
	}

	@Override
	public boolean isSignedIn(){
		return _gameHelper.isSignedIn();
	}

	@Override
	public void unlockTrophy(int index) {
		if(isSignedIn()){
			index = getLeaderboardId(index);
			Games.Achievements.unlock(_gameHelper.getApiClient(), getString(index));
		}
	}
	
	@Override
	public void viewTrophies(){
		if(isSignedIn()){
			startActivityForResult(Games.Achievements.getAchievementsIntent(_gameHelper.getApiClient()), 54321);
		}
	}
	
	// Returns the appropriate string id based on the trophy index
	private int getLeaderboardId(int index){
		switch(index){
			case 0:
				return R.string.trophy_id_0;
				
			case 1:
				return R.string.trophy_id_1;
				
			case 2:
				return R.string.trophy_id_2;
				
			case 3:
				return R.string.trophy_id_3;
				
			case 4:
				return R.string.trophy_id_4;
				
			case 5:
				return R.string.trophy_id_5;
				
			case 6:
				return R.string.trophy_id_6;
				
			case 7:
				return R.string.trophy_id_7;
				
			case 8:
				return R.string.trophy_id_8;
				
			case 9:
				return R.string.trophy_id_9;
				
			case 10:
				return R.string.trophy_id_10;
				
			case 11:
				return R.string.trophy_id_11;
				
			case 12:
				return R.string.trophy_id_12;
				
			case 13:
				return R.string.trophy_id_13;
				
			case 14:
				return R.string.trophy_id_14;
				
			case 15:
				return R.string.trophy_id_15;
				
			case 16:
				return R.string.trophy_id_16;
				
			case 17:
				return R.string.trophy_id_17;
				
			case 18:
				return R.string.trophy_id_18;
				
			case 19:
				return R.string.trophy_id_19;
				
			case 20:
				return R.string.trophy_id_20;
				
			case 21:
				return R.string.trophy_id_21;
				
			case 22:
				return R.string.trophy_id_22;
				
			case 23:
				return R.string.trophy_id_23;
				
			case 24:
				return R.string.trophy_id_24;
				
			case 25:
				return R.string.trophy_id_25;
				
			case 26:
				return R.string.trophy_id_26;
				
			case 27:
				return R.string.trophy_id_27;
				
			case 28:
				return R.string.trophy_id_28;
				
			case 29:
				return R.string.trophy_id_29;
				
			default:
				return R.string.trophy_id_30;
		}
	}
}
