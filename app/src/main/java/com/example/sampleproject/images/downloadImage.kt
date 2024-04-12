package com.example.sampleproject.images

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.buffer
import okio.sink
import okio.source
import java.io.File


@OptIn(ExperimentalCoilApi::class)
suspend fun downloadImage(
    context: Context,
    imageUrl: String,
    onError: () -> Unit,
    onSuccess: () -> Unit
) {
    withContext(Dispatchers.IO) {
        context.imageLoader.diskCache?.openSnapshot(imageUrl)?.use { snapshot ->
            val originalFile = snapshot.data.toFile()
            val originalUri = originalFile.getUri(context)

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
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val mimeType = lastLine?.substringAfterLast(":")
                            ?.trim()
                            ?: originalUri.getMimeType(context, MimeTypeMap.getSingleton())

                        mimeType ?: continue

                        val name = cursor.getString(
                            cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                        )

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            val extension = MimeTypeMap
                                .getSingleton()
                                .getExtensionFromMimeType(mimeType)

                            extension ?: continue

                            val storageDir = File(
                                Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                    .toString(),
                                "SAMPLE APP"
                            )
                            storageDir.mkdirs()
                            val file = File(storageDir, "$name.$extension")
                            file.createNewFile()

                            originalFile.copyTo(file, overwrite = true)

                            val contentValues = ContentValues().apply {
                                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
                                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                                put(
                                    MediaStore.Images.Media.DATE_ADDED,
                                    System.currentTimeMillis() / 1000
                                )
                                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                                put(MediaStore.Images.Media.DATA, file.absolutePath)
                            }
                            context.contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )

                            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                            intent.data = file.getUri(context)
                            context.sendBroadcast(intent)

                            onSuccess()

                        } else {
                            val relativeLocation =
                                Environment.DIRECTORY_PICTURES + File.separator + "SAMPLE APP"

                            val contentValues = ContentValues().apply {
                                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, name)
                                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                                put(MediaStore.Images.ImageColumns.RELATIVE_PATH, relativeLocation)
                                put(
                                    MediaStore.Images.Media.DATE_ADDED,
                                    System.currentTimeMillis() / 1000
                                )
                                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                                put(MediaStore.Images.Media.IS_PENDING, 1)
                            }

                            val uri = context.contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )

                            uri ?: continue

                            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                outputStream.sink().buffer().use { out ->
                                    val buffer = Buffer()

                                    context.contentResolver.openInputStream(originalUri)
                                        ?.use { inputStream ->
                                            inputStream.source().use { source ->
                                                while (true) {
                                                    val read =
                                                        source.read(
                                                            buffer,
                                                            DEFAULT_BUFFER_SIZE.toLong()
                                                        )
                                                    if (read == -1L) {
                                                        break
                                                    }
                                                    out.write(buffer, read)
                                                    out.flush()
                                                }
                                                out.close()
                                            }
                                        }
                                }
                            }

                            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                            context.contentResolver.update(
                                uri, contentValues, null, null
                            )

                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }
}

fun File.getUri(context: Context, packageName: String = context.packageName): Uri {
    return FileProvider.getUriForFile(
        context, "$packageName.fileprovider", this
    )
}

fun Uri.getMimeType(context: Context, mimeTypeMap: MimeTypeMap): String? {
    return context.contentResolver.getType(this)
        ?: mimeTypeMap.getMimeTypeFromExtension(
            MimeTypeMap.getFileExtensionFromUrl(this.toString())
        )
}