package com.hryzx.consumerapp.helper;

import android.database.Cursor;

import com.hryzx.consumerapp.entity.User;

import java.util.ArrayList;

import static com.hryzx.consumerapp.database.UserContract.UserColumns;

public class MappingHelper {
    public static ArrayList<User> mapCursorToArrayList(Cursor userCursor) {
        ArrayList<User> users = new ArrayList<>();
        while (userCursor.moveToNext()) {
            User user = new User();

            user.setId(userCursor.getInt(userCursor.getColumnIndexOrThrow(UserColumns.ID)));
            user.setUrl(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.URL)));
            user.setName(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.NAME)));
            user.setUsername(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.USERNAME)));
            user.setDescription(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.DESCRIPTION)));
            user.setLocation(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.LOCATION)));
            user.setPhoto(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.PHOTO)));
            user.setFollowing(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.FOLLOWING)));
            user.setFollowers(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.FOLLOWERS)));
            user.setFollowers_url(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.FOLLOWERS_URL)));
            user.setFollowing_url(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.FOLLOWING_URL)));
            user.setRepos(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.REPOS)));
            user.setCompany(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.COMPANY)));
            user.setBlog(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.BLOG)));
            user.setEmail(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.EMAIL)));
            users.add(user);
        }
        return users;
    }

    public static User mapCursorToObject(Cursor userCursor) {
        userCursor.moveToFirst();
        User user = new User();

        user.setId(userCursor.getInt(userCursor.getColumnIndexOrThrow(UserColumns.ID)));
        user.setUrl(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.URL)));
        user.setName(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.NAME)));
        user.setUsername(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.USERNAME)));
        user.setDescription(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.DESCRIPTION)));
        user.setLocation(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.LOCATION)));
        user.setPhoto(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.PHOTO)));
        user.setFollowing(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.FOLLOWING)));
        user.setFollowers(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.FOLLOWERS)));
        user.setFollowers_url(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.FOLLOWERS_URL)));
        user.setFollowing_url(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.FOLLOWING_URL)));
        user.setRepos(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.REPOS)));
        user.setCompany(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.COMPANY)));
        user.setBlog(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.BLOG)));
        user.setEmail(userCursor.getString(userCursor.getColumnIndexOrThrow(UserColumns.EMAIL)));

        return user;
    }

}
