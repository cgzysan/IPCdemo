package ysan.aidldemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import ysan.aidldemo.aidl.MusicCallback;
import ysan.aidldemo.aidl.MusicManager;
import ysan.aidldemo.aidl.MusicSceneInfo;


/**
 * Created by YSAN on 2017/05/08 08:45
 * AIDL通信服务端代码
 */

public class AIDLService extends Service {

    private static MusicCallback mCallback = null;

    private final MusicManager.Stub mMusicManager = new MusicManager.Stub() {
        @Override
        public void registerCallback(MusicCallback callback) throws RemoteException {
            Log.i("ysan", "service registerCallback");
            mCallback = callback;
        }

        @Override
        public void dealResult(MusicSceneInfo res) throws RemoteException {
            Log.i("ysan", "service dealResult" + res.getSongName());
            Intent intent = new Intent("start_music");
            intent.putExtra("music", res);
            sendBroadcast(intent);
        }

        @Override
        public void dealOpration(String operation) throws RemoteException {
            Log.i("ysan", "发送 operation = " + operation);
            Intent intent = new Intent("music_operation");
            intent.putExtra("operation", operation);
            sendBroadcast(intent);
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("callback")) {
                String callback = intent.getStringExtra("callback");
                try {
                    mCallback.onSuccess(callback + "操作成功");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter("callback");
        registerReceiver(mReceiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ysan", "on bind");
        return mMusicManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
