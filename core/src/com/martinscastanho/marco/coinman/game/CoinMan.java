package com.martinscastanho.marco.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;

	int manState;	// to make it look like he's walking
	int pause = 0;	// because otherwise the man runs way too fast
	float gravity = 0.4f;	// how quickly he falls down
	float velocity = 0;		// the y-axis velocity
	int manY = 0;	// the y-axis position

	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();
	Texture bomb;
	int bombCount;

	int score = 0;

	int gameState = 0;

	Rectangle manRectangle;
	Random random;

	BitmapFont font;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();	// randomize at which height the coins show up

		// show the score on the screen
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	@Override
	public void render () {
		// this method render() keeps being called on and on and on...
		batch.begin();

		drawBackground();

		if(gameState == 1){
			// GAME IS LIVE
			generateCoins();
			generateBombs();

			makeDudeWalk();
			makeDudeJump();
			makeDudeFall();
		}
		else if(gameState == 0){
			// Waiting to start
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}
		else if(gameState == 2){
			// GAME OVER
			makeDudeFall();

			if(Gdx.input.justTouched()){
				restart();
			}
		}

		drawDude();

		checkCoinCaught();
		checkBombCaught();

		// show the score
		font.draw(batch, String.valueOf(score), 100, 200);

		// in the end, when we have everything we want showing on screen
		batch.end();
	}

	public void drawBackground(){
		// show the background
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	}

	public void generateCoins(){
		coinCount++;
		// generate a coin every 100 renders
		if(coinCount >= 100){
			coinCount = 0;
			makeCoin();
		}

		coinRectangles.clear();
		// loop thought the coins to make them move
		for(int i=0; i<coinXs.size(); i++){
			batch.draw(coin, coinXs.get(i), coinYs.get(i));
			coinXs.set(i, coinXs.get(i)-4);
			coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
		}
	}

	public void generateBombs(){
		bombCount++;
		// generate a coin every 100 renders
		if(bombCount >= 271){
			bombCount = 0;
			makeBomb();
		}

		bombRectangles.clear();
		// loop thought the coins to make them move
		for(int i=0; i<bombXs.size(); i++){
			batch.draw(bomb, bombXs.get(i), bombYs.get(i));
			bombXs.set(i, bombXs.get(i)-6);
			bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
		}
	}

	public void makeDudeWalk(){
		pause++;
		// make the man move once every 8 renders
		if(pause >= 8) {
			pause = 0;
			manState++;

			if (manState >= 4) {
				manState = 0;
			}
		}
	}

	public void makeDudeJump(){
		// on screen touch...
		if(Gdx.input.justTouched()){
			// make the guy jump
			velocity = -20;
		}
	}

	public void makeDudeFall(){
		// make the guy fall
		velocity += gravity;
		manY -= velocity;

		// make him run on the ground, not fall below the ground
		if(manY <= 0){
			manY = 0;
		}
	}

	public void restart(){
		// reset everything
		gameState = 1;
		manY = Gdx.graphics.getHeight()/2;
		score = 0;
		velocity = 0;
		coinXs.clear();
		coinYs.clear();
		bombXs.clear();
		bombYs.clear();
		coinCount = 0;
		bombCount = 0;
		coinRectangles.clear();
		bombRectangles.clear();
	}

	public void drawDude(){
		// now the man appears on top of the background
		int manX = Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2;

		Texture dude = man[manState];
		if(gameState == 2){
			// GAME OVER
			dude = new Texture("dizzy-1.png");
		}

		batch.draw(dude, manX, manY);	// divided by 2 to show on the center fo the screen
		manRectangle = new Rectangle(manX, manY, man[manState].getWidth(), man[manState].getHeight());

	}

	public void checkCoinCaught(){
		// loop thought the coins to check if there was a collision
		for(int i=0; i<coinRectangles.size(); i++){
			if(Intersector.overlaps(manRectangle, coinRectangles.get(i))){
				score++;

				// remove the coin so that we don't score it more than once
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
	}

	public void checkBombCaught(){
		// loop thought the bombs to check if there was a collision
		for(int i=0; i<bombRectangles.size(); i++){
			if(Intersector.overlaps(manRectangle, bombRectangles.get(i))){
				Gdx.app.log("Collision!", "BOMB!");
				gameState = 2;
			}
		}
	}

	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);	// make the coin appear at a random height
		coinXs.add(Gdx.graphics.getWidth());	// make the coin appear right off the screen
	}

	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);	// make the bomb appear at a random height
		bombXs.add(Gdx.graphics.getWidth());	// make the bomb appear right off the screen
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
