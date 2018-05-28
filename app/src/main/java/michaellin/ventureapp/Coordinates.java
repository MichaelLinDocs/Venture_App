package michaellin.ventureapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Coordinates implements Parcelable{
    double latitude, longitude;
    String title;

    Coordinates(double latitude, double longitude, String title)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }

    private Coordinates(Parcel in)
    {
        latitude = in.readDouble();
        longitude = in.readDouble();
        title = in.readString();
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(title);
    }

    public static final Parcelable.Creator<Coordinates> CREATOR = new Parcelable.Creator<Coordinates>() {
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };
}
