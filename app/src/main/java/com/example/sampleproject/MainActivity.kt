package com.example.sampleproject

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sampleproject.ui.theme.SampleProjectTheme
import com.example.sampleproject.wifi.WifiReceiverAndConnect


class MainActivity : ComponentActivity() {

    private val wifiManager by lazy {
        this.applicationContext?.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    }

    private val wifiReceiver = WifiReceiverAndConnect()

    val connectivityManager by lazy {
        this.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SampleProjectTheme {

                val isWifiEnabled by wifiReceiver.isWifiEnabled.collectAsState()

                val hasSomeConnecting by wifiReceiver.hasSomeConnecting.collectAsState()

                Column(
                    modifier = Modifier
                        .padding(PaddingValues(16.dp))
                        .fillMaxSize()
                        .imePadding()
                        .statusBarsPadding()
                        .verticalScroll(rememberScrollState()),
                    //   verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Ajude um desenvolvedor Android a validar uma task <3 <3",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Vamos tentar conectar 'automaticamente' na sua rede de wifi. \n" +
                                "Esqueça a sua rede conectada nesse momento e desligue o wi-fi \n" +
                                "Se tudo der certo, este app vai fazer isso junto com você! \n" +
                                "Prometo não tem nenhum hack ou pegadinha <3",
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    var wifiName by rememberSaveable { mutableStateOf("") }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = wifiName,
                        onValueChange = { wifiName = it },
                        label = {
                            Text(text = "Nome do seu Wifi")
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    var password by rememberSaveable { mutableStateOf("") }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(text = "Wifi password")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isWifiEnabled) {

                        Text(text = "Seu Wifi está ativo.")

                        Spacer(modifier = Modifier.height(16.dp))

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    this@MainActivity.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
                                }
                            ) {
                                Text(text = "Ativar wifi")
                            }
                        } else {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    val result = wifiManager?.setWifiEnabled(true)
                                    println("Wifi - result from enabled wifi: $result")
                                }
                            ) {
                                Text(text = "Ativar wifi")
                            }
                        }
                    }

                    if (password.isBlank() && wifiName.isBlank()) {

                        Text(
                            text = "Adicione o nome do wifi e senha para continuar..",
                            modifier = Modifier.padding(16.dp)
                        )

                    } else {

                        if (hasSomeConnecting == wifiName) {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(text = "Você está conectado a $wifiName")
                            }

                        } else {

                            FilledTonalButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    // connectToWifi("Joinin", "#wearejoinin!", this@MainActivity, connectivityManager)

                                    //  anotherteste(this@MainActivity, wifiName, password)

                                }
                            ) {
                                Text(text = "Conectar a rede")
                            }
                        }
                    }

                }

            }
        }

    }

    override fun onResume() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(wifiReceiver, filter)

        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiReceiver)
    }

}