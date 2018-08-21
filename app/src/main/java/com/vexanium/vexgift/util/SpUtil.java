package com.vexanium.vexgift.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.socks.library.KLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SpUtil {
    private static SpUtil singleton;

    private SharedPreferences mPreferences;
    private HashMap<String, Object> mCfgMap;


    public SpUtil(Context context) {
        mPreferences = context.getSharedPreferences("PrefName", Context.MODE_PRIVATE);
        mCfgMap = new HashMap<>();
    }

    public static SpUtil getInstance(Context context) {
        if (singleton == null) {
            singleton = new SpUtil(context);
        }
        return singleton;
    }

    public static void put(String key, Object value) {
        try {
            singleton.mCfgMap.put(key, value);
            singleton.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remove(String key) {
        try {
            singleton.mCfgMap.remove(key);
            singleton.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeAll() {
        try {
            SharedPreferences.Editor editor = singleton.mPreferences.edit();
            singleton.mCfgMap.clear();
            editor.clear();
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object get(String key) {
        try {
            return singleton.mCfgMap.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean has(String key) {
        return singleton.mCfgMap.containsKey(key);
    }

    public static String getString(String key) {
        String str = (String) get(key);
        if (str == null)
            str = "";
        return str;
    }

    public static Set<String> getStringSet(String key) {
        Set<String> strSet = (Set<String>) get(key);
        if (strSet == null)
            strSet = new HashSet<>();
        return strSet;
    }

    public static long getLong(String key) {
        Long value = (Long) get(key);
        if (value != null) {
            return value;
        }
        return 0;
    }

    public static float getFloat(String key) {
        Float value = (Float) get(key);
        if (value != null) {
            return value;
        }
        return 0;
    }

    public static double getDouble(String key) {
        Double value = (Double) get(key);
        if (value != null) {
            return value;
        }
        return 0;
    }

    public static int getInt(String key) {
        Integer value = (Integer) get(key);
        if (value != null) {
            return value;
        }
        return 0;
    }

    public static boolean getBoolean(String key) {
        Boolean value = (Boolean) get(key);
        if (value != null)
            return (Boolean) get(key);
        return false;
    }

    public static void putMap(String key, Map<String, String> inputMap) {
        JSONObject jsonObject = new JSONObject(inputMap);
        String jsonString = jsonObject.toString();
        put(key, jsonString);
    }

    public static Map<String, String> getMap(String key) {
        Map<String, String> outputMap = new HashMap<>();
        try {
            String jsonString = getString(key);
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String k = keysItr.next();
                String v = (String) jsonObject.get(k);
                outputMap.put(k, v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }

    public void load() {
        mCfgMap.putAll(mPreferences.getAll());
        KLog.v("SharedPref Load");
    }

    public void save() {
        SharedPreferences.Editor edit = mPreferences.edit();

        for (String key : mCfgMap.keySet()) {
            Object value = mCfgMap.get(key);

            if (value instanceof Boolean) {
                edit.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                edit.putInt(key, (Integer) value);
            } else if (value instanceof Float) {
                edit.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                edit.putLong(key, (Long) value);
            } else if (value instanceof String) {
                edit.putString(key, String.valueOf(value));
            } else if (value instanceof Set<?>) {
                edit.putStringSet(key, (Set<String>) value);
            }
        }

        edit.apply();
    }
}
