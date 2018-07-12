package com.example.app.service;

import android.util.Log;
import com.example.app.config.UserTimelineSetting;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TwitterService {

    private static final String TAG = "TwitterAPI";

    private static TwitterService instance = null;

    private TwitterAuthToken authToken;
    private TwitterApiClient apiClient;

    private static String userName;

    private static void init() {
        instance = new TwitterService();
    }
    public static TwitterService instance() {
        if (instance == null)
            init();
        return instance;
    }
    TwitterService() {
    }
    public void setUser(String userName, long userID) {
        Log.d(TAG, "userName: " + userName + " , userID" + userID);
        this.userName = userName;
        initApiClient();
    }

    public static String getUserName() {
        return userName;
    }

    private void initApiClient() {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        final OkHttpClient customClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor).build();

        TwitterSession activeSession = TwitterCore.getInstance()
                .getSessionManager().getActiveSession();

        if (activeSession != null) {
            apiClient = new TwitterApiClient(activeSession, customClient);
            TwitterCore.getInstance().addApiClient(activeSession, apiClient);
            this.authToken = activeSession.getAuthToken();
        } else {
            apiClient = new TwitterApiClient(customClient);
            TwitterCore.getInstance().addGuestApiClient(apiClient);
        }
    }
    public void fetchTweets(Long sinceId) {
        Call<List<Tweet>> call = apiClient.getStatusesService().userTimeline(UserTimelineSetting.userId, UserTimelineSetting.screenName, UserTimelineSetting.maxItemsPerRequest, sinceId, UserTimelineSetting.maxID, UserTimelineSetting.trimUser, !UserTimelineSetting.includeReplies, UserTimelineSetting.contributeDetails, UserTimelineSetting.includeRetweets);
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
            }
            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
            }
        });
    }
}
