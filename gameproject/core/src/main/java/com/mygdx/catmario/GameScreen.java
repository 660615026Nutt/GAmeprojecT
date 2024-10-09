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
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private Texture characterTexture;
    private final Texture backgroundTexture;
    private final Texture groundTexture;
    private final int selectedCharacter;
    private final String characterName;
    private Boss boss;
    private int characterHP = 100;

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
    private final int getCharacterHP = 100;           // HP for each life (max 100)

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

        //spawn Boss at the edge
        boss = new Boss(Gdx.graphics.getWidth() - 100, GROUND_Y + 150);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //check if character has HP or not
        if (characterHP <= 0) {
            game.setScreen(new GameScreen(game, selectedCharacter, characterName)); //just temp page, waiting for GameOver Page
            //game.setScreen(new GameOverScreen()game)); for the real one (For GameOver Page)
            return;
        }

        characterController.update(delta);
        characterPosition.set(characterController.getPosition());

        boss.update(delta);

        batch.begin();

        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(groundTexture, 0, GROUND_Y, Gdx.graphics.getWidth(), GROUND_HEIGHT);
        batch.draw(characterTexture, characterPosition.x, characterPosition.y, characterWidth, characterHeight);

        Iterator<Bullet> iterator = characterController.getBullets().iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update(delta);
            batch.draw(bullet.getTexture(), bullet.getPosition().x, bullet.getPosition().y, bullet.getWidth(), bullet.getHeight());

            if (checkCollision(bullet, boss)) {
                boss.takeDamage(bullet.getDamage());
//                SoundManager.playSoundEffect("bossHit.wav");
                iterator.remove();
            }
        }

        //draw Boss
        if (!boss.isDead()) {
            batch.draw(boss.getTexture(), boss.getPosition().x, boss.getPosition().y, 300, 500); //boss's size

            //Check if it hit or not
            if (boss.isAttacking()) {
                batch.setColor(Color.RED); //change the color when it's hit
            } else {
                batch.setColor(Color.WHITE); // change back to normal color when it's not hit
            }

            //Draw HP's Bar
            float percentHP = (float) boss.getCurrentHP() / (float) boss.getMaxHP(); //calculate HP's %
            float barWidth = 200; //width of HP's bar
            float barHeight = 20; //height og HP's bar
            float barX = boss.getPosition().x; // x position
            float barY = boss.getPosition().y + 520; // y position

            //Background's Bar (Black)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(barX, barY, barWidth, barHeight);

            //HP's Bar (Red)
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(barX, barY, barWidth * percentHP, barHeight);
            shapeRenderer.end();

        }

        //check if the Boss is hit or not
        for (Bullet bullet : characterController.getBullets()) {
            bullet.update(delta);
            batch.draw(bullet.getTexture(), bullet.getPosition().x, bullet.getPosition().y, bullet.getWidth(), bullet.getHeight());

            if (checkCollision(bullet, boss)) {
                boss.takeDamage(bullet.getDamage());
                characterController.removeBullet(bullet);
                break;
            }
        }

        if (boss.getPosition().x < characterPosition.x + characterWidth &&
        boss.getPosition().x + 200 > characterPosition.x) {
            characterHP -= 350; //reduce character in one time
        }

        // Display character status in the top-left corner
        drawCharacterStatus(batch);

        batch.end();

        // Draw border around the status box
        drawStatusBoxBorder();

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            pauseGame(); //ESC to pause the game
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
        font.draw(batch, "Student Name: " + characterName, 130, Gdx.graphics.getHeight() - 50 - statusYOffset);

        // Draw hearts (lives)
        for (int i = 0; i < lives; i++) {
            batch.draw(heartTexture, 130 + i * 30, Gdx.graphics.getHeight() - 120 - statusYOffset, 60, 40);  // Adjust heart size to 60x40
        }

        // Draw HP
        font.draw(batch, "Score : " + characterHP + "/100", 130, Gdx.graphics.getHeight() - 160 - statusYOffset);  // Adjusted Y position
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

    private boolean checkCollision (Bullet bullet, Enemy enemy) {
        //check if the bullet work or not
        return bullet.getPosition().x < enemy.getPosition().x + 100 && // for calculate position and size
        bullet.getPosition().x + bullet.getWidth() > enemy.getPosition().x &&
        bullet.getPosition().y < enemy.getPosition().y + 100 &&
        bullet.getPosition().y + bullet.getHeight() > enemy.getPosition().y;
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
