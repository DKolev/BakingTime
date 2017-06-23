package com.example.android.bakingtime.data.Steps;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kolev on 08-Jun-17.
 */

public class Steps implements Parcelable {

    private String id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;

    public Steps (String stepID, String stepShortDescription, String stepDescription,
                  String stepVideoURL, String stepThumbnailURL) {

        id = stepID;
        shortDescription = stepShortDescription;
        description = stepDescription;
        videoURL = stepVideoURL;
        thumbnailURL = stepThumbnailURL;
    }

    public String getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.shortDescription);
        dest.writeString(this.description);
        dest.writeString(this.videoURL);
        dest.writeString(this.thumbnailURL);
    }

    protected Steps(Parcel in) {
        this.id = in.readString();
        this.shortDescription = in.readString();
        this.description = in.readString();
        this.videoURL = in.readString();
        this.thumbnailURL = in.readString();
    }

    public static final Parcelable.Creator<Steps> CREATOR = new Parcelable.Creator<Steps>() {
        @Override
        public Steps createFromParcel(Parcel source) {
            return new Steps(source);
        }

        @Override
        public Steps[] newArray(int size) {
            return new Steps[size];
        }
    };
}
