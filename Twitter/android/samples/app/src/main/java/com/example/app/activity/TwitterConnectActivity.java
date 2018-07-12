package com.example.app.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.service.TwitterService;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class TwitterConnectActivity extends AppCompatActivity {
    private static final String TAG = TwitterConnectActivity.class.toString();

    TwitterLoginButton loginButton;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, TwitterConnectActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize Twitter SDK
        Twitter.initialize(this);
        setContentView(R.layout.activity_connect);
        initializeTwitterSDK();

        loginButton = findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                getUser(result);
                startActivity(new Intent(TwitterConnectActivity.this, TimelineActivity.class));
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(TwitterConnectActivity.this, "Failed to connect!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeTwitterSDK() {
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(
                        getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }
    private void getUser(Result<TwitterSession> result) {
        String userName = result.data.getUserName();
        long userID = result.data.getUserId();
        TwitterService.instance().setUser(userName, userID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
