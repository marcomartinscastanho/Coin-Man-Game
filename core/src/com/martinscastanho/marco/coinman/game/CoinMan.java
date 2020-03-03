package com.martinscastanho.marco.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;

	int manState;	// to make it look like he's walking
	int pause = 0;	// because otherwise the man runs way too fast
	float gravity = 0.2f;	// how quickly he falls down
	float velocity = 0;		// the y-axis velocity
	int manY = 0;	// the y-axis position


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
	}

	@Override
	public void render () {
		// this method render() keeps being called on and on and on...

		pause++;
		// make the man move once every 8 renders
		if(pause == 8) {
			pause = 0;
			manState++;

			if (manState == 4) {
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
			velocity = -10;
		}

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// now the man appears on top of hte background
		batch.draw(man[manState], Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY);	// divided by 2 to show on the center fo the screen


		// in the end, when we have everything we want showing on screen
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
