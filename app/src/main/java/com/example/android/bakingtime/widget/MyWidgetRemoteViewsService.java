package com.example.android.bakingtime.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Kolev on 21-Jul-17.
 */

public class MyWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
