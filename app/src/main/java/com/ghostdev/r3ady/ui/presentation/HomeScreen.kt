package com.ghostdev.r3ady.ui.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ghostdev.r3ady.navigation.NavDestinations

@Composable
fun HomeScreen(
    navController: NavController
) {
    val pickModelLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                val encodedUri = Uri.encode(it.toString())
                navController.navigate("${NavDestinations.CustomViewer}/$encodedUri")
            }
        }
    )

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "R3Dy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(24.dp)
                )

                Text(
                    text = "Load a model",
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(
                    modifier = Modifier
                        .height(24.dp)
                )

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, Color(0xFF374151)),
                    onClick = {
                        pickModelLauncher.launch(arrayOf("*/*"))
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = "Open a model file(*.glb)"
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(18.dp)
                )

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, Color(0xFF374151)),
                    onClick = {
                        navController.navigate(NavDestinations.DemoViewer.toString())
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = "Load demo scene :)"
                    )
                }
            }
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                text = "Created with ❤️ by Adrian Ikeaba",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}