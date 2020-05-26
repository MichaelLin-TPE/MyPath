package com.path.mypath.data_parser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.path.mypath.fragment.FansData;

import java.io.Serializable;
import java.util.ArrayList;

public class DataObject implements Parcelable {
    @SerializedName("user_photo")
    private String userPhoto;
    @SerializedName("user_nickname")
    private String userNickname;
    @SerializedName("article_count")
    private int articleCount;
    @SerializedName("friend_count")
    private int friendCount;
    @SerializedName("chasing_count")
    private int chasingCount;
    @SerializedName("data_array")
    private ArrayList<DataArray> dataArray;
    @SerializedName("is_public_account")
    private boolean isPublicAccount;
    @SerializedName("sentence")
    private String sentence;
    @SerializedName("email")
    private String email;
    @SerializedName("fans_array")
    private ArrayList<FansData> fansArray;
    @SerializedName("chase_array")
    private ArrayList<FansData> chaseArray;
    @SerializedName("room_id_list")
    private ArrayList<String> roomIdArray;

    public DataObject() {

    }

    public ArrayList<String> getRoomIdArray() {
        return roomIdArray;
    }

    public void setRoomIdArray(ArrayList<String> roomIdArray) {
        this.roomIdArray = roomIdArray;
    }

    public ArrayList<FansData> getFansArray() {
        return fansArray;
    }

    public void setFansArray(ArrayList<FansData> fansArray) {
        this.fansArray = fansArray;
    }

    public ArrayList<FansData> getChaseArray() {
        return chaseArray;
    }

    public void setChaseArray(ArrayList<FansData> chaseArray) {
        this.chaseArray = chaseArray;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public boolean isPublicAccount() {
        return isPublicAccount;
    }

    public void setPublicAccount(boolean publicAccount) {
        isPublicAccount = publicAccount;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }

    public int getChasingCount() {
        return chasingCount;
    }

    public void setChasingCount(int chasingCount) {
        this.chasingCount = chasingCount;
    }

    public ArrayList<DataArray> getDataArray() {
        return dataArray;
    }

    public void setDataArray(ArrayList<DataArray> dataArray) {
        this.dataArray = dataArray;
    }

    public DataObject(Parcel in) {
        userPhoto = in.readString();
        userNickname = in.readString();
        articleCount = in.readInt();
        friendCount = in.readInt();
        chasingCount = in.readInt();
        if (in.readByte() == 0x01) {
            dataArray = new ArrayList<DataArray>();
            in.readList(dataArray, DataArray.class.getClassLoader());
        } else {
            dataArray = null;
        }
        isPublicAccount = in.readByte() != 0x00;
        sentence = in.readString();
        email = in.readString();
        if (in.readByte() == 0x01) {
            fansArray = new ArrayList<FansData>();
            in.readList(fansArray, FansData.class.getClassLoader());
        } else {
            fansArray = null;
        }
        if (in.readByte() == 0x01) {
            chaseArray = new ArrayList<FansData>();
            in.readList(chaseArray, FansData.class.getClassLoader());
        } else {
            chaseArray = null;
        }
        if (in.readByte() == 0x01) {
            roomIdArray = new ArrayList<String>();
            in.readList(roomIdArray, String.class.getClassLoader());
        } else {
            roomIdArray = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userPhoto);
        dest.writeString(userNickname);
        dest.writeInt(articleCount);
        dest.writeInt(friendCount);
        dest.writeInt(chasingCount);
        if (dataArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(dataArray);
        }
        dest.writeByte((byte) (isPublicAccount ? 0x01 : 0x00));
        dest.writeString(sentence);
        dest.writeString(email);
        if (fansArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(fansArray);
        }
        if (chaseArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(chaseArray);
        }
        if (roomIdArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(roomIdArray);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DataObject> CREATOR = new Parcelable.Creator<DataObject>() {
        @Override
        public DataObject createFromParcel(Parcel in) {
            return new DataObject(in);
        }

        @Override
        public DataObject[] newArray(int size) {
            return new DataObject[size];
        }
    };
}
