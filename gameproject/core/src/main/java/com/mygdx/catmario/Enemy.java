package com.mygdx.catmario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

    private int health; //enemy health
    private boolean isDead;
    private final Vector2 position;
    private final Texture texture;

    public Enemy(float startX, float startY) {
        this.health = 100; //initial HP
        this.isDead = false; //for start game
        this.position = new Vector2(startX, startY);
        this.texture = new Texture("enemy.jpg"); //enemy texture
    }

    //damage from bullet
    public void takeDamage(int damage) {

        if(!isDead) {
            health -= damage;

            if(health <= 0) {
                isDead = true;
            }

        }
    }

    //get the current HP of Enemy
    public int getHealth() {
        return health;
    }

    //Check if it alive or not
    public boolean isAlive() {
        return health > 0;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isDead() {
        return isDead;
    }

    public void dispose () {
        texture.dispose(); //this part if for dispose the source when it's unused
    }

}
