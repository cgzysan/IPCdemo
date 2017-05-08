package ysan.aidldemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YSAN on 2017/05/06 16:57
 * ${describe}
 */

public class Book implements Parcelable {
    private String name;
    private int price;
    public Book(){}

    public Book(Parcel in) {
        name = in.readString();
        price = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "name : " + name + ", price :" + price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //默认生成的模板类的对象只支持为 in 的定向 tag 。
    // 为什么呢？因为默认生成的类里面只有 writeToParcel() 方法，
    // 而如果要支持为 out 或者 inout 的定向 tag 的话，还需要实现 readFromParcel() 方法
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(price);
    }

    /**
     * 参数是一个Parcel,用它来存储与传输数据
     * @param dest
     * 添加了 readFromParcel() 方法之后，
     * 我们的 Book 类的对象在AIDL文件里就可以用 out 或者 inout 来作为它的定向 tag 了。
     */
    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        name = dest.readString();
        price = dest.readInt();
    }
}
