package com.hryzx.consumerapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.hryzx.consumerapp.adapter.SectionsPagerAdapter;
import com.hryzx.consumerapp.database.UserContract.UserColumns;
import com.hryzx.consumerapp.databinding.ActivityDetailBinding;
import com.hryzx.consumerapp.entity.User;
import com.hryzx.consumerapp.helper.MappingHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_USER = "extra_user";
    private ActivityDetailBinding binding;
    private boolean isFav = false;
    private Uri uriWithId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        user = getIntent().getParcelableExtra(EXTRA_USER);
        // content://com.hryzx.submissiongithubuser3/users/username
        uriWithId = Uri.withAppendedPath(UserColumns.CONTENT_URI, String.valueOf(user.getId()));
        Cursor cursor = getContentResolver().query(uriWithId, null, null, null, null);
        isFav = (cursor != null && cursor.getCount() > 0);

        if (isFav) {
            showLoading(true);
            user = MappingHelper.mapCursorToObject(cursor);
            Glide.with(this)
                    .load(user.getPhoto())
                    .apply(new RequestOptions().override(86, 86))
                    .into(binding.imgDetailPhoto);
            binding.tvDetailName.setText(user.getName()
                    .compareToIgnoreCase("null") == 0 ?
                    user.getUsername() : user.getName());
            binding.tvDetailUsername.setText(user.getUsername());
            setupTab(user);
            getSupportActionBar().setTitle(user.getUsername());
            showLoading(false);
            binding.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24));
            cursor.close();
        } else {
            getUserData();
            binding.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
        }

        binding.floatingActionButton.setOnClickListener(v -> {
            if (!isFav) {
                ContentValues values = new ContentValues();
                values.put(UserColumns.ID, user.getId());
                values.put(UserColumns.URL, user.getUrl());
                values.put(UserColumns.NAME, user.getName());
                values.put(UserColumns.USERNAME, user.getUsername());
                values.put(UserColumns.DESCRIPTION, user.getDescription());
                values.put(UserColumns.LOCATION, user.getLocation());
                values.put(UserColumns.PHOTO, user.getPhoto());
                values.put(UserColumns.FOLLOWING, user.getFollowing());
                values.put(UserColumns.FOLLOWERS, user.getFollowers());
                values.put(UserColumns.FOLLOWERS_URL, user.getFollowers_url());
                values.put(UserColumns.FOLLOWING_URL, user.getFollowing_url());
                values.put(UserColumns.REPOS, user.getRepos());
                values.put(UserColumns.COMPANY, user.getCompany());
                values.put(UserColumns.BLOG, user.getBlog());
                values.put(UserColumns.EMAIL, user.getEmail());

                // content://com.hryzx.submissiongithubuser3/users/
                getContentResolver().insert(UserColumns.CONTENT_URI, values);
                Snackbar.make(v, getString(R.string.added_to_favorites), Snackbar.LENGTH_SHORT).show();
                binding.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24));
            } else {
                // content://com.hryzx.submissiongithubuser3/users/
                getContentResolver().delete(uriWithId, null, null);
                Snackbar.make(v, getString(R.string.removed_from_favorites), Snackbar.LENGTH_SHORT).show();
                binding.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
            }
        });
    }

    private void getUserData() {
        AsyncHttpClient client = new AsyncHttpClient();

        showLoading(true);
        client.addHeader("Authorization", "token " + BuildConfig.GITHUB_TOKEN);
        client.addHeader("User-Agent", "request");
        client.get(user.getUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Jika koneksi berhasil

                String result = new String(responseBody);
                try {
                    JSONObject item = new JSONObject(result);
                    User userItem = new User();

                    userItem.setUrl(user.getUrl());
                    userItem.setId(item.getInt("id"));
                    userItem.setUsername(item.getString("login"));
                    userItem.setPhoto(item.getString("avatar_url"));
                    userItem.setFollowers_url(item.getString("followers_url"));
                    userItem.setFollowing_url(item.getString("url") + "/following");
                    userItem.setName(item.getString("name"));
                    userItem.setCompany(item.getString("company"));
                    userItem.setBlog(item.getString("blog"));
                    userItem.setLocation(item.getString("location"));
                    userItem.setEmail(item.getString("email"));
                    userItem.setDescription(item.getString("bio"));
                    userItem.setRepos(item.getString("public_repos"));
                    userItem.setFollowers(item.getString("followers"));
                    userItem.setFollowing(item.getString("following"));

                    Glide.with(DetailActivity.this)
                            .load(userItem.getPhoto())
                            .apply(new RequestOptions().override(86, 86))
                            .into(binding.imgDetailPhoto);
                    binding.tvDetailName.setText(userItem.getName()
                            .compareToIgnoreCase("null") == 0 ?
                            userItem.getUsername() : userItem.getName());
                    binding.tvDetailUsername.setText(userItem.getUsername());
                    setupTab(userItem);
                    user = userItem;
                    Objects.requireNonNull(DetailActivity.this.getSupportActionBar()).setTitle(userItem.getUsername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showLoading(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Jika koneksi gagal
                String errorMessage;
                switch (statusCode) {
                    case 401:
                        errorMessage = statusCode + " : Bad Request";
                        break;
                    case 403:
                        errorMessage = statusCode + " : Forbidden";
                        break;
                    case 404:
                        errorMessage = statusCode + " : Not Found";
                        break;
                    default:
                        errorMessage = statusCode + " : " + error.getMessage();
                        break;
                }
                Log.e("get user data", errorMessage);
                showLoading(false);
            }
        });
    }

    public void setupTab(User user) {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        sectionsPagerAdapter.infoUser = user;
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tab_detail);
        tabs.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabs.getTabAt(1)).getOrCreateBadge().setNumber(Integer.parseInt(user.getFollowers()));
        Objects.requireNonNull(tabs.getTabAt(2)).getOrCreateBadge().setNumber(Integer.parseInt(user.getFollowing()));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return true;
    }

    private void showLoading(boolean state) {
        binding.detailProgressbar.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        binding.imgDetailPhoto.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
        binding.tvDetailName.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
        binding.tvDetailUsername.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
        binding.tabDetail.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
        binding.viewLine.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
        binding.viewPager.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
        binding.floatingActionButton.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
    }
}