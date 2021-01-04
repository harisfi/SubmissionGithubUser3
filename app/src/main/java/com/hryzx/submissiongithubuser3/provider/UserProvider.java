package com.hryzx.submissiongithubuser3.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hryzx.submissiongithubuser3.database.UserHelper;

import java.util.Objects;

import static com.hryzx.submissiongithubuser3.database.UserContract.AUTHORITY;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.CONTENT_URI;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.TABLE_NAME;

public class UserProvider extends ContentProvider {
    private static final int USER = 1;
    private static final int USER_USERNAME = 2;
    private UserHelper userHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // content://com.hryzx.submissiongithubuser3/users
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, USER);
        // content://com.hryzx.submissiongithubuser3/users/username
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/*", USER_USERNAME);
    }

    @Override
    public boolean onCreate() {
        userHelper = UserHelper.getInstance(getContext());
        userHelper.open();
        return true;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case USER:
                cursor = userHelper.queryAll();
                break;
            case USER_USERNAME:
                cursor = userHelper.queryByUsername(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }

        return cursor;
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long added;
        switch (sUriMatcher.match(uri)) {
            case USER:
                added = userHelper.insert(contentValues);
                break;
            default:
                added = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI + "/" + added);
    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int updated;
        switch (sUriMatcher.match(uri)) {
            case USER_USERNAME:
                updated = userHelper.update(uri.getLastPathSegment(), contentValues);
                break;
            default:
                updated = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int deleted;
        switch (sUriMatcher.match(uri)) {
            case USER_USERNAME:
                deleted = userHelper.deleteByUsername(uri.getLastPathSegment());
                break;
            default:
                deleted = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return deleted;
    }
}