package com.adm.e09_login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding


import androidx.compose.material3.Surface

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.adm.e09_login.navigation.Navigation
import com.adm.e09_login.ui.theme.BackgroundColor


import com.adm.e09_login.ui.theme.E09LoginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            E09LoginTheme {
                Surface(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 46.dp),
                        color = BackgroundColor
                )
                {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    )
                    {
                        Navigation()
                    }
                }
            }
        }
    }
}


