package main.ui;

import main.model.Sound;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// This represents the sound system and controls all of the sound and music related objects in the app. This makes it so that all their volumes can be adjusted in
// a single object. This also implements a thread pool to handle multiple sounds being played at once.
public class SoundSystem {

    private final int MAX_T = 4;
    private ArrayList<Sound> soundList;
    private float currentVolume;
    private ExecutorService pool;
    private boolean isMuted;

    // the constructor for the SoundSystem
    public SoundSystem() {
        init();
    }

    // EFFECTS: initializes the Sound System
    private void init() {
        soundList = new ArrayList<>();
        currentVolume = 0.5f;
        pool = Executors.newFixedThreadPool(MAX_T);
        isMuted = false;
    }

    // EFFECTS: plays a sound based on the name of the sound, it also returns true if the sound plays successfully and false otherwise (if the state of the system is muted)
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

    // MODIFIES: this
    // EFFECTS: clears the sounds currently in soundList
    public void clearAllSounds() {
        soundList = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a Sound object to soundList
    public void addToSounds(Sound s) {
        if (!soundList.contains(s)) {
            soundList.add(s);
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the currentVolume for all the sounds in soundList to the volume amount
    public void setVolume(float volume) {
        currentVolume = volume;
        for (Sound s: soundList) {
            s.setVolume(currentVolume);
        }
    }

    public float getCurrentVolume() {
        return currentVolume;
    }

    // MODIFIES: this
    // EFFECTS: mutes all the sounds
    public void mute() {
        isMuted = true;
        for (Sound s: soundList) {
            s.mute();
        }
        System.out.println("Is now muted!");
    }

    // MODIFIES: this
    // EFFECTS: unmutes all the sounds
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

    // MODIFIES: sounds in soundList
    // EFFECTS: pauses all the sounds regardless of what point in the clip they were currently in
    public void pauseAllSounds() {
        for (Sound s: soundList) {
            s.pause();
        }
    }

    // MODIFIES: sounds in soundList
    // EFFECTS: resumes all sounds in the soundList
    public void resumeFromPause() {
        for (Sound s: soundList) {
            s.resume();
        }
    }

    // MODIFIES: sounds in soundList
    // EFFECTS: resets all sounds in the soundList such that their clips are back at the beginning
    public void resetSounds() {
        for (Sound s: soundList) {
            s.resetSound();
        }
    }

    // MODIFIES: a sound in soundList
    // EFFECTS: resets a specific sound (s) in soundList
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
