package main.ui;

import main.model.Sound;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SoundSystem {

    private final int MAX_T = 4;
    private ArrayList<Sound> soundList;
    private float currentVolume;
    private ExecutorService pool;
    private boolean isMuted;

    public SoundSystem() {
        init();
    }

    private void init() {
        soundList = new ArrayList<>();
        currentVolume = 0.5f;
        pool = Executors.newFixedThreadPool(MAX_T);
        isMuted = false;
    }

    public boolean playSound(String name) {
        if (!isMuted) {
            for (Sound s : soundList) {
                if (name.equals(s.getSoundName())) {
                    pool.execute(s);
                    return true;
                }
            }
        }

        return false;
    }

    public void clearAllSounds() {
        soundList = new ArrayList<>();
    }

    public void addToSounds(Sound s) {
        if (!soundList.contains(s)) {
            soundList.add(s);
        }
    }

    public void setVolume(float volume) {
        currentVolume = volume;
        for (Sound s: soundList) {
            s.setVolume(currentVolume);
        }
    }

    public float getCurrentVolume() {
        return currentVolume;
    }

    public void mute() {
        isMuted = true;
        for (Sound s: soundList) {
            s.mute();
        }
        System.out.println("Is now muted!");
    }

    public void unmute() {
        isMuted = false;
        for (Sound s: soundList) {
            s.unMute();
        }
        System.out.println("Is now unmuted!");
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void pauseAllSounds() {
        for (Sound s: soundList) {
            s.pause();
        }
    }

    public void resumeFromPause() {
        for (Sound s: soundList) {
            s.resume();
        }
    }

    public void resetSounds() {
        for (Sound s: soundList) {
            s.resetSound();
        }
    }

    public void resetSound(Sound s) {
        if (soundList.contains(s)) {
            s.resetSound();
        }
    }

    // MODIFIES: this
    // EFFECTS: A method which ensures that the pool and any of its remaining threads is shutdown properly. This would be called at the closing of the App.
    public void closeSoundSystem() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate properly!");
                }
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
