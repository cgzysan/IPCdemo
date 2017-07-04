package ysan.servicedemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ysan.servicedemo.service.ServiceA;
import ysan.servicedemo.service.ServiceB;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mInfo1;
    private TextView mInfo2;
    private Button mStartA;
    private Button mStopA;
    private Button mStartB;
    private Button mStopB;

    boolean mBound_B = false;
    boolean mBound_C = false;
    private ServiceB mMServiceB;
    private Button mShow;
    private Button mStartC;
    private Button mStopC;

    Messenger mService = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
    }

    private void initEvent() {
        mStartA.setOnClickListener(this);
        mStopA.setOnClickListener(this);
        mStartB.setOnClickListener(this);
        mStopB.setOnClickListener(this);
        mStartC.setOnClickListener(this);
        mStopC.setOnClickListener(this);

        mShow.setOnClickListener(this);
    }

    private void initView() {
        mInfo1 = (TextView) findViewById(R.id.info1);
        mInfo2 = (TextView) findViewById(R.id.info2);
        mStartA = (Button) findViewById(R.id.startA);
        mStopA = (Button) findViewById(R.id.stopA);
        mStartB = (Button) findViewById(R.id.startB);
        mStopB = (Button) findViewById(R.id.stopB);
        mShow = (Button) findViewById(R.id.show_data);
        mStartC = (Button) findViewById(R.id.startC);
        mStopC = (Button) findViewById(R.id.stopC);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startA:
                startService(new Intent(MainActivity.this, ServiceA.class));
                break;
            case R.id.stopA:
                stopService(new Intent(MainActivity.this, ServiceA.class));
                break;
            case R.id.startB:
                Intent intentB = new Intent(MainActivity.this, ServiceB.class);
                bindService(intentB, mConnectionB, BIND_AUTO_CREATE);
                break;
            case R.id.stopB:
                unbindService(mConnectionB);
                break;
            case R.id.startC:
                //绑定服务端的服务，此处的action是service在Manifests文件里面声明的
                Intent intent = new Intent();
                intent.setAction("com.ysan.servicec");
                //不要忘记了包名，不写会报错
                intent.setPackage("ysan.servicedemo");
                bindService(intent, mConnectionC, BIND_AUTO_CREATE);
                break;
            case R.id.stopC:
                unbindService(mConnectionC);
                break;
            case R.id.show_data:
                if (mBound_B) {
                    int num = mMServiceB.getNum();
                    mInfo1.setText(num + "");
                }
                if (mBound_C)   {
                    Message message = Message.obtain(null, 110, 0, 0);
                    try {
                        mService.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound_B) {
            unbindService(mConnectionB);
        }
    }

    private ServiceConnection mConnectionB = new ServiceConnection() {

        //系统会调用该方法以传递服务的    onBind() 方法返回的 IBinder。
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceB.LocalBinder binder = (ServiceB.LocalBinder) service;
            mMServiceB = binder.getService();
            mBound_B = true;
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound_B = false;
        }
    };

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
