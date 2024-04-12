package com.example.sampleproject.wifi.util

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build

fun codigodocarala(
    ssid: String,
    password: String,
    context: Context,
    connectivityManager: ConnectivityManager
) {
    val wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager?

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        try {
            println("Wifi - connection wifi pre Q")
            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = "\"" + ssid + "\""
            wifiConfig.preSharedKey = "\"" + password + "\""
            val netId = wifiManager!!.addNetwork(wifiConfig)
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        println("Wifi - connection wifi  Q")

        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()


        val networkCallback2 = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                connectivityManager.bindProcessToNetwork(network)
                println("Wifi - onAvailable")
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                println("Wifi - onLosing")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                connectivityManager.bindProcessToNetwork(null)
                //  connectivityManager.unregisterNetworkCallback(null)
                println("Wifi - losing active connection")
            }

            override fun onUnavailable() {
                super.onUnavailable()
                println("Wifi - onUnavailable")
            }
        }
        connectivityManager.requestNetwork(networkRequest, networkCallback2)
    }
}