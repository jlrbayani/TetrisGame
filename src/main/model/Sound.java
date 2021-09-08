package main.model;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

// represents a Sound that could be heard in the Game
public class Sound implements Runnable{

    private String fileRef;
    private String soundName;
    private Clip clip;
    private float volume;
    private long clipStopSec;
    private boolean keepLooping;

    // the constructor for sound requiring the file reference, a name for the sound, and the current volume it should be set with
    public Sound(String fileRef, String soundName, float currentVolume) {
        this.fileRef = fileRef;
        this.soundName = soundName;
        this.keepLooping = false;
        clipStopSec = 0;

        initClip();
        setVolume(currentVolume);
    }

    // MODIFIES: this
    // EFFECTS: initializes a clip that is supposed to be tied to the sound using fileRef
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

    // MODIFIES: this
    // EFFECTS: changes the volume of the sound depending on volume
    public void changeVolume() {
        FloatControl masterGainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        masterGainControl.setValue(20f * (float) Math.log10(volume));
    }

    // MODIFIES: this
    // EFFECTS: mutes this sound
    public void mute() {
        BooleanControl booleanControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
        booleanControl.setValue(true);
    }

    // MODIFIES: this
    // EFFECTS: unmutes this sound
    public void unMute() {
        BooleanControl booleanControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
        booleanControl.setValue(false);
    }

    // MODIFIES: this
    // EFFECTS: the clip resumes at clipStopSec
    public void resume() {
        clip.setMicrosecondPosition(clipStopSec);
        if (clipStopSec != 0) {
            clip.start();
            clipStopSec = 0;
        }
    }

    // MODIFIES: this
    // EFFECTS: stores the value of the last played second of the clip and stops the clip as well
    public void pause() {
        clipStopSec = clip.getMicrosecondPosition();
        clip.stop();
    }

    // MODIFIES: this
    // EFFECTS: resets the sound to its starting point
    public void resetSound() {
        clip.stop();
        clip.setMicrosecondPosition(0);
    }

    // EFFECTS: plays the clip until it ends, unless keepLooping is set to true which makes the clip play endlessly
    @Override
    public void run() {
        clip.start();
        if (keepLooping) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}
