package com.example.cse110.Controller;

import android.os.Parcel;
import android.os.Parcelable;

public class Settings implements Parcelable {
    private boolean enableNotifications;

    /**
     * Default constructor with initial values only for testing.
     */
    public Settings() {
        enableNotifications = true;
    }

    protected Settings(Parcel in) {
        enableNotifications = in.readByte() != 0;
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (enableNotifications ? 1 : 0));
    }

    public void setEnableNotifications(boolean enabled) {
        enableNotifications = enabled;
    }

    public boolean getEnableNotifications() {
        return enableNotifications;
    }
}
