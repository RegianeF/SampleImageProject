package com.example.sampleproject

import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalCoilApi::class)
suspend fun shareImage(
    context: Context,
    imageUrl: String,
    onError: () -> Unit
) {
    withContext(Dispatchers.IO) {
        context.imageLoader.diskCache?.openSnapshot(imageUrl)?.use { snapshot ->
            val file = snapshot.data.toFile()
            val originalUri = file.getUri(context)

            val reader = snapshot.metadata.toFile().bufferedReader()
            var lastLine: String? = reader.readLine()

            while (lastLine != null && !lastLine.contains("content-type", ignoreCase = true)) {
                lastLine = reader.readLine()
            }

            try {
                context.contentResolver.query(
                    originalUri,
                    null,
                    null,
                    null,
                    null
                )?.use {
                    while (it.moveToNext()) {
                        val mimeType = lastLine?.substringAfterLast(":")
                            ?.trim()
                            ?: originalUri.getMimeType(context, MimeTypeMap.getSingleton())

                        mimeType ?: continue

                        val extension = MimeTypeMap
                            .getSingleton()
                            .getExtensionFromMimeType(mimeType)

                        extension ?: continue

                        val imageCache = File.createTempFile(
                            "temp",
                            ".$extension"
                        )

                        file.copyTo(imageCache, overwrite = true)

                        val imageCacheUri = imageCache.getUri(context)

                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, imageCacheUri)
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            type = mimeType
                        }

                        val shareIntent = Intent.createChooser(sendIntent, "")

                        context.startActivity(shareIntent)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }
}