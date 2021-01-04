package com.hryzx.consumerapp.helper;

import android.database.Cursor;

import com.hryzx.consumerapp.entity.User;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.BLOG;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.COMPANY;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.DESCRIPTION;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.EMAIL;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.FOLLOWERS;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.FOLLOWERS_URL;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.FOLLOWING;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.FOLLOWING_URL;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.LOCATION;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.NAME;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.PHOTO;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.REPOS;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.URL;
import static com.hryzx.consumerapp.database.UserContract.UserColumns.USERNAME;

public class MappingHelper {
    public static ArrayList<User> mapCursorToArrayList(Cursor userCursor) {
        ArrayList<User> users = new ArrayList<>();
        while (userCursor.moveToNext()) {
            User user = new User();

            user.setId(userCursor.getInt(userCursor.getColumnIndexOrThrow(_ID)));
            user.setUrl(userCursor.getString(userCursor.getColumnIndexOrThrow(URL)));
            user.setName(userCursor.getString(userCursor.getColumnIndexOrThrow(NAME)));
            user.setUsername(userCursor.getString(userCursor.getColumnIndexOrThrow(USERNAME)));
            user.setDescription(userCursor.getString(userCursor.getColumnIndexOrThrow(DESCRIPTION)));
            user.setLocation(userCursor.getString(userCursor.getColumnIndexOrThrow(LOCATION)));
            user.setPhoto(userCursor.getString(userCursor.getColumnIndexOrThrow(PHOTO)));
            user.setFollowing(userCursor.getString(userCursor.getColumnIndexOrThrow(FOLLOWING)));
            user.setFollowers(userCursor.getString(userCursor.getColumnIndexOrThrow(FOLLOWERS)));
            user.setFollowers_url(userCursor.getString(userCursor.getColumnIndexOrThrow(FOLLOWERS_URL)));
            user.setFollowing_url(userCursor.getString(userCursor.getColumnIndexOrThrow(FOLLOWING_URL)));
            user.setRepos(userCursor.getString(userCursor.getColumnIndexOrThrow(REPOS)));
            user.setCompany(userCursor.getString(userCursor.getColumnIndexOrThrow(COMPANY)));
            user.setBlog(userCursor.getString(userCursor.getColumnIndexOrThrow(BLOG)));
            user.setEmail(userCursor.getString(userCursor.getColumnIndexOrThrow(EMAIL)));
            users.add(user);
        }
        return users;
    }

    public static User mapCursorToObject(Cursor userCursor) {
        userCursor.moveToFirst();
        User user = new User();

        user.setId(userCursor.getInt(userCursor.getColumnIndexOrThrow(_ID)));
        user.setUrl(userCursor.getString(userCursor.getColumnIndexOrThrow(URL)));
        user.setName(userCursor.getString(userCursor.getColumnIndexOrThrow(NAME)));
        user.setUsername(userCursor.getString(userCursor.getColumnIndexOrThrow(USERNAME)));
        user.setDescription(userCursor.getString(userCursor.getColumnIndexOrThrow(DESCRIPTION)));
        user.setLocation(userCursor.getString(userCursor.getColumnIndexOrThrow(LOCATION)));
        user.setPhoto(userCursor.getString(userCursor.getColumnIndexOrThrow(PHOTO)));
        user.setFollowing(userCursor.getString(userCursor.getColumnIndexOrThrow(FOLLOWING)));
        user.setFollowers(userCursor.getString(userCursor.getColumnIndexOrThrow(FOLLOWERS)));
        user.setFollowers_url(userCursor.getString(userCursor.getColumnIndexOrThrow(FOLLOWERS_URL)));
        user.setFollowing_url(userCursor.getString(userCursor.getColumnIndexOrThrow(FOLLOWING_URL)));
        user.setRepos(userCursor.getString(userCursor.getColumnIndexOrThrow(REPOS)));
        user.setCompany(userCursor.getString(userCursor.getColumnIndexOrThrow(COMPANY)));
        user.setBlog(userCursor.getString(userCursor.getColumnIndexOrThrow(BLOG)));
        user.setEmail(userCursor.getString(userCursor.getColumnIndexOrThrow(EMAIL)));

        return user;
    }

}
