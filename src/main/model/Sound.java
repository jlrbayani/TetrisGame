package main.model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound {

    private String fileRef;
    private String soundName;
    private Clip clip;
    private float volume;

    public Sound(String fileRef, String soundName, float currentVolume) {
        this.fileRef = fileRef;
        this.soundName = soundName;

        initClip();
        setVolume(currentVolume);
    }

    public void initClip() {
        File file = new File(fileRef);
        AudioInputStream audioInputStream;
        this.clip = null;

        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
            try {
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            } catch (LineUnavailableException e) {
                System.out.println("LineUnavailable Exception for sound: " + soundName);
                e.printStackTrace();
            }
        } catch (UnsupportedAudioFileException e) {
            System.out.println("UnsupportedAudioFile Exception Occurred with sound: " + soundName);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception Occurred with file: " + fileRef);
            e.printStackTrace();
        }
    }

    public String getSoundName() {
        return soundName;
    }

    public void setVolume(float volume) {
        if (volume < 0.0f || volume > 1.0f) {
            this.volume = 0.5f;
        } else {
            this.volume = volume;
        }
        changeVolume();
    }

    public void changeVolume() {
        FloatControl masterGainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        masterGainControl.setValue(20f * (float) Math.log10(volume));
    }

    public void mute() {
        BooleanControl booleanControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
        booleanControl.setValue(true);
    }

    public void unMute() {
        BooleanControl booleanControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
        booleanControl.setValue(false);
    }

    public void start() {
        clip.start();
    }

    public void pause() {
        clip.stop();
    }

    public void resetSound() {
        clip.stop();
        clip.drain();
    }
}
