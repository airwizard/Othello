/*
 * @author WANG Dazhi
 * @since 1st Nov, 2015
 * @version 1.0.0
 */
package hk.hku.msccs.daz.othello;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class LoginActivity extends Activity {
    private TextView mLoginTV;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.pal4);
        initView();
        init();

    }

    public void initView(){
        mLoginTV = (TextView) findViewById(R.id.login_tv);
        mLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundPool.play(mSoundMap.get(1), Float.valueOf("0.5"), Float.valueOf("0.5"), 0, 0, 1);
                startActivity(new Intent(mContext, MainActivity.class));

            }
        });

    }

    private void init() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
        mSoundMap = new HashMap<>();
        //noinspection deprecation
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        mSoundMap.put(1, mSoundPool.load(mContext, R.raw.start_game, 0));
    }
}
