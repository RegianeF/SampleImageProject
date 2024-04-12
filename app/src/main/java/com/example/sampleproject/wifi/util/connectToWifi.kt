package com.example.sampleproject.wifi.util

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.R)
fun connectToWifi(
    ssid: String,
    password: String,
    context: Context
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val suggestion = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val wifiManager = (context.applicationContext
            .getSystemService(WIFI_SERVICE) as WifiManager)

        wifiManager.removeNetworkSuggestions(mutableListOf(suggestion))
        when (wifiManager.addNetworkSuggestions(mutableListOf(suggestion))) {
            WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS -> {
                println("Cenoura : Networkadd ")
            }

            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE -> {
                println("Cenoura : Duplicate ")
            }

            else -> {
                println("Cenoura : Else ")
            }

        }
    } else {
        // For older versions, use the deprecated WifiConfiguration
        val config = WifiConfiguration()
        config.SSID = "\"your_network_ssid\""
        config.preSharedKey = "\"your_network_password\""
        val wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        wifiManager.addNetwork(config)
    }
}


fun testeMil(
    context: Context,
    connectivityManager: ConnectivityManager
) {
    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Create a specifier for the network to connect to
            val specifier = WifiNetworkSpecifier.Builder()
                .setSsid("your_network_ssid")
                .setWpa2Passphrase("your_network_password")
                .build()

            // Create a network request
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build()

            // Register a network callback to handle the connection
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    // Use the network for your connection
                    // For example, use Network.openConnection() to create a connection
                }

                override fun onLost(network: Network) {
                    // Handle the case when the network is lost
                }
            })
        } else {
            // For older versions, use the deprecated WifiConfiguration
            val config = WifiConfiguration()
            config.SSID = "Waleska"
            config.preSharedKey = "outubro2022"
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.addNetwork(config)
        }*/

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        // For older versions, use the deprecated WifiConfiguration
        val config = WifiConfiguration()
        config.SSID = "Waleska"
        config.preSharedKey = "outubro2022"
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.addNetwork(config)
    } else {
        println("Wifi - connection wifi  Q")

        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid("Waleska")
            .setWpa2Passphrase("outubro2022")
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()

        val networkCallback2 = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                //  connectivityManager.bindProcessToNetwork(network)
                println("Wifi - onAvailable")
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                println("Wifi - onLosing")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                //   connectivityManager.bindProcessToNetwork(null)
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

