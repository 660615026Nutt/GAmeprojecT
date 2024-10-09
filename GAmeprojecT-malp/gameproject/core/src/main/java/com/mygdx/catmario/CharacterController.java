package com.mygdx.catmario;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class CharacterController {
    private final Vector2 position;
    private final Vector2 velocity;
    private final ArrayList<Bullet> bullets;

    private static final float GRAVITY = -500;
    private static final float JUMP_VELOCITY = 400;
    private static final float MOVE_SPEED = 300;
    private static final float GROUND_LEVEL = 150;
    private final float characterHeight;

    private int jumpCount;

    // Animation variables
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> currentAnimation;

    private float stateTime; // Used to track animation time

    public CharacterController(float startX, float startY, float moveSpeed, float jumpHeight, float characterHeight) {
        position = new Vector2(startX, startY);
        velocity = new Vector2();
        bullets = new ArrayList<>();
        jumpCount = 0;
        this.characterHeight = characterHeight;

        // Load animations
        idleAnimation = loadAnimation("IDLEspritesheet.png", 3, 2, 0.1f); // Idle animation
        walkAnimation = loadAnimation("WALKspritesheet.png", 3, 2, 0.1f); // Walk animation
        jumpAnimation = loadAnimation("JUMPspritesheet.png", 3, 2, 0.1f); // Jump animation
        attackAnimation = loadAnimation("ATTACKspritesheet.png", 3, 2, 0.1f); // Attack animation

        currentAnimation = idleAnimation; // Default to idle animation
        stateTime = 0f;
    }

    // Helper function to load animations from a sprite sheet
    private Animation<TextureRegion> loadAnimation(String filePath, int cols, int rows, float frameDuration) {
        Texture sheet = new Texture(filePath);
        TextureRegion[][] tmpFrames = TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight() / rows);
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames.add(tmpFrames[i][j]);
            }
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        // Update animation time
        stateTime += delta;

        // Handle horizontal movement and set animation to walking
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            position.x += MOVE_SPEED * delta; // Move right
            currentAnimation = walkAnimation; // Switch to walk animation
        } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            position.x -= MOVE_SPEED * delta; // Move left
            currentAnimation = walkAnimation; // Switch to walk animation
        } else {
            currentAnimation = idleAnimation; // Switch back to idle when not moving
        }

        // Handle jumping
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            if (jumpCount < 2) { // Allow double jump
                velocity.y = JUMP_VELOCITY;
                jumpCount++;
                currentAnimation = jumpAnimation; // Switch to jump animation
            }
        }

        // Handle attacking
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A)) {
            currentAnimation = attackAnimation; // Switch to attack animation
            float bulletStartX = position.x + 170; // Bullet starts right of the character
            float bulletStartY = position.y + (characterHeight / 2) - 10; // Adjust Y to match character's height
            bullets.add(new Bullet(bulletStartX, bulletStartY, 80, 20)); // Add new bullet
        }

        // Apply gravity
        velocity.y += GRAVITY * delta;
        position.add(0, velocity.y * delta); // Update position based on velocity

        // Check if the character is on the ground
        if (position.y <= GROUND_LEVEL) {
            position.y = GROUND_LEVEL; // Reset to ground level
            jumpCount = 0; // Reset jump count upon landing
            velocity.y = 0; // Reset vertical velocity
        }

        // Update bullets
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update(delta); // Update bullet position

            // Remove bullet if it goes off-screen
            if (bullet.isOffScreen(Gdx.graphics.getWidth())) {
                iterator.remove(); // Remove bullet from the list
            }
        }
    }

    public void render(SpriteBatch batch) {
        // Get the current frame of the animation
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        // Draw the current animation frame
        batch.draw(currentFrame, position.x, position.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
}
