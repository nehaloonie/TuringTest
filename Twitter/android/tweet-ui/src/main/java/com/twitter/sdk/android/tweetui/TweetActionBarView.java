/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.tweetui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;

public class TweetActionBarView extends LinearLayout {
    final DependencyProvider dependencyProvider;
    ImageView replyImage;
    ToggleImageButton likeButton;
    ImageButton shareButton;
    TextView likeCountText;
    TextView retweetCountText;
    Callback<Tweet> actionCallback;
    ReplyClickListener replyClickListener;

    public TweetActionBarView(Context context) {
        this(context, null, new DependencyProvider());
    }

    public TweetActionBarView(Context context, AttributeSet attrs) {
        this(context, attrs, new DependencyProvider());
    }

    TweetActionBarView(Context context, AttributeSet attrs, DependencyProvider dependencyProvider) {
        super(context, attrs);
        this.dependencyProvider = dependencyProvider;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findSubviews();
    }

    /*
     * Sets the callback to call when a Tweet Action (favorite, unfavorite) is performed.
     */
    void setOnActionCallback(Callback<Tweet> actionCallback) {
        this.actionCallback = actionCallback;
    }
    /*
    * Sets the callback to call when a Tweet Action (reply) is performed.
    * */
    void setOnReplyClickListener(ReplyClickListener listener) {
        this.replyClickListener = listener;
    }
    void findSubviews() {
        replyImage = findViewById(R.id.tw__tweet_reply_image);
        likeButton = findViewById(R.id.tw__tweet_like_button);
        likeCountText = findViewById(R.id.tw__tweet_like_count);
        shareButton = findViewById(R.id.tw__tweet_share_button);
        retweetCountText = findViewById(R.id.tw__tweet_retweet_count);

    }

    /*
     * Setup action bar buttons with Tweet and action performer.
     * @param tweet Tweet source for whether an action has been performed (e.g. isFavorited?)
     */
    void setTweet(Tweet tweet) {
        setReply(tweet);
        setLike(tweet);
        setShare(tweet);
        setLikeCount(tweet);
        setRetweetCount(tweet);
    }
    void setReply(Tweet tweet) {
        if (tweet != null) {
            replyImage.setOnClickListener(view -> {
                replyClickListener.onReplyClicked(tweet);
            });
        }
    }
    void setLike(Tweet tweet) {
        final TweetUi tweetUi = dependencyProvider.getTweetUi();
        if (tweet != null) {
            likeButton.setToggledOn(tweet.favorited);
            final LikeTweetAction likeTweetAction = new LikeTweetAction(tweet,
                    tweetUi, actionCallback);
            likeButton.setOnClickListener(likeTweetAction);
        }
    }
    void setLikeCount(Tweet tweet) {
        if (tweet != null) {
            Integer count = tweet.favoriteCount;
            likeCountText.setText(String.valueOf(count));
        }
    }
    void setShare(Tweet tweet) {
        final TweetUi tweetUi = dependencyProvider.getTweetUi();
        if (tweet != null) {
            shareButton.setOnClickListener(new ShareTweetAction(tweet, tweetUi));
        }
    }
    void setRetweetCount(Tweet tweet) {
        if (tweet != null) {
            retweetCountText.setText(String.valueOf(tweet.retweetCount));
        }
    }

    /**
     * This is a mockable class that extracts our tight coupling with the TweetUi singleton.
     */
    static class DependencyProvider {
        /**
         * Return TweetRepository
         */
        TweetUi getTweetUi() {
            return TweetUi.getInstance();
        }
    }
}
