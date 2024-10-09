package com.mygdx.catmario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Boss extends Enemy {
    private final float speed = 75; //normally speed of Boss
    private final int maxHP = 750;
    private int currentHP;
    private Texture normalTexture; //Normally Boss's Animation
    private Texture attackTexture; //Attack's Animation
    private boolean isAttacking = false; //show if it attacked or not
    private float hitTimer = 0; //time for Red effect
    private static final float attacked_duration = 0.2f; //time to red effect

    public Boss(float startX, float startY) {
        super(startX, startY);
        this.currentHP = maxHP;
        this.normalTexture = new Texture("bossAttackF.png");
        this.attackTexture = new Texture("bossAttackF.png");
        this.setTexture(normalTexture);
    }

    @Override
    public void update(float delta) {
        if (!isDead()) {
            this.getPosition().x -= speed * delta; //running to poor student
//            if (isAttacking) {
//                setTexture(attackTexture);
//            } else {
//                setTexture(normalTexture);
//            }
            if (isAttacking) {
                hitTimer -= delta;
                if (hitTimer <= 0) {
                    isAttacking = false;
                }
            }
        }
    }

    public void takeDamage(int damage) {
        currentHP -= damage;
        if (currentHP <= 0) {
            setDead(true);
        }

        isAttacking = true;
        hitTimer = attacked_duration; //start Timing
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

   public boolean isAttacking() {
        return isAttacking;
   }

    public void attack() {
        isAttacking = true; //start attacking
    }

    public void stopAttack() {
        isAttacking = false; //for stopping attack
    }

    @Override
    public void dispose() {
        super.dispose();
        normalTexture.dispose();
        attackTexture.dispose();
    }

}
