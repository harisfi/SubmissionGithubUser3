package com.hryzx.consumerapp;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hryzx.consumerapp.adapter.ListUserAdapter;
import com.hryzx.consumerapp.database.UserContract;
import com.hryzx.consumerapp.databinding.ActivityFavoritesBinding;
import com.hryzx.consumerapp.entity.User;
import com.hryzx.consumerapp.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

interface LoadUsersCallback {
    void preExecute();

    void postExecute(ArrayList<User> users);
}

public class FavoritesActivity extends AppCompatActivity implements LoadUsersCallback {
    private static final String EXTRA_STATE = "EXTRA_STATE";
    private ListUserAdapter listUserAdapter;
    private ActivityFavoritesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        getSupportActionBar().setTitle(getString(R.string.favorites));

        binding.rvFavUsers.setLayoutManager(new LinearLayoutManager(this));
        listUserAdapter = new ListUserAdapter();
        listUserAdapter.notifyDataSetChanged();
        binding.rvFavUsers.setAdapter(listUserAdapter);
        binding.rvFavUsers.setHasFixedSize(true);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        DataObserver myObserver = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(UserContract.UserColumns.CONTENT_URI, true, myObserver);

        if (savedInstanceState == null) {
            new LoadUsersAsync(this, this).execute();
        } else {
            ArrayList<User> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            setStatus(0, null);
            if (list != null) {
                listUserAdapter.setData(list);
                setStatus(1, null);
            } else {
                setStatus(2, getString(R.string.no_favorites));
            }
        }

        listUserAdapter.setOnItemClickCallback(data -> {
            Intent detailIntent = new Intent(FavoritesActivity.this, DetailActivity.class);
            detailIntent.putExtra(DetailActivity.EXTRA_USER, data);
            detailIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(detailIntent);
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, listUserAdapter.getListUsers());
    }

    @Override
    public void preExecute() {
        setStatus(0, null);
    }

    @Override
    public void postExecute(ArrayList<User> users) {
        setStatus(1, null);
        if (users.size() > 0) {
            listUserAdapter.setData(users);
        } else {
            setStatus(2, getString(R.string.no_favorites));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return true;
    }

    private void setStatus(int status, String message) {
        switch (status) {
            case 0: // loading
                runOnUiThread(() -> {
                    binding.favProgressbar.setVisibility(View.VISIBLE);
                    binding.rvFavUsers.setVisibility(View.INVISIBLE);
                    binding.tvFavStatus.setVisibility(View.INVISIBLE);
                });
                break;
            case 1: // stop loading
                runOnUiThread(() -> {
                    binding.favProgressbar.setVisibility(View.INVISIBLE);
                    binding.rvFavUsers.setVisibility(View.VISIBLE);
                    binding.tvFavStatus.setVisibility(View.INVISIBLE);
                });
                break;
            case 2: // message
                runOnUiThread(() -> {
                    binding.favProgressbar.setVisibility(View.INVISIBLE);
                    binding.rvFavUsers.setVisibility(View.INVISIBLE);
                    binding.tvFavStatus.setVisibility(View.VISIBLE);
                    binding.tvFavStatus.setText(message);
                });
                break;
        }
    }

    private static class LoadUsersAsync {
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadUsersCallback> weakCallback;

        private LoadUsersAsync(Context context, LoadUsersCallback callback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
        }

        void execute() {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            weakCallback.get().preExecute();
            executor.execute(() -> {
                Context context = weakContext.get();
                Cursor dataCursor = context.getContentResolver().query(UserContract.UserColumns.CONTENT_URI, null, null, null, null);
                ArrayList<User> users = MappingHelper.mapCursorToArrayList(dataCursor);

                handler.post(() -> weakCallback.get().postExecute(users));
            });
        }
    }

    public static class DataObserver extends ContentObserver {

        final Context context;

        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadUsersAsync(context, (LoadUsersCallback) context).execute();
        }
    }
}