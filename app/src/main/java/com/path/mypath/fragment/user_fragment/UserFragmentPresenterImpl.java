package com.path.mypath.fragment.user_fragment;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.data_parser.DataReply;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.fragment.FansData;
import com.path.mypath.fragment.MessageArray;
import com.path.mypath.fragment.MessageObject;

import java.util.ArrayList;
import java.util.List;

public class UserFragmentPresenterImpl implements UserFragmentPresenter {

    private UserFragmentVu mView;

    private DataObject data;

    private Gson gson;

    private String message;

    private ArrayList<DataArray> dataArray;

    private ArrayList<ArticleLikeNotification> likeArray;

    private ArrayList<MessageObject> msgArray;

    private ArrayList<String> roomIdArray;

    private ArrayList<DataObject> allUserArray;

    private ArrayList<ArrayList<ArticleLikeNotification>> allLikeArray;

    private ArrayList<String> emailArray;

    private int chatCount, userCount, likeCount;

    public UserFragmentPresenterImpl(UserFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onSearchDataFromFirebase() {
        mView.searchDataFromFirebase();
    }

    @Override
    public void onCatchJsonSuccessful(String json) {

        data = gson.fromJson(json, DataObject.class);
        if (data != null) {

            if (data.getArticleCount() != data.getDataArray().size()){
                data.setArticleCount(data.getDataArray().size());
                String userJson = gson.toJson(data);
                mView.saveUserData(userJson);
            }else {
                mView.setRecyclerView(data);
            }


        } else {
            Log.i("Michael", "資料結構 == null");
        }

    }

    @Override
    public void onUserPhotoClick() {
        mView.selectPhoto();
    }

    @Override
    public void onCatchUserPhotoByte(byte[] byteArray) {
        message = "上傳中....請稍後";
        mView.showToast(message);
        mView.showWaitDialog();
        mView.updateUserPhoto(byteArray);
    }

    @Override
    public void onCatchPhotoUrl(String downLoadUrl) {

        /**
         * 修正personal data
         */

        mView.saveUserPhoto(downLoadUrl);
        data.setUserPhoto(downLoadUrl);
        String json = gson.toJson(data);
        mView.updateFirebaseData(json);

        /**
         * 修正 home data 的所有照片(留言,愛心)
         */
        if (dataArray != null && dataArray.size() != 0) {
            for (DataArray data : dataArray) {
                if (data.getUserNickName().equals(mView.getNickname())) {
                    data.setUserPhoto(downLoadUrl);
                }
                for (DataUserPresHeart heart : data.getHeartPressUsers()) {
                    if (heart.getName().equals(mView.getNickname())) {
                        heart.setPhotoUrl(downLoadUrl);
                    }
                }
                for (DataReply reply : data.getReplyArray()) {
                    if (reply.getNickname().equals(mView.getNickname())) {
                        reply.setPhoto(downLoadUrl);
                    }
                }
            }
            String homeJson = gson.toJson(dataArray);
            mView.updateHomeData(homeJson);
        } else {
            Log.i("Michael", "home data 不用更新");
        }

        /**
         * 修正聊天室 所有照片
         */
        if (msgArray != null && msgArray.size() != 0) {
            for (MessageObject data : msgArray) {
                if (data.getUser2Nickname().equals(mView.getNickname())) {
                    data.setUser2PhotoUrl(downLoadUrl);
                } else if (data.getUser1Nickname().equals(mView.getNickname())) {
                    data.setUser1PhotoUrl(downLoadUrl);
                }
                for (MessageArray msg : data.getMessageArray()) {
                    if (msg.getUserNickname().equals(mView.getNickname())) {
                        msg.setUserPhotoUrl(downLoadUrl);
                    }
                }
            }
            updateChatData();
        } else {
            Log.i("Michael", "personal chat 不用更新");
        }
        /**
         * 修正愛心頁面所有照片
         */
        if (likeArray != null && likeArray.size() != 0) {
            for (ArticleLikeNotification data : likeArray) {
                if (data.getUserNickname().equals(mView.getNickname())) {
                    data.setUserPhoto(downLoadUrl);
                }
            }
            String likeJson = gson.toJson(likeArray);
            mView.updateLikeData(likeJson);
        } else {
            Log.i("Michael", "user like 不用更新");
        }

        /**
         * 修正所有使用者資料的照片(粉絲與追蹤的)
         */
        if (allUserArray != null && allUserArray.size() != 0) {
            for (DataObject data : allUserArray) {
                if (data.getFansArray() != null && data.getFansArray().size() != 0) {
                    for (FansData object : data.getFansArray()) {
                        if (object.getNickname().equals(mView.getNickname())) {
                            object.setPhoto(downLoadUrl);
                        }
                    }
                }
                if (data.getChaseArray() != null && data.getChaseArray().size() != 0) {
                    for (FansData object : data.getChaseArray()) {
                        if (object.getNickname().equals(mView.getNickname())) {
                            object.setPhoto(downLoadUrl);
                        }
                    }
                }
            }
            updateAllUserData();
        }

        /**
         * 修正所有 愛心資料頁面的照片
         */
        if (allLikeArray != null && allLikeArray.size() != 0) {
            for (ArrayList<ArticleLikeNotification> data : allLikeArray) {
                for (ArticleLikeNotification object : data) {
                    if (object.getUserNickname().equals(mView.getNickname())) {
                        object.setUserPhoto(downLoadUrl);
                    }
                }
            }
            updateAllLikeData();
        }

    }

    private void updateAllLikeData() {
        if (likeCount < allLikeArray.size()){
            String likeJson = gson.toJson(allLikeArray.get(likeCount));
            String email = emailArray.get(likeCount);
            Log.i("Michael","被更新的 EMAIL : "+email+" , json : "+likeJson);
            mView.updateAllLikeData(likeJson,email);
        }else {
            Log.i("Michael","likeData更新結束");
            likeCount = 0;
        }
    }

    private void updateAllUserData() {
        if (userCount < allUserArray.size()) {
            String userJson = gson.toJson(allUserArray.get(userCount));
            String userEmail = allUserArray.get(userCount).getEmail();
            mView.updateAllUserData(userEmail, userJson);
        } else {
            Log.i("Michael","allUserData更新結束");
            mView.closeWaitDialog();
            userCount = 0;
        }
    }

    private void updateChatData() {
        if (chatCount < roomIdArray.size()) {
            String chatJson = gson.toJson(msgArray.get(chatCount));
            String roomId = msgArray.get(chatCount).getRoomId();
            mView.updateChatData(chatJson, roomId);
        } else {
            Log.i("Michael","chatData更新結束");
            chatCount = 0;
        }
    }

    @Override
    public void onUpdateUserPhotoSuccessful() {
        message = "照片更新成功";
        mView.showToast(message);
    }

    @Override
    public void onCatchFirebaseListenerFail(String toString) {
        mView.showToast(toString);
    }

    @Override
    public void onAddIconClickListener() {
        mView.intentToRecordActivity();
    }

    @Override
    public void onMapItemClickListener(DataArray locationArray) {
        boolean isDataFound = false;
        if (dataArray != null) {
            for (DataArray data : dataArray) {
                if (data.getArticleTitle().equals(locationArray.getArticleTitle()) && data.getCurrentTime() == locationArray.getCurrentTime()) {
                    mView.intentToSingleViewActivity(data);
                    isDataFound = true;
                    break;
                }
            }
            if (!isDataFound) {
                mView.intentToSingleViewActivity(locationArray);
            }
        }


    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        //取得所有資料
        dataArray = gson.fromJson(json, new TypeToken<List<DataArray>>() {
        }.getType());
    }

    @Override
    public void onEditNicknameClickListener() {
        mView.showEditNicknameDialog();
    }

    @Override
    public void onEditConfirmClickListener(String nickname, String oldNickname) {

        if (nickname == null || nickname.isEmpty()){
            message = "請輸入名字";
            mView.showToast(message);
            return;
        }


        //更改personal data 的 array
        if (data != null) {
            mView.showWaitDialog();
            data.setUserNickname(nickname);
            for (DataArray object : data.getDataArray()) {
                if (object.getUserNickName().equals(oldNickname)) {
                    object.setUserNickName(nickname);
                }
                if (object.getReplyCount() != 0) {
                    for (DataReply reply : object.getReplyArray()) {
                        if (reply.getNickname().equals(oldNickname)) {
                            reply.setNickname(nickname);
                        }
                    }
                }
            }
            //更改粉絲名單
            if (data.getFansArray() != null && data.getFansArray().size() != 0) {
                for (FansData object : data.getFansArray()) {
                    if (object.getNickname().equals(oldNickname)) {
                        object.setNickname(nickname);
                    }
                }
            }
            //更改追蹤名單
            if (data.getChaseArray() != null && data.getChaseArray().size() != 0) {
                for (FansData object : data.getChaseArray()) {
                    if (object.getNickname().equals(oldNickname)) {
                        object.setNickname(nickname);
                    }
                }
            }

            String newJson = gson.toJson(data);
            mView.updateUserData(newJson, nickname);
            mView.updatePersonalData(newJson);
        }


        //更新 home data
        if (dataArray != null) {
            for (DataArray object : dataArray) {
                if (object.getUserNickName().equals(oldNickname)) {
                    object.setUserNickName(nickname);
                }
                if (object.getReplyCount() != 0) {
                    for (DataReply reply : object.getReplyArray()) {
                        if (reply.getNickname().equals(oldNickname)) {
                            reply.setNickname(nickname);
                        }
                    }
                }
                if (object.getHeartCount() != 0) {
                    for (DataUserPresHeart heart : object.getHeartPressUsers()) {
                        if (heart.getName().equals(oldNickname)) {
                            heart.setName(nickname);
                        }
                    }
                }
            }

            String homeJson = gson.toJson(dataArray);
            mView.updateHomeData(homeJson);
        }

        //更改 user like data
        if (likeArray != null) {
            for (ArticleLikeNotification object : likeArray) {
                if (object.getUserNickname().equals(oldNickname)) {
                    object.setUserNickname(nickname);
                }
                if (object.getArticleCreatorName().equals(oldNickname)) {
                    object.setArticleCreatorName(nickname);
                }
            }
            String likeJson = gson.toJson(likeArray);
            mView.updateLikeData(likeJson);
        }
        //修改聊天室資料
        if (msgArray != null && msgArray.size() != 0) {
            for (MessageObject msg : msgArray) {
                if (msg.getUser1Nickname().equals(oldNickname)) {
                    msg.setUser1Nickname(nickname);
                } else if (msg.getUser2Nickname().equals(oldNickname)) {
                    msg.setUser2Nickname(nickname);
                }
                for (MessageArray data : msg.getMessageArray()) {
                    if (data.getUserNickname().equals(oldNickname)) {
                        data.setUserNickname(nickname);
                    }
                }
            }
            updateChatData();
        }

        //更新所有使用者資料
        if (allUserArray != null && allUserArray.size() != 0) {
            for (DataObject data : allUserArray) {

                if (data.getUserNickname().equals(oldNickname)){
                    data.setUserNickname(nickname);
                }
                ArrayList<FansData> fansArray = data.getFansArray();
                ArrayList<FansData> chasArray = data.getChaseArray();

                ArrayList<DataArray> articleArray = data.getDataArray();

                for (DataArray object : articleArray){

                    if (object.getUserNickName().equals(oldNickname)) {
                        object.setUserNickName(nickname);
                    }
                    if (object.getReplyCount() != 0) {
                        for (DataReply reply : object.getReplyArray()) {
                            if (reply.getNickname().equals(oldNickname)) {
                                reply.setNickname(nickname);
                            }
                        }
                    }
                }
                if (fansArray != null && fansArray.size() != 0) {
                    for (FansData object : fansArray) {
                        if (object.getNickname().equals(oldNickname)) {
                            object.setNickname(nickname);
                        }
                    }
                }
                if (chasArray != null && chasArray.size() != 0) {
                    for (FansData object : chasArray) {
                        if (object.getNickname().equals(oldNickname)) {
                            object.setNickname(nickname);
                        }
                    }
                }
            }
            updateAllUserData();
        }

        //更新所有愛心頁面的資料
        if (allLikeArray != null && allLikeArray.size() != 0) {
            Log.i("Michael","舊的 名字 : "+oldNickname+ " , 新的名字 : "+nickname);
            int index = 0;
            for (ArrayList<ArticleLikeNotification> data : allLikeArray) {
                for (ArticleLikeNotification object : data) {
                    if (object.getUserNickname().equals(oldNickname)) {
                        Log.i("Michael","即將改變的名字 : "+object.getUserNickname() + "第"+index+"筆"+ " , 擁有者 : "+object.getArticleCreatorName());
                        object.setUserNickname(nickname);
                        Log.i("Michael","改變後的名字 : "+object.getUserNickname() + "第"+index+"筆"+ " , 擁有者 : "+object.getArticleCreatorName());
                    }
                    if (object.getArticleCreatorName().equals(oldNickname)){
                        Log.i("Michael","即將改變的名字 : "+object.getArticleCreatorName() + "第"+index+"筆"+ " , 擁有者 : "+object.getArticleCreatorName());
                        object.setArticleCreatorName(nickname);
                        Log.i("Michael","改變後的名字 : "+object.getArticleCreatorName() + "第"+index+"筆"+ " , 擁有者 : "+object.getArticleCreatorName());
                    }
                }
                index ++;
            }
            updateAllLikeData();
        }

    }

    @Override
    public void onEditSentenceClickListener() {
        mView.showEditSentenceDialog();
    }

    @Override
    public void onEditSentenceConfirmClickListener(String sentence) {
        if (data != null) {
            data.setSentence(sentence);
            String json = gson.toJson(data);
            mView.updateUserDataSentence(json, sentence);
        }
    }

    @Override
    public void onCatchLikeDataSuccess(String json) {
        if (json != null) {
            likeArray = gson.fromJson(json, new TypeToken<List<ArticleLikeNotification>>() {
            }.getType());
        }
    }

    @Override
    public void onLogoutClickListener() {
        mView.showLogoutConfirmDialog();
    }

    @Override
    public void onLogoutConfirmClickListener() {
        mView.logout();
    }

    @Override
    public void onCatchPersonalChatData(ArrayList<String> chatJsonArray, ArrayList<String> roomIdArray) {

        msgArray = new ArrayList<>();
        this.roomIdArray = roomIdArray;
        for (String json : chatJsonArray) {
            MessageObject data = gson.fromJson(json, MessageObject.class);
            if (data != null) {
                msgArray.add(data);
            }
        }
    }

    @Override
    public void onUpdateNextChatData() {
        chatCount++;
        updateChatData();

    }

    @Override
    public void onCatchAllUserData(ArrayList<String> userJsonArray) {
        allUserArray = new ArrayList<>();
        for (String json : userJsonArray) {
            DataObject data = gson.fromJson(json, DataObject.class);
            allUserArray.add(data);
        }
    }

    @Override
    public void onUpdateNextUserData() {
        userCount++;
        updateAllUserData();
    }

    @Override
    public void onCatchAllLikeData(ArrayList<String> likeJsonArray, ArrayList<String> emailArray) {
        allLikeArray = new ArrayList<>();
        this.emailArray = emailArray;
        for (String json : likeJsonArray) {
            ArrayList<ArticleLikeNotification> dataArray = gson.fromJson(json, new TypeToken<List<ArticleLikeNotification>>() {
            }.getType());
            allLikeArray.add(dataArray);
        }
    }

    @Override
    public void onUpdateNextLikeData() {
        likeCount++;
        updateAllLikeData();
    }

    @Override
    public void onArticleCountClick(ArrayList<DataArray> userDataArray) {
        for (DataArray data : dataArray){
            for (DataArray object : userDataArray){
                if (data.getUserNickName().equals(object.getUserNickName()) && data.getArticleTitle().equals(object.getArticleTitle()) && data.getCurrentTime() == object.getCurrentTime()){
                    object.setUserNickName(data.getUserNickName());
                    object.setReplyCount(data.getReplyCount());
                    object.setPhotoArray(data.getPhotoArray());
                    object.setReplyArray(data.getReplyArray());
                    object.setHeartCount(data.getHeartCount());
                    object.setHeartPressUsers(data.getHeartPressUsers());
                    object.setCurrentTime(data.getCurrentTime());
                    object.setLocationArray(data.getLocationArray());
                    object.setUserEmail(data.getUserEmail());
                    object.setUserPhoto(data.getUserPhoto());
                    object.setArticleTitle(data.getArticleTitle());
                    object.setDistance(data.getDistance());
                }
            }
        }

        mView.intentToMyArticleActivity(userDataArray);
    }

    @Override
    public void onChasingCountClickListener(DataObject data) {
        if (data.getChaseArray() == null || data.getChaseArray().size() == 0){
            return;
        }
        mView.intentToHeartActivity(data,"chasing");
    }

    @Override
    public void onFansCountClickListener(DataObject data) {
        if (data.getFansArray() == null || data.getFansArray().size() == 0){
            return;
        }
        mView.intentToHeartActivity(data,"fans");
    }
}
