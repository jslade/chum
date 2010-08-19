package chum.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;


/**
   Class to handle loading sounds and playing them when needed.
*/
public class SoundManager
{
    private final SoundPool soundPool;
    private final SparseIntArray soundMap;
    private final Context context;
    private final AudioManager mgr;


    public SoundManager(Context context) {
        this.context = context;
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);  
        soundMap = new SparseIntArray();
        mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);  
    }

    public void loadSound(int id, int res) {  
        synchronized(this) {
            soundMap.put(id, soundPool.load(context, res, 1));  
        }
    }

    public void playSound(int id) {
        synchronized(this) {
            // The next 4 lines calculate the current volume
            // in a scale of 0.0 to 1.0
            float curVol = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);  
            float maxVol = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = curVol / maxVol;  
            playSound(id,volume);
        }
    }


    public void playSound(int id, float volume) {
        synchronized(this) {
            int snd_id = soundMap.get(id);
            if ( volume > 1f ) volume = 1f;
            if ( volume <= 0f ) return;
            soundPool.play(snd_id, volume, volume, 1, 0, 1f);
        }
    }  

}

