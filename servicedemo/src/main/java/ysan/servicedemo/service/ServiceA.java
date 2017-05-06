package ysan.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by YSAN on 2017/05/05 16:25
 * 一般启动service
 */

public class ServiceA extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ysan", "onBInd");
        //当其他组件调用startService()方法时，此方法将会被调用
        //如果不想让这个service被绑定，在此返回null即可
        return null;
    }

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
