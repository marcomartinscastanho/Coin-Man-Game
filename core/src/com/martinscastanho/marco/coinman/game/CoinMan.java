package com.martinscastanho.marco.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

	Rectangle manRectangle;

	Random random;


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
	}

	@Override
	public void render () {
		// this method render() keeps being called on and on and on...
		batch.begin();

		// show the background
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


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


		pause++;
		// make the man move once every 8 renders
		if(pause >= 8) {
			pause = 0;
			manState++;

			if (manState >= 4) {
				manState = 0;
			}
		}

		// make the guy fall
		velocity += gravity;
		manY -= velocity;

		// make him run on the ground, not fall below the ground
		if(manY <= 0){
			manY = 0;
		}

		// on screen touch...
		if(Gdx.input.justTouched()){
			// make the guy jump
			velocity = -20;
		}

		// now the man appears on top of the background
		int manX = Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2;
		batch.draw(man[manState], manX, manY);	// divided by 2 to show on the center fo the screen
		manRectangle = new Rectangle(manX, manY, man[manState].getWidth(), man[manState].getHeight());

		// loop thought the coins to check if there was a collision
		for(int i=0; i<coinRectangles.size(); i++){
			if(Intersector.overlaps(manRectangle, coinRectangles.get(i))){
				Gdx.app.log("Collision!", "COIN!");
			}
		}

		// loop thought the bombs to check if there was a collision
		for(int i=0; i<bombRectangles.size(); i++){
			if(Intersector.overlaps(manRectangle, bombRectangles.get(i))){
				Gdx.app.log("Collision!", "BOMB!");
			}
		}


		// in the end, when we have everything we want showing on screen
		batch.end();
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
