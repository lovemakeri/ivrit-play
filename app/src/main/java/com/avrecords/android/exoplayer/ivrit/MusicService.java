package com.avrecords.android.exoplayer.ivrit;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for music playback. This is the main controller that handles all user actions
 * regarding song playback
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    // Media player
    private MediaPlayer player;
    // Song list
  //  private ArrayList<Song> songs;
    // Current position
    private int songPos, part;

    public Uri songurl;
    // Our binder
    private final IBinder musicBind = new MusicBinder();
    private OnSongChangedListener onSongChangedListener;

    public static final int STOPPED = 0;
    public static final int PAUSED = 1;
    public static final int PLAYING = 2;
    private int playerState = STOPPED;
    private SeekBar mSeekBar;
    private TextView mCurrentPosition;
    private TextView mTotalDuration;

    private ListView mListView;

    private int mInterval = 1000;

    private double timeStart = 0, finalTime = 0;
    private int forwardTime = 5000, backwardTime = 5000;

    int[] anArray1;
    int[] anArray2;
    int[] anArray3;

    public boolean gotov = false;


    private SharedPreferences preferences;


    public static final String MY_LIST = "my_list";


    // Async thread to update progress bar every second
    private Runnable mProgressRunner = new Runnable() {
        @Override
        public void run() {
            if (mSeekBar != null) {
                mSeekBar.setProgress(player.getCurrentPosition());

                if(player.isPlaying()) {
                    mSeekBar.postDelayed(mProgressRunner, mInterval);
                }
            }
        }
    };


    public void onCreate(){
        // Create the service
        super.onCreate();
        // Initialize position
        songPos = 1;
        part = 1;

        anArray1 = new int[30];
        anArray2 = new int[30];
        anArray3 = new int[30];


        // Create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        // Set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // Set player event listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Stop media player
        player.stop();
        player.reset();
        player.release();



        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if ((songPos==3) && (part==1))  {

            return;
        } else {

        int newPos = songPos + 1;
        if (newPos == 31)
            newPos = 1;
        setSong(part, newPos);

            togglePlay();

        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

//        return false;
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // Start playback
        gotov=true;
        mp.start();
        int duration = mp.getDuration();
        mSeekBar.setMax(duration);
        mSeekBar.postDelayed(mProgressRunner, mInterval);



        // Set our duration text view to display total duration in format 0:00
        mTotalDuration.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        ));
    }


    /**
     * Sets a new song to buffer
     * @param songIndex - position of the song in the array
     */
    public void setSong(int partdo, int songIndex){

        part = partdo;
            songurl = Uri.parse(PlayerActivity.path+"part-"+part+"-lesson-" + songIndex + ".mp3");


        songPos = songIndex;
        playerState = STOPPED;

        player.stop();

        gotov=false;


        onSongChangedListener.onSongChanged(songPos);
    }

    /**
     * Toggles on/off song playback
     */
    public void togglePlay() {
        switch(playerState) {
            case STOPPED:
                playSong();
                break;
            case PAUSED:
                player.start();
                onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
                mProgressRunner.run();
                break;
            case PLAYING:
                player.pause();
                onSongChangedListener.onPlayerStatusChanged(playerState = PAUSED);
                mSeekBar.removeCallbacks(mProgressRunner);
                break;
        }
    }

    public void pause() {
        switch(playerState) {
            case STOPPED:
              //  playSong();
                break;
            case PAUSED:
              /*  player.start();
                onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
                mProgressRunner.run(); */
                break;
            case PLAYING:
                player.pause();
                onSongChangedListener.onPlayerStatusChanged(playerState = PAUSED);
                mSeekBar.removeCallbacks(mProgressRunner);
                break;
        }
    }


    private void playSong() {
/*        if (songs.size() <= songPos) // if the list is empty... just return
            return;
            */
        if (songPos > 30) // if the list is empty... just return
            return;
        // Play a song
        player.reset();
    //    mSeekBar.removeCallbacks(mProgressRunner);
        // Get song
    //    Song playSong = songs.get(songPos);
   //     long currSongID = playSong.getID();
        songurl = Uri.parse(PlayerActivity.path+part+"-lesson-" + songPos + ".mp3");
     /*   Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSongID); */
     /*   Uri trackUri = ContentUris.withAppendedId(
                PlayerActivity.path,
                songPos);
*/
        // Try playing the track... but it might be missing so try and catch
        try {
         //   player.setDataSource(getApplicationContext(), trackUri);
            player.setDataSource(getApplicationContext(), songurl);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
        mProgressRunner.run();
        onSongChangedListener.onPlayerStatusChanged(playerState = PLAYING);
    }

    public interface OnSongChangedListener {
        public void onSongChanged(int song);
        public void onPlayerStatusChanged(int status);
    }

    // Sets a callback to execute when we switch songs.. ie: update UI
   public void setOnSongChangedListener(OnSongChangedListener listener) {
        onSongChangedListener = listener;
    }

    /**
     * Sets seekBar to control while playing music seekBar - Seek bar instance that's already on our UI thread
    /*/

    public void setUIControls(SeekBar seekBar, TextView currentPosition, TextView totalDuration) {
     //   mListView = ListView;
        mSeekBar = seekBar;
        mCurrentPosition = currentPosition;
        mTotalDuration = totalDuration;
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Change current position of the song playback
                    if (gotov) {
                        player.seekTo(progress);
                    }
                }

                // Update our textView to display the correct number of second in format 0:00
                mCurrentPosition.setText(String.format("%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(progress),
                        TimeUnit.MILLISECONDS.toSeconds(progress) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress))
                ));
            }

      //      mListView

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }




    public int getCurrentIndex() {
        return songPos;
    }

    public void forward() {
        timeStart = player.getCurrentPosition();

        if ((timeStart + forwardTime) <= player.getDuration()) {
            timeStart = timeStart + forwardTime;
            player.seekTo((int) timeStart);
        }
        mProgressRunner.run();
    //    startPlayProgressUpdater();


    }

    public void backforward() {

        //check if we can go back at backwardTime seconds after song starts

        timeStart = player.getCurrentPosition();

        if ((timeStart - backwardTime) > 0) {
            timeStart = timeStart - backwardTime;
            player.seekTo((int) timeStart);
        }
        mProgressRunner.run();
        // startPlayProgressUpdater();


    }

}
