package com.example.myapplication.utils;

import com.example.myapplication.security.DataStoreManager;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class GlobalUtility {


    public GlobalUtility() {
    }

    /**
     * @param key        The key will pair to the data to be inserted in Data Store.
     * @param dm         The Data Store Class
     * @param value      The data to be inserted in data store.
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

    public String getLocalIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                for (InetAddress addr : java.util.Collections.list(iface.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

}
