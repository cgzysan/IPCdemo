package ysan.ipcdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ysan.ipcdemo.activity.AIDLActivity;
import ysan.ipcdemo.activity.MessengerActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mByMessenger;
    private Button mByAidl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
    }

    private void initEvent() {
        mByMessenger.setOnClickListener(this);
        mByAidl.setOnClickListener(this);
    }

    private void initView() {
        mByMessenger = (Button) findViewById(R.id.bt_messenger);
        mByAidl = (Button) findViewById(R.id.bt_aidl);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_messenger:
                startActivity(new Intent(MainActivity.this, MessengerActivity.class));
                break;
            case R.id.bt_aidl:
                startActivity(new Intent(MainActivity.this, AIDLActivity.class));
                break;
            default:
                break;
        }
    }
}
