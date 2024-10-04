package com.mygdx.catmario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private Texture characterTexture;
    private final Texture backgroundTexture;
    private final Texture groundTexture;
    private final int selectedCharacter;
    private final String characterName;

    // Character size and position
    private float characterWidth;
    private float characterHeight;
    private final Vector2 characterPosition;

    // Ground properties
    private static final float GROUND_Y = 0;
    private static final float GROUND_HEIGHT = 300;

    // Music for background
    private final Music backgroundMusic;

    // Character controller
    private final CharacterController characterController;

    // Add status elements
    private final BitmapFont font;  // Font for drawing text
    private final Texture heartTexture;   // Texture for hearts
    private Texture characterFaceTexture;  // Texture for character face
    private final int lives = 3;          // Number of lives (3 hearts)
    private final int hp = 100;           // HP for each life (max 100)

    // Add background block for status
    private final Texture statusBackgroundTexture;

    // Add ShapeRenderer for drawing the border
    private final ShapeRenderer shapeRenderer;

    public GameScreen(Main game, int selectedCharacter, String characterName) {
        this.game = game;
        this.batch = game.batch;
        this.selectedCharacter = selectedCharacter;
        this.characterName = characterName;

        if (selectedCharacter == 1) {
            characterTexture = new Texture("character1.png");
            characterFaceTexture = new Texture("character1face.png");  // Add face texture
            characterWidth = 300;
            characterHeight = 150;
        } else if (selectedCharacter == 2) {
            characterTexture = new Texture("character2.png");
            characterFaceTexture = new Texture("character2face.png");  // Add face texture
            characterWidth = 300;
            characterHeight = 150;
        }

        backgroundTexture = new Texture("gamebackground.png");
        groundTexture = new Texture("ground.png");

        // Initialize font, heart texture, and shape renderer
        font = new BitmapFont();
        heartTexture = new Texture("heart.png");  // Add heart texture
        shapeRenderer = new ShapeRenderer();      // Initialize ShapeRenderer

        characterPosition = new Vector2(100, GROUND_Y + 150);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("gamemusic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);

        characterController = new CharacterController(100, GROUND_Y + 150, 200, 300, characterHeight);

        // Load the background texture for the status box
        statusBackgroundTexture = new Texture("statusbackground.png");

        SoundManager.playMusic(backgroundMusic);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        characterController.update(delta);
        characterPosition.set(characterController.getPosition());

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(groundTexture, 0, GROUND_Y, Gdx.graphics.getWidth(), GROUND_HEIGHT);
        batch.draw(characterTexture, characterPosition.x, characterPosition.y, characterWidth, characterHeight);

        for (Bullet bullet : characterController.getBullets()) {
            batch.draw(bullet.getTexture(), bullet.getPosition().x, bullet.getPosition().y, bullet.getWidth(), bullet.getHeight());
        }

        // Display character status on the top-left corner
        drawCharacterStatus(batch);

        batch.end();

        // Draw border around the status box
        drawStatusBoxBorder();

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            pauseGame();
        }
    }

    private void drawCharacterStatus(SpriteBatch batch) {
        // Adjusted Y positions for the status box to be slightly lower
        float statusYOffset = 50;  // Lower the status box
        float statusBoxWidth = 400;
        float statusBoxHeight = 200;

        // Draw background block for status
        batch.draw(statusBackgroundTexture, 10, Gdx.graphics.getHeight() - statusBoxHeight - 60, statusBoxWidth, statusBoxHeight);  // Position and size of background

        // Draw character face at the top-left corner inside the status block
        batch.draw(characterFaceTexture, 30, Gdx.graphics.getHeight() - 120 - statusYOffset, 80, 80);  // Adjusted size to 80x80

        // Draw character name
        font.getData().setScale(1.5f);  // Adjusted scale for better size
        font.setColor(Color.BLACK);  // Set font color to black
        font.draw(batch, "Name: " + characterName, 130, Gdx.graphics.getHeight() - 50 - statusYOffset);

        // Draw hearts (lives)
        for (int i = 0; i < lives; i++) {
            batch.draw(heartTexture, 130 + i * 30, Gdx.graphics.getHeight() - 120 - statusYOffset, 60, 40);  // Adjust heart size to 60x40
        }

        // Draw HP
        font.draw(batch, "HP: " + hp + "/100", 130, Gdx.graphics.getHeight() - 160 - statusYOffset);  // Adjusted Y position
    }

    private void drawStatusBoxBorder() {
        // Use ShapeRenderer to draw a border around the status box
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);  // Set border color to black
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 237, 400, 205);  // Coordinates and size of the box
        shapeRenderer.end();
    }

    private void pauseGame() {
        SoundManager.stopMusic(backgroundMusic);
        game.setScreen(new PauseMenuScreen(game, selectedCharacter, characterName));
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {
        SoundManager.playMusic(backgroundMusic);
    }

    @Override
    public void hide() {
        SoundManager.stopMusic(backgroundMusic);
    }

    @Override
    public void pause() {
        SoundManager.stopMusic(backgroundMusic);
    }

    @Override
    public void resume() {
        SoundManager.playMusic(backgroundMusic);
    }

    @Override
    public void dispose() {
        characterTexture.dispose();
        backgroundTexture.dispose();
        groundTexture.dispose();
        heartTexture.dispose();
        characterFaceTexture.dispose();
        statusBackgroundTexture.dispose();  // Dispose status background texture
        backgroundMusic.dispose();
        font.dispose();
        shapeRenderer.dispose();  // Dispose ShapeRenderer
    }
}

























