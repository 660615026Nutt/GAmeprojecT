package com.mygdx.catmario;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SaveScreen implements Screen {

    private final Main game;
    private final SpriteBatch batch;
    private final Texture backgroundTexture;
    private final Texture saveConfirmButtonTexture;
    private final Texture cancelButtonTexture;
    private final Rectangle saveConfirmButtonBounds;
    private final Rectangle cancelButtonBounds;
    private final Rectangle blockBounds;
    private final Texture blockTexture;
    private final BitmapFont font;
    private final int selectedCharacter;
    private final String characterName;

    // Sound effects for hover and click
    private final Sound hoverSound;
    private final Sound clickSound;

    // Hover status flags
    private boolean isHoveringSaveConfirm = false;
    private boolean isHoveringCancel = false;

    public SaveScreen(Main game, int selectedCharacter, String characterName) {
        this.game = game;
        this.batch = game.batch;
        this.selectedCharacter = selectedCharacter;
        this.characterName = characterName;

        backgroundTexture = new Texture("savebackground.png");
        saveConfirmButtonTexture = new Texture("confirm.png");
        cancelButtonTexture = new Texture("cancel.png");
        blockTexture = new Texture("block.png");

        hoverSound = Gdx.audio.newSound(Gdx.files.internal("hover.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.wav"));

        float centerX = Gdx.graphics.getWidth() / 2;
        float centerY = Gdx.graphics.getHeight() / 2;

        float blockWidth = 800;
        float blockHeight = 500;
        blockBounds = new Rectangle(centerX - blockWidth / 2, centerY - blockHeight / 2, blockWidth, blockHeight);

        float buttonY = centerY - 300;
        float confirmButtonX = centerX - 350;
        float cancelButtonX = confirmButtonX + 300 + 100;

        saveConfirmButtonBounds = new Rectangle(confirmButtonX, buttonY, 300, 300);
        cancelButtonBounds = new Rectangle(cancelButtonX, buttonY, 300, 300);

        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(blockTexture, blockBounds.x, blockBounds.y, blockBounds.width, blockBounds.height);
        batch.draw(saveConfirmButtonTexture, saveConfirmButtonBounds.x, saveConfirmButtonBounds.y, saveConfirmButtonBounds.width, saveConfirmButtonBounds.height);
        batch.draw(cancelButtonTexture, cancelButtonBounds.x, cancelButtonBounds.y, cancelButtonBounds.width, cancelButtonBounds.height);

        font.setColor(Color.BLACK);
        font.getData().setScale(3f);
        font.draw(batch, "Do you want to save game?", blockBounds.x + 155, blockBounds.y + blockBounds.height - 185);

        font.setColor(Color.WHITE);
        font.draw(batch, "Do you want to savegame?", blockBounds.x + 140, blockBounds.y + blockBounds.height - 200);

        batch.end();

        handleHoverAndInput();
    }

    private void handleHoverAndInput() {
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Handle hover for Save Confirm button
        if (saveConfirmButtonBounds.contains(touchX, touchY)) {
            if (!isHoveringSaveConfirm) {
                SoundManager.playSound(hoverSound);  // Play hover sound when first hovering
                isHoveringSaveConfirm = true;
            }
        } else {
            isHoveringSaveConfirm = false;  // Reset hover state when not hovering
        }

        // Handle hover for Cancel button
        if (cancelButtonBounds.contains(touchX, touchY)) {
            if (!isHoveringCancel) {
                SoundManager.playSound(hoverSound);  // Play hover sound when first hovering
                isHoveringCancel = true;
            }
        } else {
            isHoveringCancel = false;  // Reset hover state when not hovering
        }

        // Handle button click events
        if (Gdx.input.isTouched()) {
            if (saveConfirmButtonBounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);
                saveGame();  // Save the game data
                game.setScreen(new PauseMenuScreen(game, selectedCharacter, characterName));
            } else if (cancelButtonBounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);
                game.setScreen(new PauseMenuScreen(game, selectedCharacter, characterName));
            }
        }
    }

    // Method to save the game data with a unique ID
    private void saveGame() {
        Preferences prefs = Gdx.app.getPreferences("SaveGame");

        // Generate a unique save ID (timestamp)
        long saveID = System.currentTimeMillis();

        // Save character and other game states with the unique ID
        prefs.putInteger("selectedCharacter_" + saveID, selectedCharacter);
        prefs.putString("characterName_" + saveID, characterName);
        prefs.putFloat("characterPositionX_" + saveID, game.getCurrentCharacterX());
        prefs.putFloat("characterPositionY_" + saveID, game.getCurrentCharacterY());

        // Save time in Thailand timezone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String saveTime = dateFormat.format(calendar.getTime());
        prefs.putString("saveTime_" + saveID, saveTime);

        // Keep track of all save IDs
        String allSaves = prefs.getString("allSaves", "");
        allSaves += saveID + ";";  // Add new save ID
        prefs.putString("allSaves", allSaves);

        prefs.flush();  // Save all preferences to disk
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

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        saveConfirmButtonTexture.dispose();
        cancelButtonTexture.dispose();
        blockTexture.dispose();
        font.dispose();
        hoverSound.dispose();
        clickSound.dispose();
    }
}




