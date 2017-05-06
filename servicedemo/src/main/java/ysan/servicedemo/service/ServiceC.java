package ysan.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by YSAN on 2017/05/05 19:18
 * ${describe}
 */

public class ServiceC extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ysan", "onBInd");
        //当其他组件调用startService()方法时，此方法将会被调用
        //如果不想让这个service被绑定，在此返回null即可
        return serviceMessenger.getBinder();
    }

    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 110:
                    Toast.makeText(ServiceC.this, "hello world", Toast.LENGTH_SHORT).show();
                    Log.i("ysan", "收到消息，hello world 发送消息 hello");
                    clientMessenger = msg.replyTo;
                    if (clientMessenger != null) {
                        Message recover = Message.obtain(null, 210, 0, 0);
                        try {
                            clientMessenger.send(recover);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    //serviceMessenger表示的是Service端的Messenger，其内部指向了ServiceC的ServiceHandler实例
    //可以用serviceMessenger向MyService发送消息
    Messenger serviceMessenger = new Messenger(new ServiceHandler());
    //clientMessenger是客户端自身的Messenger，内部指向了ClientHandler的实例
    //MyService可以通过Message的replyTo得到clientMessenger，从而MyService可以向客户端发送消息，
    //并由ClientHandler接收并处理来自于Service的消息
    Messenger clientMessenger = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ysan", "onCreate");
        //只在service创建的时候调用一次，可以在此进行一些一次性的初始化操作
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ysan", "onStartCommand");
        //当其他组件调用startService()方法时，此方法将会被调用
        //在这里进行这个service主要的操作
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("ysan", "onDestroy");
        //service调用的最后一个方法
        //在此进行资源的回收
        super.onDestroy();
    }
}
