package ru.nedan.util;

import com.google.gson.JsonObject;

public interface Configure {

    void addConfig(JsonObject jsonObject);
    void readFromConfig(JsonObject jsonObject);

}
