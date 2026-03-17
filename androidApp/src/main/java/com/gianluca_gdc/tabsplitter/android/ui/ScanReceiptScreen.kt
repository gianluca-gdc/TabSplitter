package com.gianluca_gdc.tabsplitter.android.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.TableInfo
import com.gianluca_gdc.tabsplitter.android.R
import com.gianluca_gdc.tabsplitter.ui.constants.Res
import androidx.compose.runtime.rememberCoroutineScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.launch
import android.provider.MediaStore
import androidx.activity.result.launch
import androidx.core.graphics.drawable.toBitmap

import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.text.Text

import com.gianluca_gdc.tabsplitter.util.parseReceiptText
import com.gianluca_gdc.tabsplitter.util.convertToGrayscale

import com.gianluca_gdc.tabsplitter.model.AssignedItem
import com.gianluca_gdc.tabsplitter.model.BillDetails

@Composable
fun ScanReceiptScreen(
    onBack: () -> Unit,
    onLaunchCamera: () -> Unit,
    onLaunchGallery: () -> Unit,
    onScanComplete: (subtotal: Double?, tax: Double?, items: List<Pair<String, Double>>) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val preprocessedBitmap = convertToGrayscale(it)
            val image = InputImage.fromBitmap(preprocessedBitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            scope.launch {
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        // using ml kits recognized text blocks, preserve order
                        val parsedText = visionText.textBlocks
                            .sortedBy { it.boundingBox?.top ?: 0 }
                            .flatMap { block ->
                                block.lines.sortedBy { it.boundingBox?.left ?: 0 }
                            }
                            .joinToString("\n") { it.text }
                        Log.d("OCR", "Full parsed text:\n$parsedText")
                        // parsing raw ocr text into receipt data
                        val parsed = parseReceiptText(parsedText)
                        onScanComplete(parsed.subtotal, parsed.tax, parsed.items)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            val preprocessedBitmap = convertToGrayscale(bitmap)
            val image = InputImage.fromBitmap(preprocessedBitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            scope.launch {
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        // using ml kit raw recognized text to preserve original line breaks
                        val parsedText = visionText.textBlocks
                            .sortedBy { it.boundingBox?.top ?: 0 }
                            .flatMap { block ->
                                block.lines.sortedBy { it.boundingBox?.left ?: 0 }
                            }
                            .joinToString("\n") { it.text }
                        Log.d("OCR", "Full parsed text:\n$parsedText")
                        // parsing raw ocr text into structured receipt
                        val parsed = parseReceiptText(parsedText)
                        onScanComplete(parsed.subtotal, parsed.tax, parsed.items)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(16.dp))
    {
        Row(){
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 5.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back to Bill Details",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()){
            Row(){
                Text(
                    text = "Take Photo",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp
                    )
                )
            }
            Spacer(Modifier.height(15.dp))
            Row(){
                IconButton(
                    onClick = { cameraLauncher.launch() },
                    modifier = Modifier.size(50.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera_scan),
                        contentDescription = "OCR Scan",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
            Row(){
                Text(
                    text = "Choose from Camera Roll",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp
                    )
                )
            }
            Spacer(Modifier.height(15.dp))
            Row(){
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.size(50.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.folder),
                        contentDescription = "OCR Scan",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}