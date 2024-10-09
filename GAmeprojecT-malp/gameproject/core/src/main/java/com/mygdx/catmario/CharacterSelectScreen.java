package com.mygdx.catmario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class CharacterSelectScreen implements Screen {

    private final Main game;
    private final SpriteBatch batch;
    private final Texture backgroundTexture;
    private final Texture character1;
    private final Texture character2;
    private final Texture selectButton;
    private final Rectangle character1Bounds;
    private final Rectangle character2Bounds;
    private final Rectangle selectButtonBounds;
    private final ShapeRenderer shapeRenderer;

    private boolean isHoveringCharacter1 = false;
    private boolean isHoveringCharacter2 = false;
    private boolean isHoveringSelect = false;

    // Sound effects
    private final Sound hoverSound;
    private final Sound clickSound;

    // Font for text input
    private final BitmapFont font;

    // Store the name entered by the player
    private final StringBuilder characterName = new StringBuilder();

    // Define a rectangle for the text input box
    private final Rectangle textBoxBounds;

    // For confirmation
    private boolean isEnteringName = false;

    // Selected character variable
    private int selectedCharacter = 0;  // 1 for character1, 2 for character2

    public CharacterSelectScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Load background and character images
        backgroundTexture = new Texture("characterselect.png");
        character1 = new Texture("character1.png");
        character2 = new Texture("character2.png");
        selectButton = new Texture("select.png");

        // Load sounds for hover and click
        hoverSound = Gdx.audio.newSound(Gdx.files.internal("hover.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click.wav"));

        // Set positions and sizes for characters
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        int characterWidth = 800;
        int characterHeight = 600;

        // Set character positions
        character1Bounds = new Rectangle(screenWidth / 4 - characterWidth / 2, screenHeight / 2 - characterHeight / 2, characterWidth, characterHeight);
        character2Bounds = new Rectangle(3 * screenWidth / 4 - characterWidth / 2, screenHeight / 2 - characterHeight / 2, characterWidth, characterHeight);

        // Set select button bounds
        float selectButtonWidth = 300;
        float selectButtonHeight = 300;
        float selectButtonY = screenHeight / 7;
        selectButtonBounds = new Rectangle(screenWidth / 2 - selectButtonWidth / 2, selectButtonY - selectButtonHeight / 2, selectButtonWidth, selectButtonHeight);

        // Define the text box bounds
        float textBoxWidth = 500;
        float textBoxHeight = 100;
        float textBoxY = screenHeight / 2 - textBoxHeight / 2;
        textBoxBounds = new Rectangle(screenWidth / 2 - textBoxWidth / 2, textBoxY, textBoxWidth, textBoxHeight);

        // Initialize ShapeRenderer and font
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        
        // Render the background
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!isEnteringName) {
            // Render characters and select button when not entering name
            batch.draw(character1, character1Bounds.x, character1Bounds.y, character1Bounds.width, character1Bounds.height);
            batch.draw(character2, character2Bounds.x, character2Bounds.y, character2Bounds.width, character2Bounds.height);
            batch.draw(selectButton, selectButtonBounds.x, selectButtonBounds.y, selectButtonBounds.width, selectButtonBounds.height);
        } else {
            // Dim the background when entering name
            Color originalColor = batch.getColor();
            batch.setColor(originalColor.r, originalColor.g, originalColor.b, 0.5f);  // Reduce alpha to dim background
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(originalColor);  // Reset to original color
        }

        batch.end();

        // Draw selection border around the selected character (but hide when entering name)
        if (selectedCharacter != 0 && !isEnteringName) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);  // Set the border color to red for selected character

            if (selectedCharacter == 1) {
                shapeRenderer.rect(character1Bounds.x - 10, character1Bounds.y - 10, character1Bounds.width + 20, character1Bounds.height + 20);
            } else if (selectedCharacter == 2) {
                shapeRenderer.rect(character2Bounds.x - 10, character2Bounds.y - 10, character2Bounds.width + 20, character2Bounds.height + 20);
            }

            shapeRenderer.end();
        }

        // Draw the name input box when entering name
        if (isEnteringName) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(textBoxBounds.x, textBoxBounds.y, textBoxBounds.width, textBoxBounds.height);
            shapeRenderer.end();

            // Draw the name input text inside the box
            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(2.5f);
            font.draw(batch, characterName.toString(), textBoxBounds.x + 20, textBoxBounds.y + textBoxBounds.height - 20);  // Show entered name
            batch.end();

            // Handle name input
            handleNameInput();
        } else {
            // Handle character selection and hover
            handleHoverAndInput();
        }
    }

    private void handleHoverAndInput() {
        // Get the coordinates of the touch and adjust for camera's Y-axis
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Handle hover sound effects
        handleHoverSound(touchX, touchY);

        // Handle character selection and select button click
        if (Gdx.input.justTouched()) {
            if (character1Bounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);  // Play click sound for character 1
                selectedCharacter = 1;
            } else if (character2Bounds.contains(touchX, touchY)) {
                SoundManager.playSound(clickSound);  // Play click sound for character 2
                selectedCharacter = 2;
            } else if (selectButtonBounds.contains(touchX, touchY) && selectedCharacter != 0) {
                SoundManager.playSound(clickSound);  // Play click sound for the select button
                isEnteringName = true;  // Proceed to name input, hide character selection
            }
        }
    }

    private void handleNameInput() {
        // Handle keyboard input for entering character name
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && characterName.length() > 0) {
            characterName.deleteCharAt(characterName.length() - 1);  // Remove the last character
        } else {
            char typedChar = getTypedCharacter();  // Get the typed character
            if (typedChar != '\0' && Character.isLetterOrDigit(typedChar) && characterName.length() < 20) {  // Limit to 20 characters
                characterName.append(typedChar);  // Append typed character to the name
            }
        }

        // Confirm name when Enter is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && characterName.length() > 0) {
            // Reset alpha before entering the game screen
            batch.setColor(1, 1, 1, 1);  // Reset alpha to 1 (fully opaque)
            
            // Proceed to game
            game.setScreen(new GameScreen(game, selectedCharacter, characterName.toString()));  // Start game with selected character and name
        }
    }

    // Method to get the typed character
    private char getTypedCharacter() {
        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                return (char) (i - Input.Keys.A + 'A');  // Return uppercase character
            }
        }
        for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                return (char) (i - Input.Keys.NUM_0 + '0');  // Return number character
            }
        }
        return '\0';  // Return null character if no valid input
    }

    private void handleHoverSound(float touchX, float touchY) {
        // Check hover over character 1
        if (character1Bounds.contains(touchX, touchY)) {
            if (!isHoveringCharacter1) {
                SoundManager.playSound(hoverSound);  // Play hover sound for character 1
                isHoveringCharacter1 = true;
            }
        } else {
            isHoveringCharacter1 = false;
        }

        // Check hover over character 2
        if (character2Bounds.contains(touchX, touchY)) {
            if (!isHoveringCharacter2) {
                SoundManager.playSound(hoverSound);  // Play hover sound for character 2
                isHoveringCharacter2 = true;
            }
        } else {
            isHoveringCharacter2 = false;
        }

        // Check hover over select button
        if (selectButtonBounds.contains(touchX, touchY)) {
            if (!isHoveringSelect) {
                SoundManager.playSound(hoverSound);  // Play hover sound for select button
                isHoveringSelect = true;
            }
        } else {
            isHoveringSelect = false;
        }
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
        character1.dispose();
        character2.dispose();
        selectButton.dispose();
        hoverSound.dispose();  // Dispose hover sound
        clickSound.dispose();  // Dispose click sound
        shapeRenderer.dispose();
        font.dispose();
    }
}

























