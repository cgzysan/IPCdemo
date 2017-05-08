package ysan.ipcdemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ysan.ipcdemo.R;

/**
 * Created by YSAN on 2017/05/08 09:05
 * 通过Messenger实现IPC
 */

public class MessengerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mIPCinfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Button hello = (Button) findViewById(R.id.hello_world);
        mIPCinfo = (TextView) findViewById(R.id.IPC_info);
        hello.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //绑定服务端的服务，此处的action是service在Manifests文件里面声明的
        Intent intent = new Intent();
        intent.setAction("com.ysan.servicec");
        //不要忘记了包名，不写会报错
        intent.setPackage("ysan.servicedemo");
        bindService(intent, mConnectionC, BIND_AUTO_CREATE);
    }

    class clientHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 210:
                    Log.i("ysan", "收到IPC反馈消息");
                    mIPCinfo.setText("hi,nice to meet you!");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    Messenger clientMessenger = new Messenger(new clientHandler());

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hello_world:
                if (mBound_C) {
                    Message msg = Message.obtain(null, 110, 0, 0);
                    msg.replyTo = clientMessenger;
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                break;
            default:
                break;
        }
    }

    Messenger mService = null;
    boolean mBound_C = false;

    public ServiceConnection mConnectionC = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            mBound_C = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound_C = false;
        }
    };
}
