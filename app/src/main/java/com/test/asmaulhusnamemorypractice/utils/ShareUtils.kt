package com.test.asmaulhusnamemorypractice.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    /**
     * Renders a Composable to a Bitmap and shares it via Intent.
     */
    fun shareComposableAsImage(
        activity: Activity,
        width: Int = 1080,
        height: Int = 1920,
        content: @Composable () -> Unit
    ) {
        val composeView = ComposeView(activity).apply {
            setContent(content)
            layoutParams = FrameLayout.LayoutParams(width, height)
        }

        // Create a hidden FrameLayout to attach the ComposeView
        val hiddenLayout = FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(width, height)
            visibility = View.INVISIBLE
            addView(composeView)
        }

        // Attach to window so Compose can initialize correctly
        val rootGroup = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootGroup.addView(hiddenLayout)

        // Force layout
        hiddenLayout.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        hiddenLayout.layout(0, 0, width, height)

        // Give Compose a brief moment to render, then capture
        hiddenLayout.postDelayed({
            try {
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                hiddenLayout.draw(canvas)

                // Remove from view tree
                rootGroup.removeView(hiddenLayout)

                // Share Bitmap
                shareBitmap(activity, bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                rootGroup.removeView(hiddenLayout)
            }
        }, 300) // 300ms delay to allow Compose rendering
    }

    private fun shareBitmap(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()
            val file = File(cachePath, "progress_share.png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Saya baru saja menyelesaikan latihan hafalan Asmaul Husna! 🤲")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Progres"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
