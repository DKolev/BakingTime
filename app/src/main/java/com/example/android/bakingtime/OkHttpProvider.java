package com.example.android.bakingtime;

import okhttp3.OkHttpClient;

/**
 * Code snippet from here: https://github.com/chiuki/espresso-samples/tree/master/idling-resource-okhttp
 */

public abstract class OkHttpProvider {
    private static OkHttpClient instance = null;

    public static OkHttpClient getOkHttpInstance() {
        if (instance == null) {
            instance = new OkHttpClient();
        }
        return instance;
    }
}
