package ysan.aidldemo.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by David on 2017/5/22.
 * 音乐信息
 */

public class MusicInfo implements Parcelable {
    private String songName;//歌曲名
    private String songUrl;//歌曲播放地址
    private String songSrc;//歌曲来源
    private String singer;//歌手

    public MusicInfo() {
    }

    protected MusicInfo(Parcel in) {
        songName = in.readString();
        songUrl = in.readString();
        songSrc = in.readString();
        singer = in.readString();
    }

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel in) {
            return new MusicInfo(in);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getSongSrc() {
        return songSrc;
    }

    public void setSongSrc(String songSrc) {
        this.songSrc = songSrc;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songName);
        dest.writeString(songUrl);
        dest.writeString(songSrc);
        dest.writeString(singer);
    }
}
