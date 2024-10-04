package com.mygdx.catmario;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class CharacterController {
    private final Vector2 position;
    private final Vector2 velocity;
    private final ArrayList<Bullet> bullets; // List to hold bullets

    private static final float GRAVITY = -500; // Gravity acceleration
    private static final float JUMP_VELOCITY = 400; // Initial jump velocity
    private static final float MOVE_SPEED = 300; // Character move speed
    private static final float GROUND_LEVEL = 150; // Ground level height
    private final float characterHeight; // Add this to get the character height

    private int jumpCount; // Track the number of jumps

    public CharacterController(float startX, float startY, float moveSpeed, float jumpHeight, float characterHeight) {
        position = new Vector2(startX, startY);
        velocity = new Vector2();
        bullets = new ArrayList<>(); // Initialize bullet list
        jumpCount = 0; // Initialize jump count
        this.characterHeight = characterHeight; // Store the character height
    }

    public void update(float delta) {
        // Handle horizontal movement
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            position.x += MOVE_SPEED * delta; // Move right
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            position.x -= MOVE_SPEED * delta; // Move left
        }

        // Handle jumping
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            if (jumpCount < 2) { // Allow jumping if jump count is less than 2
                velocity.y = JUMP_VELOCITY; // Set upward velocity
                jumpCount++; // Increment jump count
            }
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A)) {
            float bulletStartX = position.x + 170; // Start bullet just right of character
            float bulletStartY = position.y + (characterHeight / 2) - 10; // Adjust Y to be at character's height
            bullets.add(new Bullet(bulletStartX, bulletStartY, 80, 20)); // Specify bullet width and height
        }
        

        // Apply gravity
        velocity.y += GRAVITY * delta;
        position.add(0, velocity.y * delta); // Update position based on velocity

        // Check if character is on the ground
        if (position.y <= GROUND_LEVEL) {
            position.y = GROUND_LEVEL; // Reset position to ground level
            jumpCount = 0; // Reset jump count on landing
            velocity.y = 0; // Reset vertical velocity
        }

        // Update bullets
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update(delta); // Update bullet position

            // Remove bullet if it goes off screen
            if (bullet.isOffScreen(Gdx.graphics.getWidth())) {
                iterator.remove(); // Remove bullet from list
            }
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets; // Getter for bullets
    }
}




