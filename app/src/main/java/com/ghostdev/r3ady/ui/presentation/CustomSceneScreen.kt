package com.ghostdev.r3ady.ui.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ghostdev.r3ady.R
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberOnGestureListener
import java.io.IOException
import java.nio.ByteBuffer

@Composable
fun CustomSceneScreen(navController: NavController, modelUri: Uri?) {
    val context = LocalContext.current
    var modelScale by remember { mutableFloatStateOf(1.5f) }

    Scaffold { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val environmentLoader = rememberEnvironmentLoader(engine)
            val centerNode = rememberNode(engine)

            val cameraNode = rememberCameraNode(engine) {
                position = Position(y = 0.5f, z = 4f)
                lookAt(centerNode)
                centerNode.addChildNode(this)
            }

            val modelNode = modelUri?.let { uri ->
                try {
                    val modelBuffer = uriToByteBuffer(context, uri)
                    modelBuffer?.let { buffer ->
                        ModelNode(
                            modelInstance = modelLoader.createModelInstance(buffer),
                            scaleToUnits = null
                        ).apply {
                            position = Position(0.0f, -0.5f, 0.0f)
                            rotation = Position(0.0f, 180f, 0.0f)
                            scale = Position(modelScale, modelScale, modelScale)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CustomSceneScreen", "Failed to load model: ${e.message}", e)
                    null
                }
            }

            LaunchedEffect(modelScale) {
                modelNode?.scale = Position(modelScale, modelScale, modelScale)
            }

            Scene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                cameraNode = cameraNode,
                cameraManipulator = rememberCameraManipulator(
                    orbitHomePosition = cameraNode.worldPosition,
                    targetPosition = centerNode.worldPosition
                ),
                childNodes = listOfNotNull(centerNode, modelNode),
                environment = environmentLoader.createHDREnvironment(
                    assetFileLocation = "env/sky_2k.hdr"
                )!!,
                onFrame = { cameraNode.lookAt(centerNode) },
                onGestureListener = rememberOnGestureListener(
                    onDoubleTap = { _, node ->
                        if (node != null) {
                            modelScale *= 1.5f
                        }
                    },
                    onScale = { _, _, _ ->
                        modelScale *= 1.5f
                        modelScale = modelScale.coerceIn(0.1f, 10.0f)
                    }
                )
            )

            Card(
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .wrapContentSize(),
                shape = CircleShape
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        modifier = Modifier.padding(4.dp),
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Go back"
                    )
                }
            }

            Card(
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .wrapContentSize(),
            ) {
                Slider(
                    value = modelScale,
                    onValueChange = { modelScale = it },
                    valueRange = 0.1f..5.0f,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        .width(200.dp)
                )
            }
        }
    }
}

fun uriToByteBuffer(context: Context, uri: Uri): ByteBuffer? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            ByteBuffer.allocateDirect(bytes.size).apply {
                put(bytes)
                rewind()
            }
        }
    } catch (e: IOException) {
        Log.e("uriToByteBuffer", "Error reading model file: ${e.message}", e)
        null
    }
}
