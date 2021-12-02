package com.ubtrobot.mini.sdkdemo.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonTool {
    private final int numbers;
    private final List<DataEle> data = new ArrayList<>();

    public JsonTool(String ans) throws JSONException {
        JSONObject baseJsonObject = new JSONObject(ans);
        this.numbers = (Integer) baseJsonObject.get("numbers");
        if (numbers > 0) {
            JSONArray array = (JSONArray) baseJsonObject.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String name = jsonObject.getString("name");
                String type = jsonObject.getString("type");
                data.add(new DataEle(name, type));
            }
        }
    }


    public int getNumbers() {
        return numbers;
    }

    public List<DataEle> getData() {
        return data;
    }

    public class DataEle {
        String name;
        String type;

        DataEle(String name, String type) {
            this.name = name;
            this.type = type;

        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
