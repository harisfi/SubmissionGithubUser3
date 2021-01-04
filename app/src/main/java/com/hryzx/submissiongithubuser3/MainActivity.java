package com.hryzx.submissiongithubuser3;

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

import com.hryzx.submissiongithubuser3.adapter.ListUserAdapter;
import com.hryzx.submissiongithubuser3.databinding.ActivityMainBinding;
import com.hryzx.submissiongithubuser3.model.MainViewModel;

import java.util.Objects;

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

        Objects.requireNonNull(getSupportActionBar()).setTitle("GitHub User");

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