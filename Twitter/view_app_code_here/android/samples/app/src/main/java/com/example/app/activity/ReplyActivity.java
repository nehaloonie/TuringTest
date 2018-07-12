package com.example.app.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.app.R;
import com.example.app.SampleApplication;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetui.BaseTweetView;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.ReplyClickListener;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.Arrays;
import java.util.List;

import static com.twitter.sdk.android.core.internal.UserUtils.formatScreenName;

public class ReplyActivity extends TweetUiActivity implements ReplyClickListener{

    Tweet tweet;

    @Override
    int getLayout() {
        return R.layout.activity_frame;
    }

    @Override
    Fragment createFragment() {
        TweetsFragment fragment =  TweetsFragment.newInstance();
        SampleApplication app = (SampleApplication) getApplication();
        tweet = app.getTweet();
        fragment.setTweet(tweet);
        fragment.setReplyActivity(this);
        return  fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    Tweet getDisplayTweet(Tweet tweet) {
        if (tweet == null || tweet.retweetedStatus == null) {
            return tweet;
        } else {
            return tweet.retweetedStatus;
        }
    }
    @Override
    public void onReplyClicked(Tweet tweet) {

        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(this)
                .session(session)
                .replyId(tweet.id)
                .createIntent();
        startActivity(intent);
    }

    public static class TweetsFragment extends Fragment {
        Tweet tweet;
        ReplyActivity activity;
        // launch the app login activity when a guest user tries to favorite a Tweet
        final Callback<Tweet> actionCallback = new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                // Intentionally blank
            }
            @Override
            public void failure(TwitterException exception) {
                if (exception instanceof TwitterAuthException) {
                    startActivity(TwitterConnectActivity.newIntent(getActivity()));
                }
            }
        };

        public static TweetsFragment newInstance() {
            return new TweetsFragment();
        }
        public void setTweet(Tweet tweet) {
            this.tweet = tweet;
        }
        public void setReplyActivity(ReplyActivity activity) {
            this.activity = activity;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.tweetui_fragment_tweet, container, false);

            final ViewGroup tweetRegion = v.findViewById(R.id.tweet_region);
            final BaseTweetView tv = new TweetView(getActivity(), this.tweet,
                    R.style.tw__TweetLightWithActionsStyle);
            tv.setOnActionCallback(actionCallback);
            tv.setId(R.id.jack_compact_tweet);
            tv.setReplyClickListener(this.activity);
            tweetRegion.addView(tv);
            return v;
        }
    }
}
