package ysan.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by YSAN on 2017/05/08 08:45
 * AIDL通信服务端代码
 */

public class AIDLService extends Service {

    private List<Book> mBooks = new ArrayList<>();

    //由AIDL文件生成的BookManager
    private final BookManager.Stub mBookManager = new BookManager.Stub() {

        @Override
        public List<Book> getBooks() throws RemoteException {
            synchronized (this) {
                Log.i("ysan", "invoking getBooks method , now the list is :" + mBooks.toString());

                if (mBooks != null) {
                    return mBooks;
                }
                return new ArrayList<>();
            }
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            synchronized (this) {
                if (mBooks == null) {
                    mBooks = new ArrayList<>();
                }
                if (book == null) {
                    Log.i("ysan", "Book is null in In");
                    book = new Book();
                }
                //尝试修改book的参数，主要是为了观察其到客户端的反馈
                book.setPrice(233);

                if (!mBooks.contains(book)) {
                    mBooks.add(book);
                }
                //打印mBooks列表，观察客户端传过来的值
                Log.i("ysan", "invoking addBooks method , now the list is : " + mBooks.toString());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Book book = new Book();
        book.setName("天局");
        book.setPrice(39);
        mBooks.add(book);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ysan", "on bind");
        return mBookManager;
    }
}