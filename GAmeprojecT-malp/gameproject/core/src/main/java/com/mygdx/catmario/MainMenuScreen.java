package com.mygdx.catmario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MainMenuScreen implements Screen, InputProcessor {

    private final Main game;
    private final SpriteBatch batch;
    private final Texture backgroundTexture;
    private final Texture newGameButton;
    private final Texture loadGameButton;
    private final Texture exitButton;
    private final Texture logo;
    private final Rectangle newGameBounds;
    private final Rectangle loadGameBounds;
    private final Rectangle exitBounds;
    private final Music menuMusic;
    private final Sound hoverSound;
    private final Sound clickSound;

    private boolean isHoveringNewGame = false;
    private boolean isHoveringLoadGame = false;
    private boolean isHoveringExit = false;

    // Add sound icons
    private final Texture soundOnIcon;
    private final Texture soundOffIcon;
    private final Texture soundEffectOnIcon;
    private final Texture soundEffectOffIcon;
    private final Rectangle soundIconBounds;
    private final Rectangle soundEffectIconBounds;

    private final float logoWidth = 650;
    private final float logoHeight = 750;

    // Adjusted icon size
    private final float iconSize = 100;  // Define a single size for all icons

    public MainMenuScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        backgroundTexture = new Texture("menubackground.png");
        newGameButton = new Texture("newgame.png");
        loadGameButton = new Texture("loadgame.png");
        exitButton = new Texture("exit.png");
        logo = new Texture("logo.png");

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menumusic.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f);

        hoverSound = Gdx.audio.newSound(Gdx.files.internal("hover.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.wav"));

        int buttonWidth = 260;
        int buttonHeight = 150;
        int buttonY = 220;

        float screenWidth = Gdx.graphics.getWidth();
        float spacing = 50;
        float totalButtonWidth = (3 * buttonWidth) + (2 * spacing);

        float startX = (screenWidth - totalButtonWidth) / 2;

        newGameBounds = new Rectangle(startX, buttonY, buttonWidth, buttonHeight);
        loadGameBounds = new Rectangle(startX + buttonWidth + spacing, buttonY, buttonWidth, buttonHeight);
        exitBounds = new Rectangle(startX + 2 * (buttonWidth + spacing), buttonY, buttonWidth, buttonHeight);

        soundOnIcon = new Texture("soundon.png");
        soundOffIcon = new Texture("soundoff.png");
        soundEffectOnIcon = new Texture("soundeffecton.png");
        soundEffectOffIcon = new Texture("soundeffectoff.png");

        soundIconBounds = new Rectangle(screenWidth - iconSize - 20, Gdx.graphics.getHeight() - iconSize - 20, iconSize, iconSize);
        soundEffectIconBounds = new Rectangle(screenWidth - (2 * iconSize) - 20, Gdx.graphics.getHeight() - iconSize - 30, iconSize, iconSize);
    }

    @Override
    public void show() {
        SoundManager.playMusic(menuMusic);  // Play menu music when the screen is shown
        Gdx.input.setInputProcessor(this);  // Set this screen as input processor after constructor finishes
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float logoX = (Gdx.graphics.getWidth() - logoWidth) / 2;
        float logoY = (Gdx.graphics.getHeight() - logoHeight) / 2 + 50;
        batch.draw(logo, logoX, logoY, logoWidth, logoHeight);

        batch.draw(newGameButton, newGameBounds.x, newGameBounds.y, newGameBounds.width, newGameBounds.height);
        batch.draw(loadGameButton, loadGameBounds.x, loadGameBounds.y, loadGameBounds.width, loadGameBounds.height);
        batch.draw(exitButton, exitBounds.x, exitBounds.y, exitBounds.width, exitBounds.height);

        // Draw icons according to the current sound settings
        if (SoundManager.isMusicEnabled()) {
            batch.draw(soundOnIcon, soundIconBounds.x, soundIconBounds.y, iconSize, iconSize);
        } else {
            batch.draw(soundOffIcon, soundIconBounds.x, soundIconBounds.y, iconSize, iconSize);
        }

        if (SoundManager.isSoundEffectsEnabled()) {
            batch.draw(soundEffectOnIcon, soundEffectIconBounds.x, soundEffectIconBounds.y, iconSize, iconSize);
        } else {
            batch.draw(soundEffectOffIcon, soundEffectIconBounds.x, soundEffectIconBounds.y, iconSize, iconSize);
        }

        batch.end();

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        checkHoverSound(touchX, touchY);

        if (Gdx.input.justTouched()) {
            if (newGameBounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);
                game.setScreen(new CharacterSelectScreen(game));
            } else if (loadGameBounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);
                game.setScreen(new LoadGameScreen(game));
            } else if (exitBounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);
                Gdx.app.exit();
            } else if (soundIconBounds.contains(touchX, touchY)) {
                SoundManager.toggleMusic(); // Toggle music
                SoundManager.playMusic(menuMusic); // Play or pause music based on new state
            } else if (soundEffectIconBounds.contains(touchX, touchY)) {
                SoundManager.toggleSoundEffects(); // Toggle sound effects
            }
        }
    }

    private void checkHoverSound(float touchX, float touchY) {
        if (newGameBounds.contains(touchX, touchY) && !isHoveringNewGame) {
            SoundManager.playSound(hoverSound);
            isHoveringNewGame = true;
        } else if (!newGameBounds.contains(touchX, touchY)) {
            isHoveringNewGame = false;
        }

        if (loadGameBounds.contains(touchX, touchY) && !isHoveringLoadGame) {
            SoundManager.playSound(hoverSound);
            isHoveringLoadGame = true;
        } else if (!loadGameBounds.contains(touchX, touchY)) {
            isHoveringLoadGame = false;
        }

        if (exitBounds.contains(touchX, touchY) && !isHoveringExit) {
            SoundManager.playSound(hoverSound);
            isHoveringExit = true;
        } else if (!exitBounds.contains(touchX, touchY)) {
            isHoveringExit = false;
        }
    }

    @Override
    public void hide() {
        SoundManager.stopMusic(menuMusic); // Stop menu music when leaving the screen
        Gdx.input.setInputProcessor(null); // Remove input processor
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        newGameButton.dispose();
        loadGameButton.dispose();
        exitButton.dispose();
        logo.dispose();
        menuMusic.dispose();
        hoverSound.dispose();
        clickSound.dispose();
        soundOnIcon.dispose();
        soundOffIcon.dispose();
        soundEffectOnIcon.dispose();
        soundEffectOffIcon.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // Handle window resizing if necessary
    }

    @Override
    public void pause() {
        // Handle game pause if necessary
    }

    @Override
    public void resume() {
        // Handle game resume if necessary
    }

    // InputProcessor methods
    @Override
    public boolean keyDown(int keycode) {
        return false; // Not used
    }

    @Override
    public boolean keyUp(int keycode) {
        return false; // Not used
    }

    @Override
    public boolean keyTyped(char character) {
        return false; // Not used
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false; // Not used
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false; // Not used
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false; // Not used
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false; // Not used
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false; // Not used
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false; // Not used
    }
}







