package com.RNMixpanel;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class RNMixpanelManager extends ReactContextBaseJavaModule {
    /**
     * The name of the react module.
     */
    public static final String REACT_CLASS = "RNMixpanel";

    /**
     * The application context.
     */
    ReactApplicationContext reactContext;

    /**
     * Instance of Mixpanel API.
     */
    MixpanelAPI mixpanel;

    /**
     * Creates a new device info manager class.
     *
     * @param reactContext
     */
    public RNMixpanelManager(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    /**
     * Returns the name of the react module.
     *
     * @return String
     */
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /**
     * Get shared instance.
     *
     * @param apiToken
     */
    @ReactMethod
    public void sharedInstanceWithToken(String apiToken){
      this.mixpanel = MixpanelAPI.getInstance(this.reactContext, apiToken);
    }

    /**
     * Tracks a Mixpanel event.
     *
     * @param event
     * @param properties
     */
    @ReactMethod
    public void track(String event) {
      mixpanel.track(event);
    }

    /**
     * Tracks a Mixpanel event with properties. (for compatibilty with iOS version)
     *
     * @param event
     * @param properties
     */
    @ReactMethod
    public void trackWithProperties(String event, ReadableMap properties) {
      mixpanel.track(event, this.readableMapToJson(properties));
    }

    /**
     * Alias an old Mixpanel distinct id w/ a new email.
     *
     * @param old_id
     */
    @ReactMethod
    public void getDistinctId(String old_id) {
      mixpanel.People.getDistinctId();
    }

    /**
     * Returns the user's distinct id.
     *
     * @param old_id
     */
    @ReactMethod
    public void alias(String old_id) {
      mixpanel.alias(old_id, null);
    }

    /**
     * Starts timing a Mixpanel event. PPTMixpanelManager.track() must be called once the event
     * you're timing is complete.
     *
     * @param event
     */
    @ReactMethod
    public void timeEvent(String event){
        mixpanel.timeEvent(event);
    }

    /**
     * Registers event super properties which are sent with every subsequent event.
     *
     * @param properties
     */
    @ReactMethod
    public void registerSuperProperties(ReadableMap properties) {
        mixpanel.registerSuperProperties(this.readableMapToJson(properties));
    }

    /**
     * Registers event super properties which are sent with every subsequent event once.
     *
     * @param properties
     */
    @ReactMethod
    public void registerSuperPropertiesOnce(ReadableMap properties) {
        mixpanel.registerSuperPropertiesOnce(this.readableMapToJson(properties));
    }

    /**
     * Sets a user ID to identify events by.
     *
     * @param id
     */
    @ReactMethod
    public void identify(String id) {
        mixpanel.identify(id);
    }

    /**
     * Sets properties for the identified user.
     *
     * @param properties
     */
    @ReactMethod
    public void peopleSet(ReadableMap properties) {
        mixpanel.getPeople().set(this.readableMapToJson(properties));
    }

    /**
     * Increments properties for the identified user.
     *
     * @param properties
     */
    @ReactMethod
    public void peopleIncrement(ReadableMap properties) {
        mixpanel.getPeople().increment(this.readableMapToNumberMap(properties));
    }

    /**
     * Tracks a charge for the identified user.
     *
     * @param charge
     */
    @ReactMethod
    public void peopleTrackCharge(double charge) {
        mixpanel.getPeople().trackCharge(charge, null);
    }

    /**
     * Tracks a charge (with properties) for the identified user.
     *
     * @param charge
     * @param properties
     */
    @ReactMethod
    public void peopleTrackChargeWithProperties(double charge, ReadableMap properties) {
        mixpanel.getPeople().trackCharge(charge, this.readableMapToJson(properties));
    }

    /**
     * Converts a react native readable map into a JSON object.
     *
     * @param readableMap
     * @return
     */
    @Nullable
    private JSONObject readableMapToJson(ReadableMap readableMap) {
        JSONObject jsonObject = new JSONObject();

        if (readableMap == null) {
            return null;
        }

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        if (!iterator.hasNextKey()) {
            return null;
        }

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType readableType = readableMap.getType(key);

            try {
                switch (readableType) {
                    case Null:
                        jsonObject.put(key, null);
                        break;
                    case Boolean:
                        jsonObject.put(key, readableMap.getBoolean(key));
                        break;
                    case Number:
                        // Can be int or double.
                        jsonObject.put(key, readableMap.getDouble(key));
                        break;
                    case String:
                        jsonObject.put(key, readableMap.getString(key));
                        break;
                    case Map:
                        jsonObject.put(key, this.readableMapToJson(readableMap.getMap(key)));
                        break;
                    case Array:
                        jsonObject.put(key, readableMap.getArray(key));
                    default:
                        // Do nothing and fail silently
                }
            } catch (JSONException ex) {
                // Do nothing and fail silently
            }
        }

        return jsonObject;
    }

    /**
     * Converts a readable map of numbers into a native map of numbers.
     *
     * @param readableMap
     * @return
     */
    @Nullable
    private Map<String, Number> readableMapToNumberMap(ReadableMap readableMap) {
        Map<String, Number> map = new HashMap<>();

        if (readableMap == null) {
            return null;
        }

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        if (!iterator.hasNextKey()) {
            return null;
        }

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType readableType = readableMap.getType(key);

            switch (readableType) {
                case Number:
                    // Can be int or double.
                    map.put(key, readableMap.getDouble(key));
                    break;
                default:
                    // Do nothing and fail silently
            }
        }

        return map;
    }
}
