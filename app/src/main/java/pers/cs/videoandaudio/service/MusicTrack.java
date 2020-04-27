package pers.cs.videoandaudio.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author chensen
 * @time 2020/4/12  20:55
 * @desc
 */
public class MusicTrack implements Parcelable {

    public long mId;
    public int mSourcePosition;

    public MusicTrack(long id, int sourcePosition) {
        mId = id;
        mSourcePosition = sourcePosition;
    }

    public MusicTrack(Parcel source) {
        this.mId = source.readLong();
        this.mSourcePosition = source.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 序列化
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeInt(this.mSourcePosition);
    }

    public static final Parcelable.Creator<MusicTrack> CREATOR = new Creator<MusicTrack>() {

        //解码
        @Override
        public MusicTrack createFromParcel(Parcel source) {
            return new MusicTrack(source);
        }

        @Override
        public MusicTrack[] newArray(int size) {
            return new MusicTrack[size];
        }
    };
}
