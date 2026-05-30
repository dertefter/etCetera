package com.dertefter.banner_edit.presentation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dertefter.banner_edit.R
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import uk.codecymru.drawbox.box.DrawBox
import uk.codecymru.drawbox.controller.DrawController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BannerEditScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }
    val drawController = remember { DrawController() }

    val scrollState = rememberScrollState()

    val color by drawController.color.collectAsState()
    val strokeWidth by drawController.strokeWidth.collectAsState()
    val canUndo by drawController.canUndo.collectAsState(false)
    val canRedo by drawController.canRedo.collectAsState(false)

    LaunchedEffect(Unit) {
        drawController.enabled.value = true
    }

    LaunchedEffect(uiState.uri) {
        uiState.uri?.let { uri ->
            val request = ImageRequest.Builder(context)
                .data(uri)
                .allowHardware(false)
                .build()
            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val originalBitmap = (result.drawable as BitmapDrawable).bitmap
                val targetRatio = 16f / 9f
                val width = originalBitmap.width
                val height = originalBitmap.height
                val currentRatio = width.toFloat() / height

                val croppedBitmap = if (currentRatio > targetRatio) {
                    val targetWidth = (height * targetRatio).toInt()
                    val offset = (width - targetWidth) / 2
                    Bitmap.createBitmap(originalBitmap, offset, 0, targetWidth, height)
                } else {
                    val targetHeight = (width / targetRatio).toInt()
                    val offset = (height - targetHeight) / 2
                    Bitmap.createBitmap(originalBitmap, 0, offset, width, targetHeight)
                }
                drawController.open(croppedBitmap.asImageBitmap())
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onEvent(Event.OnPhotoSelected(uri))
            }
        }
    )

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(R.string.banner_edit_title))
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(Event.OnBack) }) {
                        Icon(
                            imageVector = Icons.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { drawController.undo() },
                        enabled = canUndo
                    ) {
                        Icon(
                            imageVector = Icons.ArrowBack,
                            contentDescription = "Отменить",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = { drawController.redo() },
                        enabled = canRedo
                    ) {
                        Icon(
                            imageVector = Icons.ArrowForward,
                            contentDescription = "Вернуть",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    FilledTonalIconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.AttachFile,
                            contentDescription = "Выбрать файл"
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                DrawBox(
                    controller = drawController,
                    modifier = Modifier.fillMaxSize()
                )

                if (uiState.uploadStatus == UploadStatus.UPLOADING) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                tonalElevation = 1.dp,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    BannerColorPicker(
                        selectedColor = color,
                        onColorSelected = { drawController.color.value = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(
                            value = strokeWidth,
                            onValueChange = { drawController.strokeWidth.value = it },
                            valueRange = 1f..100f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onEvent(Event.OnSaveDrawing(drawController.internalBitmap.asAndroidBitmap()))
                },
                enabled = uiState.uploadStatus != UploadStatus.UPLOADING
            ) {
                Text("Сохранить")
            }
        }
    }
}

@Composable
fun BannerColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color.Black, Color.White, Color.Red, Color.Blue,
        Color.Green, Color.Yellow, Color.Cyan, Color.Magenta,
        Color(0xFFFF5722), Color(0xFF795548), Color(0xFF9C27B0)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selectedColor == color) 3.dp else 1.dp,
                        color = if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.LightGray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Preview
@Composable
fun BannerEditScreenPreview(){
    AppTheme {
        BannerEditScreen(UiState()) {}
    }
}
