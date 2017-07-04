package ysan.aidldemo.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by David on 2017/5/22.
 */

public class MusicSceneInfo implements Parcelable {
    private String singer;
    private String songName;
    private List<MusicInfo>list;

    public MusicSceneInfo() {
    }

    protected MusicSceneInfo(Parcel in) {
        singer = in.readString();
        songName = in.readString();
        list = in.createTypedArrayList(MusicInfo.CREATOR);
    }

    public static final Creator<MusicSceneInfo> CREATOR = new Creator<MusicSceneInfo>() {
        @Override
        public MusicSceneInfo createFromParcel(Parcel in) {
            return new MusicSceneInfo(in);
        }

        @Override
        public MusicSceneInfo[] newArray(int size) {
            return new MusicSceneInfo[size];
        }
    };

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public List<MusicInfo> getList() {
        return list;
    }

    public void setList(List<MusicInfo> list) {
        this.list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(singer);
        dest.writeString(songName);
        dest.writeTypedList(list);
    }
}
