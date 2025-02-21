package com.ghostdev.r3ady.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.ghostdev.r3ady.R
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberOnGestureListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun DemoScreen(
    navController: NavController
) {
    val bottomSheetIsVisible = remember { mutableStateOf(false) }

    val currentModel = remember { mutableStateOf("") }

    var currentPlayingModel by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentAnimationIndex1 by remember { mutableIntStateOf(2) }
    var currentAnimationIndex2 by remember { mutableIntStateOf(2) }
    var isCrossFading1 by remember { mutableStateOf(false) }
    var previousAnimationIndex1 by remember { mutableIntStateOf(2) }
    var previousAnimationTime1 by remember { mutableFloatStateOf(0f) }
    var isCrossFading2 by remember { mutableStateOf(false) }
    var previousAnimationIndex2 by remember { mutableIntStateOf(2) }
    var previousAnimationTime2 by remember { mutableFloatStateOf(0f) }
    var crossFadeStartTime1 by remember { mutableFloatStateOf(0f) }
    var crossFadeStartTime2 by remember { mutableFloatStateOf(0f) }
    var animationTime by remember { mutableFloatStateOf(0f) }
    val crossFadeDuration = 0.1f

    var playOnce by remember { mutableStateOf(false) }
    var animationStartTime by remember { mutableFloatStateOf(0f) }

    // Position and rotation states for models
    var model1Position by remember { mutableStateOf(Position(x = -0.2f, y = 0.0f, z = 0.0f)) }
    var model2Position by remember { mutableStateOf(Position(x = 0.2f, y = 0.0f, z = 0.0f)) }
    var model1Rotation by remember { mutableStateOf(Rotation(y = 90.0f)) }
    var model2Rotation by remember { mutableStateOf(Rotation(y = -90.0f)) }

    val model1 = remember { mutableStateOf<ModelNode?>(null) }
    val model2 = remember { mutableStateOf<ModelNode?>(null) }

    var isRotating1 by remember { mutableStateOf(false) }
    var isRotating2 by remember { mutableStateOf(false) }
    var targetRotation1 by remember { mutableStateOf(Rotation(y = 90.0f)) }
    var targetRotation2 by remember { mutableStateOf(Rotation(y = -90.0f)) }
    var rotationStartTime1 by remember { mutableFloatStateOf(0f) }
    var rotationStartTime2 by remember { mutableFloatStateOf(0f) }
    val rotationDuration = 0.5f

    var lastFrameTimeNanos by remember { mutableLongStateOf(System.nanoTime()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    fun playAnimationOnce(modelIndex: Int, animIndex: Int) {
        animationTime = 0f // Reset animation time

        if (modelIndex == 1) {
            previousAnimationIndex1 = currentAnimationIndex1
            previousAnimationTime1 = 0f
            currentAnimationIndex1 = animIndex
            isCrossFading1 = true
            crossFadeStartTime1 = 0f
        } else {
            previousAnimationIndex2 = currentAnimationIndex2
            previousAnimationTime2 = 0f
            currentAnimationIndex2 = animIndex
            isCrossFading2 = true
            crossFadeStartTime2 = 0f
        }

        playOnce = true
        currentPlayingModel = modelIndex
        animationStartTime = 0f
        isPlaying = true
    }

    fun smoothRotateModel(modelIndex: Int, targetY: Float) {
        if (modelIndex == 1) {
            targetRotation1 = Rotation(model1Rotation.x, targetY, model1Rotation.z)
            isRotating1 = true
            rotationStartTime1 = animationTime
        } else {
            targetRotation2 = Rotation(model2Rotation.x, targetY, model2Rotation.z)
            isRotating2 = true
            rotationStartTime2 = animationTime
        }
    }

    // Functions to move models
    fun moveModelForward(modelIndex: Int) {
        if (modelIndex == 1) {
            smoothRotateModel(1, 0f)
            model1Position = Position(model1Position.x, model1Position.y, model1Position.z + 0.1f)
            model1.value?.position = model1Position
        } else {
            smoothRotateModel(2, 0f)
            model2Position = Position(model2Position.x, model2Position.y, model2Position.z + 0.1f)
            model2.value?.position = model2Position
        }
    }

    fun moveModelBackward(modelIndex: Int) {
        if (modelIndex == 1) {
            smoothRotateModel(1, 180f)
            model1Position = Position(model1Position.x, model1Position.y, model1Position.z - 0.1f)
            model1.value?.position = model1Position
        } else {
            smoothRotateModel(2, 180f)
            model2Position = Position(model2Position.x, model2Position.y, model2Position.z - 0.1f)
            model2.value?.position = model2Position
        }
    }

    fun moveModelLeft(modelIndex: Int) {
        if (modelIndex == 1) {
            smoothRotateModel(1, 270f)
            model1Position = Position(model1Position.x - 0.1f, model1Position.y, model1Position.z)
            model1.value?.position = model1Position
        } else {
            smoothRotateModel(2, 270f)
            model2Position = Position(model2Position.x - 0.1f, model2Position.y, model2Position.z)
            model2.value?.position = model2Position
        }
    }

    fun moveModelRight(modelIndex: Int) {
        if (modelIndex == 1) {
            smoothRotateModel(1, 90f)
            model1Position = Position(model1Position.x + 0.1f, model1Position.y, model1Position.z)
            model1.value?.position = model1Position
        } else {
            smoothRotateModel(2, 90f)
            model2Position = Position(model2Position.x + 0.1f, model2Position.y, model2Position.z)
            model2.value?.position = model2Position
        }
    }

    fun resetModelPosition(modelIndex: Int) {
        if (modelIndex == 1) {
            model1Position = Position(x = -0.2f, y = 0.0f, z = 0.0f)
            model1Rotation = Rotation(y = 90.0f)
            model1.value?.position = model1Position
            model1.value?.rotation = model1Rotation
        } else {
            model2Position = Position(x = 0.2f, y = 0.0f, z = 0.0f)
            model2Rotation = Rotation(y = -90.0f)
            model2.value?.position = model2Position
            model2.value?.rotation = model2Rotation
        }
    }

    fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + fraction * (end - start)
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                val currentTimeNanos = System.nanoTime()
                val deltaTimeSeconds = (currentTimeNanos - lastFrameTimeNanos) / 1_000_000_000f
                lastFrameTimeNanos = currentTimeNanos

                animationTime += deltaTimeSeconds

                if (isRotating1) {
                    val rotationProgress =
                        ((animationTime - rotationStartTime1) / rotationDuration).coerceIn(0f, 1f)

                    val currentY = lerp(model1Rotation.y, targetRotation1.y, rotationProgress)
                    model1Rotation = Rotation(model1Rotation.x, currentY, model1Rotation.z)
                    model1.value?.rotation = model1Rotation

                    if (rotationProgress >= 1f) {
                        isRotating1 = false
                    }
                }

                if (isRotating2) {
                    val rotationProgress =
                        ((animationTime - rotationStartTime2) / rotationDuration).coerceIn(0f, 1f)
                    val currentY = lerp(model2Rotation.y, targetRotation2.y, rotationProgress)
                    model2Rotation = Rotation(model2Rotation.x, currentY, model2Rotation.z)
                    model2.value?.rotation = model2Rotation

                    if (rotationProgress >= 1f) {
                        isRotating2 = false
                    }
                }

                if (playOnce) {
                    val model = if (currentPlayingModel == 1) model1.value else model2.value
                    val currentAnimIndex =
                        if (currentPlayingModel == 1) currentAnimationIndex1 else currentAnimationIndex2

                    model?.animator?.let { animator ->
                        if (animator.animationCount > 0) {
                            val duration = animator.getAnimationDuration(currentAnimIndex)
                            val animProgress = animationTime - animationStartTime

                            if (animProgress >= duration) {
                                if (currentPlayingModel == 1) {
                                    previousAnimationIndex1 = currentAnimationIndex1
                                    previousAnimationTime1 = animationTime
                                    currentAnimationIndex1 = 2 // idle
                                    isCrossFading1 = true
                                    crossFadeStartTime1 = animationTime
                                } else {
                                    previousAnimationIndex2 = currentAnimationIndex2
                                    previousAnimationTime2 = animationTime
                                    currentAnimationIndex2 = 2 // idle
                                    isCrossFading2 = true
                                    crossFadeStartTime2 = animationTime
                                }
                                playOnce = false
                                currentPlayingModel = 0
                            }
                        }
                    }
                }

                model1.value?.animator?.apply {
                    if (animationCount > 0) {
                        if (isCrossFading1) {
                            val fadeProgress =
                                ((animationTime - crossFadeStartTime1) / crossFadeDuration).coerceIn(
                                    0f,
                                    1f
                                )
                            applyCrossFade(
                                previousAnimationIndex1,
                                previousAnimationTime1 + (animationTime - crossFadeStartTime1),
                                fadeProgress
                            )

                            updateBoneMatrices()

                            if (fadeProgress >= 1f) {
                                isCrossFading1 = false
                            }
                        } else {
                            applyAnimation(currentAnimationIndex1, animationTime)
                            updateBoneMatrices()
                        }
                    }
                }

                model2.value?.animator?.apply {
                    if (animationCount > 0) {
                        if (isCrossFading2) {
                            val fadeProgress =
                                ((animationTime - crossFadeStartTime2) / crossFadeDuration).coerceIn(
                                    0f,
                                    1f
                                )
                            applyCrossFade(
                                previousAnimationIndex2,
                                previousAnimationTime2 + (animationTime - crossFadeStartTime2),
                                fadeProgress
                            )

                            updateBoneMatrices()

                            if (fadeProgress >= 1f) {
                                isCrossFading2 = false
                            }
                        } else {
                            applyAnimation(currentAnimationIndex2, animationTime)
                            updateBoneMatrices()
                        }
                    }
                }

                delay(16)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                isPlaying = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(model1.value) {
        model1.value?.animator?.let { animator ->
            println("Model 1 has ${animator.animationCount} animations")
            for (i in 0 until animator.animationCount) {
                println(
                    "Animation $i name: ${animator.getAnimationName(i)} and duration: ${
                        animator.getAnimationDuration(
                            i
                        )
                    }"
                )
            }
        }
    }

    LaunchedEffect(model2.value) {
        model2.value?.animator?.let { animator ->
            println("Model 2 has ${animator.animationCount} animations")
            for (i in 0 until animator.animationCount) {
                println(
                    "Animation $i name: ${animator.getAnimationName(i)} and duration: ${
                        animator.getAnimationDuration(
                            i
                        )
                    }"
                )
            }
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)
        ) {
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val environmentLoader = rememberEnvironmentLoader(engine)
            val centerNode = rememberNode(engine)

            val cameraNode = rememberCameraNode(engine) {
                position = Position(y = 0.5f, z = 1.5f)
                lookAt(centerNode)
                centerNode.addChildNode(this)
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
                childNodes = listOf(
                    centerNode,
                    rememberNode {
                        ModelNode(
                            modelInstance = modelLoader.createModelInstance(
                                assetFileLocation = "models/male_model.glb"
                            ),
                            scaleToUnits = 0.004f
                        ).apply {
                            position = model1Position
                            rotation = model1Rotation
                            centerNode.addChildNode(this)

                            model1.value = this
                        }
                    },
                    rememberNode {
                        ModelNode(
                            modelInstance = modelLoader.createModelInstance(
                                assetFileLocation = "models/female_model.glb"
                            ),
                            scaleToUnits = 0.004f
                        ).apply {
                            position = model2Position
                            rotation = model2Rotation
                            centerNode.addChildNode(this)

                            model2.value = this
                        }
                    }
                ),
                environment = environmentLoader.createHDREnvironment(
                    assetFileLocation = "env/sky_2k.hdr"
                )!!,
                onFrame = {
                    cameraNode.lookAt(centerNode)
                },
                onGestureListener = rememberOnGestureListener(
                    onDoubleTap = { _, node ->
                        node?.apply { scale *= 1.5f }
                    }
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Controls",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        Card(
                            modifier = Modifier
                                .wrapContentSize(),
                            shape = CircleShape
                        ) {
                            IconButton(
                                onClick = {
                                    currentModel.value = "Male"
                                    bottomSheetIsVisible.value = !bottomSheetIsVisible.value

                                    isPlaying = true
                                },
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(4.dp),
                                    painter = painterResource(R.drawable.male),
                                    contentDescription = "Male",
                                    tint = Color(0xFF1E90FF)
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .width(8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .wrapContentSize(),
                            shape = CircleShape
                        ) {
                            IconButton(
                                onClick = {
                                    currentModel.value = "Female"
                                    bottomSheetIsVisible.value = !bottomSheetIsVisible.value

                                    isPlaying = true
                                },
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(4.dp),
                                    painter = painterResource(R.drawable.female),
                                    contentDescription = "Female",
                                    tint = Color(0xFFFF69B4)
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .wrapContentSize(),
                        shape = CircleShape
                    ) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(4.dp),
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Go back"
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    isPlaying = !isPlaying
                }
            ) {
                Card(
                    modifier = Modifier
                        .size(60.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (isPlaying) R.drawable.pause else R.drawable.play
                            ),
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            }
        }
    }

    if (bottomSheetIsVisible.value) {
        ActionsBottomSheet(
            showBottomSheet = bottomSheetIsVisible.value,
            onDismiss = {
                bottomSheetIsVisible.value = false
            },
            model = currentModel.value,
            onMoveLeft = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                moveModelLeft(modelIndex)
            },
            onMoveRight = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                moveModelRight(modelIndex)
            },
            onMoveUp = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                moveModelBackward(modelIndex)
            },
            onMoveDown = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                moveModelForward(modelIndex)
            },
            onResetPos = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                resetModelPosition(modelIndex)
            },
            onIdle = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                playAnimationOnce(modelIndex, 2) // idle
            },
            onTalk = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                playAnimationOnce(modelIndex, 3) // talking
            },
            onWave = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                playAnimationOnce(modelIndex, 4) // wave
            },
            onDance = {
                val modelIndex = if (currentModel.value == "Male") 1 else 2
                playAnimationOnce(modelIndex, 1) // dance
            }
        )
    }
}