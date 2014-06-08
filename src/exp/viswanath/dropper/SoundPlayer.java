package exp.viswanath.dropper;

import android.content.Context;
import android.media.MediaPlayer;

/*
 * Author : Viswanath L
 * 
 * viswanath.l@experionglobal.com
 * 
 * Dec 10, 2013
 * 
 */

public class SoundPlayer {
	
	// Holds the player instance
	private MediaPlayer soundPlayer;
	// The context
	private Context context;
	
	/**
	 * @Default Constructor 
	 * @param context
	 */
	public SoundPlayer(Context context)
	{
		this.context = context;
		soundPlayer = MediaPlayer.create(context,R.raw.pull);
	}

	/**
	 * Play the soundclip
	 * @param TYPE
	 */
	public void playSound(SOUND_TYPE TYPE)
	{
		/* Stop the currently playing sound */
		stopSound();
		
		/* Release the mediaPlayer object */
		soundPlayer.release();

		switch(TYPE)
		{
		case PLAY_PULL:
		{
			soundPlayer = MediaPlayer.create(context,R.raw.pull);
			break;
		}
		case PLAY_REFRESH:
		{
			soundPlayer = MediaPlayer.create(context,R.raw.refresh);
			break;
		}
		case PLAY_COMPRESS:
		{
			soundPlayer = MediaPlayer.create(context,R.raw.compressing);
			break;
		}
		}
		playSound();
	}

	/**
	 * Start playing sound
	 */
	private void playSound()
	{
		soundPlayer.start();
	}
	
	/**
	 * Stop playing sound
	 */
	private void stopSound()
	{
		if(soundPlayer.isPlaying())
			soundPlayer.stop();
	}

}

