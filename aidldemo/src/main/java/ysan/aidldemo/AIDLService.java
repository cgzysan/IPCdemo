package ysan.aidldemo;

import android.app.Service;
import android.content.Intent;
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
            if (mCallback != null) {
                mCallback.onSuccess(res.getSinger() + "的" + res.getSongName());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ysan", "on bind");
        return mMusicManager;
    }
}
