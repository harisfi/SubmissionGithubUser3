package com.hryzx.submissiongithubuser3;

import android.content.ContentValues;
import android.database.Cursor;
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
import com.hryzx.submissiongithubuser3.adapter.SectionsPagerAdapter;
import com.hryzx.submissiongithubuser3.database.UserContract.UserColumns;
import com.hryzx.submissiongithubuser3.database.UserHelper;
import com.hryzx.submissiongithubuser3.databinding.ActivityDetailBinding;
import com.hryzx.submissiongithubuser3.entity.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.BLOG;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.COMPANY;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.DESCRIPTION;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.EMAIL;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.FOLLOWERS;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.FOLLOWERS_URL;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.FOLLOWING;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.FOLLOWING_URL;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.LOCATION;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.NAME;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.PHOTO;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.REPOS;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.URL;
import static com.hryzx.submissiongithubuser3.database.UserContract.UserColumns.USERNAME;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_USER = "extra_user";
    private ActivityDetailBinding binding;
    private User user;
    private UserHelper userHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        userHelper = UserHelper.getInstance(getApplicationContext());
        userHelper.open();

        user = getIntent().getParcelableExtra(EXTRA_USER);

        if (userHelper.isExist(user.getUsername())) {
            showLoading(true);
            Cursor c = userHelper.queryByUsername(user.getUsername());
            c.moveToFirst();

            user.setUrl(c.getString(c.getColumnIndexOrThrow(URL)));
            user.setName(c.getString(c.getColumnIndexOrThrow(NAME)));
            user.setUsername(c.getString(c.getColumnIndexOrThrow(USERNAME)));
            user.setDescription(c.getString(c.getColumnIndexOrThrow(DESCRIPTION)));
            user.setLocation(c.getString(c.getColumnIndexOrThrow(LOCATION)));
            user.setPhoto(c.getString(c.getColumnIndexOrThrow(PHOTO)));
            user.setFollowing(c.getString(c.getColumnIndexOrThrow(FOLLOWING)));
            user.setFollowers(c.getString(c.getColumnIndexOrThrow(FOLLOWERS)));
            user.setFollowers_url(c.getString(c.getColumnIndexOrThrow(FOLLOWERS_URL)));
            user.setFollowing_url(c.getString(c.getColumnIndexOrThrow(FOLLOWING_URL)));
            user.setRepos(c.getString(c.getColumnIndexOrThrow(REPOS)));
            user.setCompany(c.getString(c.getColumnIndexOrThrow(COMPANY)));
            user.setBlog(c.getString(c.getColumnIndexOrThrow(BLOG)));
            user.setEmail(c.getString(c.getColumnIndexOrThrow(EMAIL)));

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
            binding.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24));
            showLoading(false);
            Snackbar.make(binding.getRoot(), getString(R.string.offline_data), Snackbar.LENGTH_SHORT).show();
        } else {
            getUserData();
            binding.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
        }

        binding.floatingActionButton.setOnClickListener(v -> {
            if (!userHelper.isExist(user.getUsername())) {
                ContentValues values = new ContentValues();
                values.put(UserColumns.URL, user.getUrl());
                values.put(UserColumns.NAME, user.getName());
                values.put(USERNAME, user.getUsername());
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

                long result = userHelper.insert(values);
                if (result > 0) {
                    Snackbar.make(v, getString(R.string.added_to_favorites), Snackbar.LENGTH_SHORT).show();
                    binding.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24));
                } else {
                    Snackbar.make(v, getString(R.string.cannot_add_to_favorites), Snackbar.LENGTH_SHORT).show();
                }
            } else {
                long result = userHelper.deleteByUsername(user.getUsername());
                if (result > 0) {
                    Snackbar.make(v, getString(R.string.removed_from_favorites), Snackbar.LENGTH_SHORT).show();
                    binding.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
                } else {
                    Snackbar.make(v, getString(R.string.cannot_remove_from_favorites), Snackbar.LENGTH_SHORT).show();
                }
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
    }
}