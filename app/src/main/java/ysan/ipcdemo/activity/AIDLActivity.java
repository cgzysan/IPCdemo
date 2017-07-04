package ysan.ipcdemo.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ysan.aidldemo.aidl.MusicCallback;
import ysan.aidldemo.aidl.MusicInfo;
import ysan.aidldemo.aidl.MusicManager;
import ysan.aidldemo.aidl.MusicSceneInfo;
import ysan.ipcdemo.R;


/**
 * Created by YSAN on 2017/05/08 09:19
 * 通过AIDL实现IPC
 */

public class AIDLActivity extends AppCompatActivity implements View.OnClickListener {

    //由AIDL文件生成的Java类
    private MusicManager mMusicManager = null;
    //标志当前与服务端连接状况的布尔值，false为未连接，true为连接中
    private boolean mBound = false;

    //包含Book对象的list
    private Button mAddBook;
    private ListView mBookList;
    private Button mSend;
    private TextView mResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);

        initView();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            attemptToBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    private void initEvent() {
        mAddBook.setOnClickListener(this);
        mSend.setOnClickListener(this);
    }

    private void initView() {
        mAddBook = (Button) findViewById(R.id.add_book);
        mSend = (Button) findViewById(R.id.send_music);
        mResult = (TextView) findViewById(R.id.text_result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_book:
                //如果与服务端的连接处于未连接状态，则尝试连接
                attemptToBindService();
                return;
            case R.id.send_music:
                //发送歌曲
                MusicInfo musicInfo = new MusicInfo();
                List<MusicInfo> mList = new ArrayList<>();
                mList.add(musicInfo);
                MusicSceneInfo info = new MusicSceneInfo();
                info.setSongName("忘情水");
                info.setSinger("刘德华");
                info.setList(mList);
                try {
                    mMusicManager.dealResult(info);
                } catch (RemoteException e) {
                    Log.i("ysan", "Client dealResult Exception");
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 尝试与服务器建立连接
     */
    private void attemptToBindService() {
        Intent intent = new Intent();
        intent.setAction("com.ysan.aidl");
        intent.setPackage("ysan.aidldemo");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("ysan", "AIDLService connected");
            mMusicManager = MusicManager.Stub.asInterface(service);

            mBound = true;

            if (mMusicManager != null) {
                try {
                    mMusicManager.registerCallback(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ysan", "AIDLService disconnected");
            mBound = false;
        }
    };

    private MusicCallback.Stub mCallback = new MusicCallback.Stub() {
        @Override
        public void onSuccess(String result) throws RemoteException {
            Log.i("ysan", "Client onSuccess >> result = " + result);
            mResult.setText(result);
        }

        @Override
        public void onFailure(String error) throws RemoteException {
            Log.i("ysan", "Client onFailure >> error" + error);
            mResult.setText(error);
        }
    };
}
