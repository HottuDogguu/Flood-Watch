package com.example.myapplication.utils;

import com.example.myapplication.calbacks.global.OnComplete;
import com.example.myapplication.data.models.auth.LoginManualResponse;
import com.example.myapplication.security.DataStoreManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class GlobalUtility {


    public GlobalUtility() {
    }

    /**
     * @param key The key will pair to the data to be inserted in Data Store.
     * @param dm The Data Store Class
     * @param response This will contain the data to be inserted
     * @param onComplete To check if the data successfully inserted on DataStore

     */
    public void InsertDataToDataStore(String key,
                                         DataStoreManager dm,
                                         LoginManualResponse response,
                                         OnComplete onComplete){

        dm.saveDataFromJava(key, response.getToken().getAccess_token(), () -> {
            dm.getStringFromJava(key, s -> {
                boolean isSuccessful = (s != null && !s.isEmpty());
                onComplete.isSuccessful(isSuccessful);
                return null;
            });
            return null;
        });

    }
}
