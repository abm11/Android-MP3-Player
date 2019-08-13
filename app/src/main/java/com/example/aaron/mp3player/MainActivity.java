package com.example.aaron.mp3player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;

import static com.example.aaron.mp3player.MP3Player.MP3PlayerState.PLAYING;


public class MainActivity extends AppCompatActivity {
    MP3Player mp3PlayerService; //Instance of service
    boolean isBound = false; //Bind does not exist

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView lv = (ListView) findViewById(R.id.listView);
        File musicDir = new File(
                Environment.getExternalStorageDirectory().getPath()+ "/Music/");
        File list[] = musicDir.listFiles();
        lv.setAdapter(new ArrayAdapter<File>(this,
                android.R.layout.simple_list_item_1, list));

        Intent intent = new Intent(MainActivity.this, MP3Player.class);
        bindService(intent, mp3PlayerConncetion, Context.BIND_AUTO_CREATE);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> myAdapter,
                                            View myView,
                                            int myItemInt,
                                            long mylng) {

                        Log.d("g53mdp", "Binded");
                        File selectedFromList =(File) (lv.getItemAtPosition(myItemInt));
                        Log.d("g53mdp", selectedFromList.getAbsolutePath());
                        mp3PlayerService.load(selectedFromList.getAbsolutePath()); //Load path selected
                        String currentSongString = mp3PlayerService.getFilePath(); //String for Current song
                        final EditText currentSong = (EditText) findViewById(R.id.currentSong); //Text control for current song
                        currentSong.setText("Current Song - "+currentSongString); //Assign string to button
            }
        });
    }


    public void onPlayClick(View v) { //Call play from service
        mp3PlayerService.play();
    } //Play button

    public void onPauseClick(View v) {//Call pause from service
        mp3PlayerService.pause();
    } //Pause button

    public void onStopClick(View v) {//Call stop from service
        mp3PlayerService.stop();
        final EditText currentSong = (EditText) findViewById(R.id.currentSong); //Text control for current song
        currentSong.setText("Current Song - "); //Resets text to blank as song is stopped
        onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MP3Player.class);
        startService(intent);
        bindService(intent, mp3PlayerConncetion, Context.BIND_AUTO_CREATE);
        Log.d("g53mdp", "Binded");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound==true) { //Check for binding to avoid null exception
            unbindService(mp3PlayerConncetion); //Unbind service
            Log.d("g53mdp", "Unbinded");
        }
        isBound = false; //Bind does not exist
    }

    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute) */
    public void onButtonClick(View v) {
        if (isBound) {
            // Call a method from the MP3Player.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mp3PlayerConncetion = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to MP3Player, cast the IBinder and get MP3Player instance
            MP3Player.LocalBinder binder = (MP3Player.LocalBinder) service;
            mp3PlayerService = binder.getService();
            isBound = true;
            Log.d("g53mdp", "connected");

            MP3Player.MP3PlayerState check = mp3PlayerService.getState();
            if(check == PLAYING){
                String currentSongString = mp3PlayerService.getFilePath(); //String for Current song
                final EditText currentSong = (EditText) findViewById(R.id.currentSong); //Text control for current song
                currentSong.setText("Current Song - "+currentSongString); //Assign string to button

                }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false; //Bind does not exist
            Log.d("g53mdp", "Disconnected");
        }
    };

}