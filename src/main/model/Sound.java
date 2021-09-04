package main.model;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Sound implements Runnable{

    private String fileRef;
    private String soundName;
    private Clip clip;
    private float volume;
    private long clipStopSec;
    private boolean keepLooping;

    public Sound(String fileRef, String soundName, float currentVolume) {
        this.fileRef = fileRef;
        this.soundName = soundName;
        this.keepLooping = false;
        clipStopSec = 0;

        initClip();
        setVolume(currentVolume);
    }

    public void initClip() {
        URL url = Sound.class.getResource(fileRef);
        AudioInputStream audioInputStream;
        this.clip = null;

        try {
            audioInputStream = AudioSystem.getAudioInputStream(url);
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

    public void setKeepLooping(boolean keepLooping) {
        this.keepLooping = keepLooping;
    }


    public String getSoundName() {
        return soundName;
    }

    public void setVolume(float volume) {
        if (volume < 0.0f) {
            this.volume = 0.0f;
        } else if (volume > 1.0f) {
            this.volume = 1.0f;
        } else {
            this.volume = volume;
        }

        changeVolume();
    }

    public float getVolume() {
        return volume;
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

    public void resume() {
        clip.setMicrosecondPosition(clipStopSec);
        if (clipStopSec != 0) {
            clip.start();
            clipStopSec = 0;
        }
    }

    public void pause() {
        clipStopSec = clip.getMicrosecondPosition();
        clip.stop();
    }

    public void resetSound() {
        clip.stop();
        clip.setMicrosecondPosition(0);
//        clip.drain();
    }

    @Override
    public void run() {
        clip.start();
        if (keepLooping) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}
