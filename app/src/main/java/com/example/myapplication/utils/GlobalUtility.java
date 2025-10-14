package com.example.myapplication.utils;

import com.example.myapplication.security.DataStoreManager;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class GlobalUtility {


    public GlobalUtility() {
    }

    /**
     * @param key  The key will pair to the data to be inserted in Data Store.
     * @param dm   The Data Store Class
     * @param value The data to be inserted in data store.
     * @param onComplete To check wether the process is complete .
     */

    public void insertDataToDataStore(String key, DataStoreManager dm, String value, Runnable onComplete) {
        dm.saveDataFromJava(key, value, () -> {
            onComplete.run();
            return null;
        });
    }

    public void deleteDataToDataStore(String key, DataStoreManager dm) {
        dm.deleteKeyFromJava(key, () -> {
            return null;
        });
    }

    public void getDataFromDataStore(String key, DataStoreManager dm, Consumer<String> callback) {
        dm.getStringFromJava(key, data -> {
            callback.accept(data);
            return null;
        });
    }
}
