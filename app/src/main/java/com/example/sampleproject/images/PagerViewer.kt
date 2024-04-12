package com.example.sampleproject.images

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerViewer(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    pagerState: PagerState,
    currentImage: String
) {

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