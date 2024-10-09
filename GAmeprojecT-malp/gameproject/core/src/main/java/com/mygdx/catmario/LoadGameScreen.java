package com.mygdx.catmario;

import java.util.ArrayList;
import java.util.List;

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

public class LoadGameScreen implements Screen {

    private final SpriteBatch batch;
    private final Texture backgroundTexture;
    private final Texture chooseButton;
    private final Texture backButton;
    private final Sound hoverSound;
    private final Sound clickSound;
    private final BitmapFont font;
    
    private List<String> saveSlots;
    private final List<Rectangle> saveSlotBounds;
    
    private final Rectangle chooseBounds;
    private final Rectangle backBounds;
    private final Main game;
    
    private int selectedSaveIndex = -1; // Default: no save selected
    
    // เพิ่มการประกาศตัวแปร hover status
    private boolean isHoveringChoose = false;
    private boolean isHoveringBack = false;
    private int hoverSaveSlotIndex = -1; // เก็บสถานะ hover สำหรับ slot save

    public LoadGameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        backgroundTexture = new Texture("loadbackground.png");
        hoverSound = Gdx.audio.newSound(Gdx.files.internal("hover.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.wav"));
        font = new BitmapFont();

        chooseButton = new Texture("choose.png");
        backButton = new Texture("back.png");

        float buttonWidth = 350;
        float buttonHeight = 350;
        float buttonY = Gdx.graphics.getHeight() - 1150;
        float spacing = 400;

        chooseBounds = new Rectangle((Gdx.graphics.getWidth() / 2f) - buttonWidth - spacing / 2f, buttonY, buttonWidth, buttonHeight);
        backBounds = new Rectangle((Gdx.graphics.getWidth() / 2f) + spacing / 2f, buttonY, buttonWidth, buttonHeight);

        // Load saved game data from Preferences
        loadSavedGameData();

        saveSlotBounds = new ArrayList<>();
        for (int i = 0; i < saveSlots.size(); i++) {
            float y = Gdx.graphics.getHeight() - 100 - i * 100;
            saveSlotBounds.add(new Rectangle(100, y, Gdx.graphics.getWidth() - 200, 80));
        }
    }

    private void loadSavedGameData() {
        Preferences prefs = Gdx.app.getPreferences("SaveGame");
        String allSaves = prefs.getString("allSaves", "");
        saveSlots = new ArrayList<>();

        if (!allSaves.isEmpty()) {
            String[] saveIDs = allSaves.split(";");
            for (String saveID : saveIDs) {
                if (!saveID.isEmpty()) {
                    String characterName = prefs.getString("characterName_" + saveID, "Unknown");
                    String saveTime = prefs.getString("saveTime_" + saveID, "Unknown");
                    saveSlots.add("Character: " + characterName + " | Time: " + saveTime);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw buttons first
        batch.draw(chooseButton, chooseBounds.x, chooseBounds.y, chooseBounds.width, chooseBounds.height);
        batch.draw(backButton, backBounds.x, backBounds.y, backBounds.width, backBounds.height);

        // Draw save slots and highlight after the text
        for (int i = 0; i < saveSlots.size(); i++) {
            Rectangle bounds = saveSlotBounds.get(i);

            // Draw highlight if selected
            if (i == selectedSaveIndex) {
                batch.setColor(Color.YELLOW);  // Highlight selected save
                batch.draw(new Texture("highlight.png"), bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 10);
                batch.setColor(Color.WHITE);
            }

            // Draw save slot text
            font.setColor(Color.BLACK);
            font.getData().setScale(2.5f);
            font.draw(batch, saveSlots.get(i), bounds.x + 20, bounds.y + bounds.height - 20);
        }

        batch.end();

        handleHoverAndInput();
    }

    private void handleHoverAndInput() {
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Handle hover and click for save slots
        boolean hoverFound = false;
        for (int i = 0; i < saveSlotBounds.size(); i++) {
            Rectangle bounds = saveSlotBounds.get(i);
            if (bounds.contains(touchX, touchY)) {
                hoverFound = true;
                if (hoverSaveSlotIndex != i) {
                    SoundManager.playSound(hoverSound); // Play hover sound when first hover
                    hoverSaveSlotIndex = i;
                }
                if (Gdx.input.justTouched()) {
                    selectedSaveIndex = i;
                    SoundManager.playSound(clickSound);
                }
            }
        }
        if (!hoverFound) {
            hoverSaveSlotIndex = -1; // Reset hover state if no slot is hovered
        }

        // Handle hover and click for choose button
        if (chooseBounds.contains(touchX, touchY)) {
            if (!isHoveringChoose) {
                SoundManager.playSound(hoverSound);
                isHoveringChoose = true;
            }
            if (Gdx.input.justTouched() && selectedSaveIndex != -1) {
                SoundManager.playSound(clickSound);
                loadGame(selectedSaveIndex);  // Load selected save
            }
        } else {
            isHoveringChoose = false;
        }

        // Handle hover and click for back button
        if (backBounds.contains(touchX, touchY)) {
            if (!isHoveringBack) {
                SoundManager.playSound(hoverSound);
                isHoveringBack = true;
            }
            if (Gdx.input.justTouched()) {
                SoundManager.playSound(clickSound);
                game.setScreen(new MainMenuScreen(game));  // Go back to main menu
            }
        } else {
            isHoveringBack = false;
        }
    }

    private void loadGame(int index) {
        Preferences prefs = Gdx.app.getPreferences("SaveGame");
        String saveID = prefs.getString("allSaves", "").split(";")[index];
        
        int selectedCharacter = prefs.getInteger("selectedCharacter_" + saveID, 1);
        float characterPositionX = prefs.getFloat("characterPositionX_" + saveID, 100);
        float characterPositionY = prefs.getFloat("characterPositionY_" + saveID, 150);
        
        game.setCurrentCharacter(selectedCharacter);
        game.setCurrentCharacterPosition(characterPositionX, characterPositionY);
        
        // Load the game
        game.setScreen(new GameScreen(game, selectedCharacter, prefs.getString("characterName_" + saveID)));
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
        chooseButton.dispose();
        backButton.dispose();
        hoverSound.dispose();
        clickSound.dispose();
        font.dispose();
    }
}














