package edu.moravian.csci299.DungeonDomination;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Handles the the music playing in the background.
 * Contains static methods to interact with the media player
 */
public class PlayMusic {

    /** The media player to play the background music */
    public static MediaPlayer mediaPlayer;
    
    /**
     * Start the audio on a loop
     */
    public static void playAudio(Context c) {
         mediaPlayer = MediaPlayer.create(c, R.raw.voodoo);
         mediaPlayer.setLooping(true);
         mediaPlayer.start();
    }

    /**
     * Stops the audio 
     */
    public static void stopAudio() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }
}
