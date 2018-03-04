package id.tiregdev.testingapps;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by HVS on 05/03/18.
 */

public class beacon implements Parcelable {
    private String identifier;
    private UUID proximityUUID;
    private int major;
    private int minor;

    public beacon() {

    }

    public beacon(String identifier, int major, int minor, UUID proximityUUID) {
        this.identifier = identifier;
        this.major = major;
        this.minor = minor;
        this.proximityUUID = proximityUUID;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public UUID getProximityUUID() {
        return proximityUUID;
    }

    public void setProximityUUID(UUID proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeSerializable(this.proximityUUID);
        dest.writeInt(this.major);
        dest.writeInt(this.minor);
    }

    protected beacon(Parcel in) {
        this.identifier = in.readString();
        this.proximityUUID = (UUID) in.readSerializable();
        this.major = in.readInt();
        this.minor = in.readInt();
    }

    public static final Parcelable.Creator<beacon> CREATOR = new Parcelable.Creator<beacon>() {
        @Override
        public beacon createFromParcel(Parcel source) {
            return new beacon(source);
        }

        @Override
        public beacon[] newArray(int size) {
            return new beacon[size];
        }
    };
}