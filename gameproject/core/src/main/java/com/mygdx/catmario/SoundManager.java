package com.mygdx.catmario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private static boolean musicEnabled;
    private static boolean soundEffectsEnabled;

    private final static Preferences preferences;

    static {
        preferences = Gdx.app.getPreferences("SoundSettings");
        // Load settings from preferences
        musicEnabled = preferences.getBoolean("musicEnabled", true);
        soundEffectsEnabled = preferences.getBoolean("soundEffectsEnabled", true);
    }

    public static void toggleMusic() {
        musicEnabled = !musicEnabled;
        preferences.putBoolean("musicEnabled", musicEnabled);
        preferences.flush();
    }

    public static void toggleSoundEffects() {
        soundEffectsEnabled = !soundEffectsEnabled;
        preferences.putBoolean("soundEffectsEnabled", soundEffectsEnabled);
        preferences.flush();
    }

    public static void toggleAllSound() {
        boolean newState = !(musicEnabled || soundEffectsEnabled);
        musicEnabled = newState;
        soundEffectsEnabled = newState;
        preferences.putBoolean("musicEnabled", musicEnabled);
        preferences.putBoolean("soundEffectsEnabled", soundEffectsEnabled);
        preferences.flush();
    }

    public static boolean isMusicEnabled() {
        return musicEnabled;
    }

    public static boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }

    public static void playSound(Sound sound) {
        if (soundEffectsEnabled) {
            sound.play();
        }
    }

    public static void playMusic(Music music) {
        if (musicEnabled) {
            if (!music.isPlaying()) {
                music.play();
            }
        } else {
            music.pause();
        }
    }

    public static void stopMusic(Music music) {
        if (music.isPlaying()) {
            music.stop();
        }
    }
}




