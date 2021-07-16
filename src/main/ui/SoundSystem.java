package main.ui;

import main.model.Sound;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundSystem {

    private final int MAX_T = 4;
    private ArrayList<Sound> soundList;
    private float currentVolume;
    private ExecutorService pool;

    public SoundSystem() {
        init();
    }

    private void init() {
        soundList = new ArrayList<>();
        currentVolume = 0.5f;
        pool = Executors.newFixedThreadPool(MAX_T);
    }

    public boolean playSound(String name) {
        for (Sound s: soundList) {
            if (name.equals(s.getSoundName())) {
                pool.execute(s);
                return true;
            }
        }

        return false;
    }

    public void addToSounds(Sound s) {
        soundList.add(s);
    }

    public void setVolume(float volume) {
        currentVolume = volume;
        for (Sound s: soundList) {
            s.setVolume(currentVolume);
        }
    }

    public void mute() {
        for (Sound s: soundList) {
            s.mute();
        }
    }

    public void unmute() {
        for (Sound s: soundList) {
            s.unMute();
        }
    }

    public void pauseAllSounds() {
        for (Sound s: soundList) {
            s.pause();
        }
    }

    public void resumeFromPause() {
        for (Sound s: soundList) {
            s.start();
        }
    }

    public void resetSounds() {
        for (Sound s: soundList) {
            s.resetSound();
        }
    }
}
