package michaellin.venture_10;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jimmy on 11/21/2017.
 */

@SuppressWarnings("ALL")
public class VentureEvent {

    public String title, address, datetime, description, user_id;
    public double latitude, longitude;
    public Bitmap image;

    public VentureEvent() {

    }

    public VentureEvent(String title, String address, String datetime, String description, double latitude, double longitude, String user_id) {
        this.title = title;
        this.address = address;
        this.datetime = datetime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.user_id = user_id;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("title", title);
        result.put("address", address);
        result.put("datetime", datetime);
        result.put("description", description);
        result.put("user_id", user_id);

        return result;
    }

}
