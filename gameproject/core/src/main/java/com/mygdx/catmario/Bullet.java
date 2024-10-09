package com.mygdx.catmario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private final Vector2 position;
    private final Vector2 velocity;
    private final Texture texture;
    private final float speed = 500; // Bullet speed
    private final float width ; // Bullet width
    private final float height; // Bullet height
    private final int damage; //Bullet damage

    public Bullet(float startX, float startY, float width, float height, int damage) {
        position = new Vector2(startX, startY);
        velocity = new Vector2(speed, 0); // Move right
        texture = new Texture("bullet.png"); // Load bullet texture
        this.width = width; // Set the bullet width
        this.height = height; // Set the bullet height
        this.damage = damage; //set damage value
    }

    public void update(float delta) {
        position.add(velocity.cpy().scl(delta)); // Update position based on speed
    }

    public Vector2 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getDamage() {return damage;} //get bullet damage

    public boolean isOffScreen(float screenWidth) {
        return position.x > screenWidth; // Check if bullet is off the screen
    }

    public void dispose() {
        texture.dispose(); // Dispose of the bullet texture
    }

    // New method to get bullet dimensions
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}

