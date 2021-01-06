package com.hryzx.submissiongithubuser3.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hryzx.submissiongithubuser3.database.UserHelper;

import static com.hryzx.submissiongithubuser3.database.UserContract.AUTHORITY;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.CONTENT_URI;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.TABLE_NAME;

public class UserProvider extends ContentProvider {
    private static final int USER = 1;
    private static final int USER_ID = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://com.hryzx.submissiongithubuser3/users
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, USER);
        // content://com.hryzx.submissiongithubuser3/users/username
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/*", USER_ID);
    }

    private UserHelper userHelper;

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
            case USER_ID:
                cursor = userHelper.queryById(uri.getLastPathSegment());
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
        if (sUriMatcher.match(uri) == USER) {
            added = userHelper.insert(contentValues);
        } else {
            added = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI + "/" + added);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int updated;
        if (sUriMatcher.match(uri) == USER_ID) {
            updated = userHelper.update(uri.getLastPathSegment(), contentValues);
        } else {
            updated = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int deleted;
        if (sUriMatcher.match(uri) == USER_ID) {
            deleted = userHelper.deleteById(uri.getLastPathSegment());
        } else {
            deleted = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return deleted;
    }
}