package com.example.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.SampleApplication;
import com.example.app.config.UserTimelineSetting;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.ReplyClickListener;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetViewClickListener;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.lang.ref.WeakReference;

public class TimelineActivity extends AppCompatActivity implements TweetViewClickListener, ReplyClickListener{
    private static final String EXTRA_TWEET = "EXTRA_TWEET";

    final WeakReference<Activity> activityRef = new WeakReference<>(TimelineActivity.this);

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.refresh_timeline_title);
        }

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        // launch the app login activity when a guest user tries to favorite a TweetData
        final Callback<Tweet> actionCallback = new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                // Intentionally blank
                spinner.setVisibility(View.GONE);
            }
            @Override
            public void failure(TwitterException exception) {
                if (exception instanceof TwitterAuthException) {
                    startActivity(TwitterConnectActivity.newIntent(TimelineActivity.this));
                }
            }
        };
        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swipe_layout);
        final View emptyView = findViewById(android.R.id.empty);
        final ListView listView = findViewById(android.R.id.list);
        listView.setEmptyView(emptyView);

        final UserTimeline userTimeline = new UserTimeline.Builder().screenName(UserTimelineSetting.screenName).build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .setOnActionCallback(actionCallback)
                .setReplyClickListener(this)
                .setTweetViewClickListener(this)
                .build();
        listView.setAdapter(adapter);

        swipeLayout.setColorSchemeResources(R.color.twitter_blue, R.color.twitter_dark);

        // set custom scroll listener to enable swipe refresh layout only when at list top
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean enableRefresh = false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (listView != null && listView.getChildCount() > 0) {
                    // check that the first item is visible and that its top matches the parent
                    enableRefresh = listView.getFirstVisiblePosition() == 0 &&
                            listView.getChildAt(0).getTop() >= 0;
                } else {
                    enableRefresh = false;
                }
                swipeLayout.setEnabled(enableRefresh);
            }
        });

        // specify action to take on swipe refresh
        swipeLayout.setOnRefreshListener(() -> {
            swipeLayout.setRefreshing(true);
            adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                @Override
                public void success(Result<TimelineResult<Tweet>> result) {
                    swipeLayout.setRefreshing(false);
                }

                @Override
                public void failure(TwitterException exception) {
                    swipeLayout.setRefreshing(false);
                    final Activity activity = activityRef.get();
                    if (activity != null && !activity.isFinishing()) {
                        Toast.makeText(activity, exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    public void onReplyClicked(Tweet tweet) {
//        Tweet displayTweet = getDisplayTweet(tweet);
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
//        String text = "";
//        if(displayTweet != tweet) {
//            text = "Replying to " + formatScreenName(displayTweet.user.screenName) + " and " + formatScreenName(tweet.user.screenName);
//        }
        final Intent intent = new ComposerActivity.Builder(this)
                .session(session)
                .replyId(tweet.id)
                .createIntent();
        startActivity(intent);
    }
    Tweet getDisplayTweet(Tweet tweet) {
        if (tweet == null || tweet.retweetedStatus == null) {
            return tweet;
        } else {
            return tweet.retweetedStatus;
        }
    }
    public CharSequence formatScreenName(CharSequence screenName) {
        if (TextUtils.isEmpty(screenName)) {
            return "";
        }

        if (screenName.charAt(0) == '@') {
            return screenName;
        }
        return "@" + screenName;
    }

    @Override
    public void onTweetViewClick(Tweet tweet) {
        if (tweet != null) {
            SampleApplication app = (SampleApplication) getApplication();
            app.setTweet(tweet);
            Intent intent = new Intent(TimelineActivity.this, ReplyActivity.class);
            startActivity(intent);
        }
    }
}
