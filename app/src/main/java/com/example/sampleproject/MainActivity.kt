package com.example.sampleproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.sampleproject.ui.theme.SampleProjectTheme
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val listImages = listOf(
            "https://plus.unsplash.com/premium_photo-1668024966086-bd66ba04262f?q=80&w=2092&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1532274402911-5a369e4c4bb5?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1501785888041-af3ef285b470?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1511576661531-b34d7da5d0bb?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://plus.unsplash.com/premium_photo-1677483425235-980fae409996?q=80&w=971&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/34/BA1yLjNnQCI1yisIZGEi_2013-07-16_1922_IMG_9873.jpg?q=80&w=1171&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1518098268026-4e89f1a2cd8e?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1559827291-72ee739d0d9a?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1413752362258-7af2a667b590?q=80&w=1176&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1433838552652-f9a46b332c40?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?q=80&w=1213&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        )

        setContent {
            SampleProjectTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    PagerViewer(innerPadding = innerPadding, images = listImages)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerViewer(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    images: List<String>
) {

    val pagerState = rememberPagerState {
        images.size
    }

    val currentImage = remember(pagerState.currentPage) {
        try {
            images[pagerState.currentPage]
        } catch (e: Exception) {
            e.printStackTrace()
            println("IndexOutOfBoundsException")
            ""
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->

        val surfaceColor = MaterialTheme.colorScheme.surface

        var backgroundColor by remember { mutableStateOf(surfaceColor) }

        Box(modifier = Modifier.background(backgroundColor)) {

            val imageState = rememberZoomableImageState(
                rememberZoomableState(
                    zoomSpec = ZoomSpec(maxZoomFactor = 5f)
                )
            )

            var isLoading by remember { mutableStateOf(false) }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.Center),
                visible = isLoading
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(innerPadding)
                        .align(Alignment.Center)
                )
            }

            var isError by remember { mutableStateOf(false) }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.Center),
                visible = isError
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberVectorPainter(Icons.Default.Build),
                    contentDescription = null
                )
            }

            ZoomableAsyncImage(
                state = imageState,
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentImage)
                    .allowHardware(false)
                    .crossfade(300)
                    .listener(
                        remember {
                            object : ImageRequest.Listener {
                                override fun onSuccess(
                                    request: ImageRequest,
                                    result: SuccessResult
                                ) {
                                    println("Image is loaded")
                                    isLoading = false
                                    Palette.Builder(result.drawable.toBitmap())
                                        .generate { palette ->
                                            val generatedColor =
                                                palette?.getDarkMutedColor(
                                                    backgroundColor.toArgb()
                                                )

                                            if (generatedColor != null && generatedColor != backgroundColor.toArgb()) {
                                                backgroundColor =
                                                    Color(generatedColor)
                                            }
                                        }
                                    super.onSuccess(request, result)
                                }

                                override fun onError(
                                    request: ImageRequest,
                                    result: ErrorResult
                                ) {
                                    println("Image has error")
                                    isLoading = false
                                    isError = true
                                    super.onError(request, result)
                                }

                                override fun onStart(request: ImageRequest) {
                                    println("Image start load")
                                    isLoading = true
                                    super.onStart(request)
                                }

                                override fun onCancel(request: ImageRequest) {
                                    println("Image is cancel")
                                    isLoading = false
                                    super.onCancel(request)
                                }
                            }
                        }
                    )
                    .build(),
                contentDescription = null,
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleProjectTheme {

    }
}