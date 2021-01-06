package com.hryzx.consumerapp.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class UserContract {
    public static final String AUTHORITY = "com.hryzx.submissiongithubuser3";
    private static final String SCHEME = "content";

    public static final class UserColumns implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String ID = "id";
        public static final String URL = "url";
        public static final String NAME = "name";
        public static final String USERNAME = "username";
        public static final String DESCRIPTION = "description";
        public static final String LOCATION = "location";
        public static final String PHOTO = "photo";
        public static final String FOLLOWING = "following";
        public static final String FOLLOWERS = "followers";
        public static final String FOLLOWERS_URL = "followers_url";
        public static final String FOLLOWING_URL = "following_url";
        public static final String REPOS = "repos";
        public static final String COMPANY = "company";
        public static final String BLOG = "blog";
        public static final String EMAIL = "email";

        // content://com.dicoding.hryzx.submissiongithubuser3/users
        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();
    }
}
