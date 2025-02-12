package com.hryzx.consumerapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hryzx.consumerapp.adapter.ListUserAdapter;
import com.hryzx.consumerapp.databinding.ActivityMainBinding;
import com.hryzx.consumerapp.model.MainViewModel;

import java.util.Objects;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class MainActivity extends AppCompatActivity {
    private ListUserAdapter listUserAdapter;
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Consumer GitHub User");

        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));
        listUserAdapter = new ListUserAdapter();
        listUserAdapter.notifyDataSetChanged();
        binding.rvUsers.setAdapter(listUserAdapter);
        binding.rvUsers.setHasFixedSize(true);

        mainViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);

        if (username == null || username.isEmpty())
            setStatus(2, getResources().getString(R.string.start_search));

        mainViewModel.getUsers().observe(this, users -> {
            if (users != null) {
                listUserAdapter.setData(users);
                setStatus(1, null);
            }
        });

        listUserAdapter.setOnItemClickCallback(data -> {
            Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
            detailIntent.putExtra(DetailActivity.EXTRA_USER, data);
            detailIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(detailIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchManager != null) {
            SearchView searchView = (SearchView) (menu.findItem(R.id.search)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    setStatus(0, null);
                    username = query;
                    mainViewModel.getListUsers("https://api.github.com/search/users?q=" + query);
                    listUserAdapter.notifyDataSetChanged();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty())
                        setStatus(2, getResources().getString(R.string.start_search));
                    else {
                        setStatus(0, null);
                        username = newText;
                        mainViewModel.getListUsers("https://api.github.com/search/users?q=" + newText);
                        listUserAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorites) {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setStatus(int status, String message) {
        switch (status) {
            case 0: // loading
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.rvUsers.setVisibility(View.INVISIBLE);
                binding.tvStatus.setVisibility(View.INVISIBLE);
                break;
            case 1: // stop loading
                binding.progressbar.setVisibility(View.INVISIBLE);
                binding.rvUsers.setVisibility(View.VISIBLE);
                binding.tvStatus.setVisibility(View.INVISIBLE);
                break;
            case 2: // message
                binding.progressbar.setVisibility(View.INVISIBLE);
                binding.rvUsers.setVisibility(View.INVISIBLE);
                binding.tvStatus.setVisibility(View.VISIBLE);
                binding.tvStatus.setText(message);
                break;
        }
    }
}