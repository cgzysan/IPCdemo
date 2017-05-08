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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ysan.aidldemo.Book;
import ysan.aidldemo.BookManager;
import ysan.ipcdemo.R;

/**
 * Created by YSAN on 2017/05/08 09:19
 * 通过AIDL实现IPC
 */

public class AIDLActivity extends AppCompatActivity implements View.OnClickListener {

    //由AIDL文件生成的Java类
    private BookManager mBookManager = null;

    //标志当前与服务端连接状况的布尔值，false为未连接，true为连接中
    private boolean mBound = false;

    //包含Book对象的list
    private List<Book> mBooks;
    private Button mAddBook;
    private ListView mBookList;
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);

        initView();
        initEvent();
        initData();
    }

    private void initData() {
        mAdapter = new BookAdapter();
        mBookList.setAdapter(mAdapter);
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
    }

    private void initView() {
        mAddBook = (Button) findViewById(R.id.add_book);
        mBookList = (ListView) findViewById(R.id.book_list);
    }

    class BookAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mBooks != null) {
                return mBooks.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView info = new TextView(AIDLActivity.this);
            Book bookInfo = mBooks.get(position);
            info.setText("书名 ：" + bookInfo.getName() + "价格 ：" + bookInfo.getPrice());
            return info;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_book:
                //如果与服务端的连接处于未连接状态，则尝试连接
                if (!mBound) {
                    attemptToBindService();
                    Toast.makeText(this, "当前与服务端处于未连接状态，正在尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mBookManager == null)
                    return;

                Book book = new Book();
                book.setPrice(67);
                book.setName("越狱");
                try {
                    mBookManager.addBook(book);
                    mBooks = mBookManager.getBooks();
                    mAdapter.notifyDataSetChanged();
                    Log.i("ysan", "book : " + book.toString() + "=== mBooks : " + mBooks.toString());
                } catch (RemoteException e) {
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
            mBookManager = BookManager.Stub.asInterface(service);
            mBound = true;

            if (mBookManager != null) {
                try {
                    mBooks = mBookManager.getBooks();
                    Log.i("ysan", "mBooks : " + mBooks.toString());
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
}
