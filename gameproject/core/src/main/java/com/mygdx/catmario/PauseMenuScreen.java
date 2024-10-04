package com.mygdx.catmario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PauseMenuScreen implements Screen {

    private final Main game;
    private final SpriteBatch batch;
    private final Texture backgroundTexture;
    private final Texture resumeButtonTexture;
    private final Texture saveGameButtonTexture;
    private final Texture mainMenuButtonTexture; 
    private final Rectangle resumeButtonBounds;
    private final Rectangle saveGameButtonBounds;
    private final Rectangle mainMenuButtonBounds; 
    private final int selectedCharacter;
    private final String characterName;  // Add characterName
        
    // Add sound effects for hover and click
    private final Sound hoverSound;
    private final Sound clickSound;

    private boolean isButtonPressed = false;

    public PauseMenuScreen(Main game, int selectedCharacter, String characterName) {
        this.game = game;
        this.batch = game.batch;
        this.selectedCharacter = selectedCharacter;
        this.characterName = characterName;  // Store the characterName

        backgroundTexture = new Texture("pausebackground.png");
        resumeButtonTexture = new Texture("resume.png");
        saveGameButtonTexture = new Texture("savegame.png");
        mainMenuButtonTexture = new Texture("mainmenu.png");

        float buttonWidth = 350;
        float buttonHeight = 350;
        float centerX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float centerYResume = 650;
        float centerYSave = centerYResume - 300;
        float centerYMainMenu = centerYSave - 300;

        resumeButtonBounds = new Rectangle(centerX, centerYResume, buttonWidth, buttonHeight);
        saveGameButtonBounds = new Rectangle(centerX, centerYSave, buttonWidth, buttonHeight);
        mainMenuButtonBounds = new Rectangle(centerX, centerYMainMenu, buttonWidth, buttonHeight);

        // Load sounds
        hoverSound = Gdx.audio.newSound(Gdx.files.internal("hover.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.wav"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(resumeButtonTexture, resumeButtonBounds.x, resumeButtonBounds.y, resumeButtonBounds.width, resumeButtonBounds.height);
        batch.draw(saveGameButtonTexture, saveGameButtonBounds.x, saveGameButtonBounds.y, saveGameButtonBounds.width, saveGameButtonBounds.height);
        batch.draw(mainMenuButtonTexture, mainMenuButtonBounds.x, mainMenuButtonBounds.y, mainMenuButtonBounds.width, mainMenuButtonBounds.height);
        batch.end();

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.isTouched() && !isButtonPressed) {
            if (resumeButtonBounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);
                game.setScreen(new GameScreen(game, selectedCharacter, characterName));  // Pass characterName
            } else if (saveGameButtonBounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);
                game.setScreen(new SaveScreen(game, selectedCharacter, characterName));  // Pass characterName
            } else if (mainMenuButtonBounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);
                game.setScreen(new MainMenuScreen(game));
            }
            isButtonPressed = true;
        } else if (!Gdx.input.isTouched()) {
            isButtonPressed = false;
        }
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        resumeButtonTexture.dispose();
        saveGameButtonTexture.dispose();
        mainMenuButtonTexture.dispose();
        hoverSound.dispose();
        clickSound.dispose();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void show() {}
}


