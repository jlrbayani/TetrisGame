package main.persistence;

import org.json.JSONObject;

// an interface that allows an object that implements this to be turned into a JSON object
public interface Writable {

    // returns this as a JSON object
    JSONObject toJson();
}
