package com.example.sampleproject.wifi.util

import android.net.wifi.WifiManager

fun wifiIsEnabled(wifiManager: WifiManager?): Boolean {
    return if (wifiManager?.isWifiEnabled == true) {
        println("wifi is enabled")
        true
    } else {
        println("wifi isn't enabled")
        false
    }
}