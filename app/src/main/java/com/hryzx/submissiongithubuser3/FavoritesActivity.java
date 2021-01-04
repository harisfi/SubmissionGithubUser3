package com.hryzx.submissiongithubuser3;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hryzx.submissiongithubuser3.adapter.ListUserAdapter;
import com.hryzx.submissiongithubuser3.database.UserHelper;
import com.hryzx.submissiongithubuser3.databinding.ActivityFavoritesBinding;
import com.hryzx.submissiongithubuser3.entity.User;
import com.hryzx.submissiongithubuser3.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

interface LoadUsersCallback {
    void preExecute();

    void postExecute(ArrayList<User> users);
}

public class FavoritesActivity extends AppCompatActivity implements LoadUsersCallback {
    private static final String EXTRA_STATE = "EXTRA_STATE";
    private ListUserAdapter listUserAdapter;
    private ActivityFavoritesBinding binding;
    private UserHelper userHelper;

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

        userHelper = UserHelper.getInstance(getApplicationContext());
        userHelper.open();

        if (savedInstanceState == null) {
            new LoadUsersAsync(userHelper, this).execute();
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
                binding.favProgressbar.setVisibility(View.VISIBLE);
                binding.rvFavUsers.setVisibility(View.INVISIBLE);
                binding.tvFavStatus.setVisibility(View.INVISIBLE);
                break;
            case 1: // stop loading
                binding.favProgressbar.setVisibility(View.INVISIBLE);
                binding.rvFavUsers.setVisibility(View.VISIBLE);
                binding.tvFavStatus.setVisibility(View.INVISIBLE);
                break;
            case 2: // message
                binding.favProgressbar.setVisibility(View.INVISIBLE);
                binding.rvFavUsers.setVisibility(View.INVISIBLE);
                binding.tvFavStatus.setVisibility(View.VISIBLE);
                binding.tvFavStatus.setText(message);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userHelper.close();
    }

    private static class LoadUsersAsync extends AsyncTask<Void, Void, ArrayList<User>> {
        private final WeakReference<UserHelper> weakUserHelper;
        private final WeakReference<LoadUsersCallback> weakCallback;

        private LoadUsersAsync(UserHelper userHelper, LoadUsersCallback callback) {
            weakUserHelper = new WeakReference<>(userHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<User> doInBackground(Void... voids) {
            Cursor dataCursor = weakUserHelper.get().queryAll();
            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);
            weakCallback.get().postExecute(users);
        }
    }
}