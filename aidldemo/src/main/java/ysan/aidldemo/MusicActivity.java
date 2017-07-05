package ysan.aidldemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import ysan.aidldemo.aidl.MusicSceneInfo;

/**
 * Created by YSAN on 2017/07/05
 */

public class MusicActivity extends AppCompatActivity {

    private TextView mOpra;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("ysan", "收到广播");
            String action = intent.getAction();
            if (action.equals("music_operation")) {
                final String operation = intent.getStringExtra("operation");
                mOpra.setText(operation);

                Intent callback = new Intent("callback");
                callback.putExtra("callback", operation);
                sendBroadcast(callback);

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        IntentFilter filter = new IntentFilter("music_operation");
        registerReceiver(mReceiver, filter);

        MusicSceneInfo music = getIntent().getParcelableExtra("music");
        TextView info = (TextView) findViewById(R.id.music_info);
        mOpra = (TextView) findViewById(R.id.music_operation);

        info.setText("播放" + music.getSinger() + "的" + music.getSongName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
