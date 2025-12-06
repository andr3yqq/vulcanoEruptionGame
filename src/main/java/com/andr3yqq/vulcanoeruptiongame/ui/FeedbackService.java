package com.andr3yqq.vulcanoeruptiongame.ui;

import javafx.animation.FadeTransition;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.function.Consumer;

public class FeedbackService {
    private static final double FLASH_ALPHA = 0.45;
    private static final int FLASH_MS = 450;
    private static final int MAX_LOG_ITEMS = 120;

    private final Rectangle overlay;
    private final AudioClip lavaClip;
    private final AudioClip saveClip;
    private final AudioClip deathClip;
    private FadeTransition flashAnimation;

    public FeedbackService(Rectangle overlay, AudioClip lavaClip, AudioClip saveClip, AudioClip deathClip, Consumer<String> logger) {
        this.overlay = overlay;
        this.lavaClip = lavaClip;
        this.saveClip = saveClip;
        this.deathClip = deathClip;
    }

    public void flash(Color color) {
        if (overlay == null) return;
        if (flashAnimation != null) {
            flashAnimation.stop();
        }
        overlay.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), FLASH_ALPHA));
        flashAnimation = new FadeTransition(Duration.millis(FLASH_MS), overlay);
        flashAnimation.setFromValue(FLASH_ALPHA);
        flashAnimation.setToValue(0);
        flashAnimation.play();
    }

    public void playLava() { play(lavaClip); }
    public void playSave() { play(saveClip); }
    public void playDeath() { play(deathClip); }

    private void play(AudioClip clip) {
        if (clip != null) {
            clip.play();
        }
    }

    public static int maxLogItems() { return MAX_LOG_ITEMS; }
}
