package com.mygdx.catmario;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

//TODO: No idea
public class Main extends Game {
    public SpriteBatch batch;
    private int currentCharacter = 1;  // Default character
    private float currentCharacterX = 100;
    private float currentCharacterY = 150;
    private int currentScore = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Set full screen mode
        DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setFullscreenMode(displayMode);  // Set the game to full screen

        setScreen(new MainMenuScreen(this));  // Initializes Main Menu
    }

    @Override
    public void render() {
        super.render();  // Renders the current screen
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }

    // Getters and setters for game state
    public int getCurrentCharacter() {
        return currentCharacter;
    }

    public void setCurrentCharacter(int currentCharacter) {
        this.currentCharacter = currentCharacter;
    }

    public float getCurrentCharacterX() {
        return currentCharacterX;
    }

    public void setCurrentCharacterPosition(float x, float y) {
        this.currentCharacterX = x;
        this.currentCharacterY = y;
    }

    public float getCurrentCharacterY() {
        return currentCharacterY;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }
}













