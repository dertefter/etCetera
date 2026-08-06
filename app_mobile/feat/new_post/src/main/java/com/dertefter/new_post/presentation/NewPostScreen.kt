package com.dertefter.new_post.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.contextmenu.builder.TextContextMenuBuilderScope
import androidx.compose.foundation.text.contextmenu.builder.item
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.poll.NewPollCard
import com.dertefter.design.components.post.OriginalPostCard
import com.dertefter.design.components.post.buildPostAnnotatedString
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.dertefter.new_post.R
import com.dertefter.new_post.presentation.component.UploadCard
import com.dertefter.new_post.presentation.mapper.toOriginalPostUiModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewPostScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
    screenMode: ScreenMode,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(uiState.content))
    }

    LaunchedEffect(uiState.content) {
        if (uiState.content != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = uiState.content)
        }
    }

    val selection = textFieldValue.selection
    val boldLabel = stringResource(R.string.span_bold)
    val italicLabel = stringResource(R.string.span_italic)
    val underlineLabel = stringResource(R.string.span_underline)
    val strikeLabel = stringResource(R.string.span_strike)
    val monospaceLabel = stringResource(R.string.span_monospace)
    val spoilerLabel = stringResource(R.string.span_spoiler)

    val menuBuilder: TextContextMenuBuilderScope.() -> Unit = {
        item(label = boldLabel, key = "bold", onClick = { onEvent(Event.OnSpanToggled("bold", selection.min, selection.max)) })
        item(label = italicLabel, key = "italic", onClick = { onEvent(Event.OnSpanToggled("italic", selection.min, selection.max)) })
        item(label = underlineLabel, key = "underline", onClick = { onEvent(Event.OnSpanToggled("underline", selection.min, selection.max)) })
        item(label = strikeLabel, key = "strike", onClick = { onEvent(Event.OnSpanToggled("strike", selection.min, selection.max)) })
        item(label = monospaceLabel, key = "monospace", onClick = { onEvent(Event.OnSpanToggled("monospace", selection.min, selection.max)) })
        item(label = spoilerLabel, key = "spoiler", onClick = { onEvent(Event.OnSpanToggled("spoiler", selection.min, selection.max)) })
    }


    val alpha by animateFloatAsState(
        targetValue = if (scrollBehavior.state.contentOffset < 0f) 0f else 1f
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                onEvent(Event.OnPhotosSelected(uris))
            }
        }
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.defaultScreenPadding)
                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
            )
            {
                Text(
                    modifier = Modifier
                        .alpha(alpha)
                        .weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = when (screenMode){
                        ScreenMode.NEW_POST -> stringResource(R.string.new_post_title)
                        ScreenMode.REPOST -> stringResource(R.string.new_repost_title)
                        ScreenMode.EDIT_POST -> stringResource(R.string.edit_post_title)
                        ScreenMode.NEW_COMMENT -> stringResource(R.string.new_comment_title)
                    }
                )

                if (screenMode == ScreenMode.NEW_POST || screenMode == ScreenMode.NEW_COMMENT){
                    if (screenMode == ScreenMode.NEW_POST){
                        FilledTonalIconButton(
                            onClick = { onEvent(Event.OnAddPoll) },
                            enabled = uiState.poll == null,
                            colors = IconButtonDefaults.filledTonalIconButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Icon(imageVector = Icons.Poll, contentDescription = stringResource(R.string.action_add_poll))
                        }
                    }

                    FilledTonalIconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Icon(imageVector = Icons.AttachFile, contentDescription = stringResource(R.string.action_add_media))
                    }
                }




                FilledIconButton(
                    onClick = { onEvent(Event.OnSavePost) },
                    shape = MaterialTheme.shapes.extraLargeIncreased,
                    enabled = (uiState.content.isNotBlank()
                            || uiState.uploads.isNotEmpty()
                            || ((uiState.poll != null)) && (uiState.poll.isReady()))
                            || (uiState.originalPost != null)
                ) {
                    if (uiState.isUploadingPost) {
                        AppLoadingIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    } else {
                        Icon(imageVector = Icons.Check, contentDescription = stringResource(R.string.action_save))
                    }
                }
            }
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),

        ) {

            uiState.originalPost?.let {
                OriginalPostCard(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.defaultScreenPadding),
                    originalPost = uiState.originalPost.toOriginalPostUiModel(),
                    onOpenPost = {},
                    onHashtagClick = {},
                    onUserClick = {},
                    onAttachmentClick = {_,_ -> }
                )
            }

            BasicTextField(
                value = textFieldValue.copy(annotatedString = buildPostAnnotatedString(textFieldValue.text, uiState.spans)),
                onValueChange = {
                    textFieldValue = it
                    if (it.text != uiState.content) {
                        onEvent(Event.OnContentChanged(it.text))
                    }
                },
                modifier = Modifier
                    .appendTextContextMenuComponents {
                        menuBuilder()
                    }
                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = uiState.content,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = false,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = remember { MutableInteractionSource() },
                        placeholder = {
                            Text(
                                text = when (screenMode){
                                    ScreenMode.NEW_POST -> stringResource(R.string.post_placeholder)
                                    ScreenMode.REPOST -> stringResource(R.string.repost_placeholder)
                                    ScreenMode.EDIT_POST -> stringResource(R.string.post_placeholder)
                                    ScreenMode.NEW_COMMENT -> stringResource(R.string.comment_placeholder)
                                }
                            )
                                      },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary,
                        ),
                        contentPadding = PaddingValues(0.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            AnimatedVisibility(
                visible = uiState.poll != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.poll?.let { poll ->
                    NewPollCard(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                            .padding(bottom = MaterialTheme.spacing.medium),
                        title = poll.title,
                        onTitleChanged = { onEvent(Event.OnPollTitleChanged(it)) },
                        questions = poll.questions,
                        isMultipleChoice = poll.isMultipleChoice,
                        onMultipleChoiceChanged = { onEvent(Event.OnPollMultipleChoiceChanged(it)) },
                        onNewQuestion = { onEvent(Event.OnAddPollQuestion) },
                        onRemoveQuestion = { onEvent(Event.OnRemovePollQuestion(it)) },
                        onChangeQuestion = { id, text -> onEvent(Event.OnPollQuestionChanged(id, text)) }
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                contentPadding = PaddingValues(
                    horizontal = MaterialTheme.spacing.defaultScreenPadding
                )
            ) {
                items(uiState.uploads) { upload ->
                    UploadCard(
                        upload = upload,
                        onRetry = { onEvent(Event.OnRetryUpload(upload.uri)) },
                        onDelete = { onEvent(Event.OnRemoveUpload(upload.uri)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewPostScreenPreview() {
    AppTheme {
        NewPostScreen(
            uiState = UiState(
                content = "Hello",
                spans = emptyList(),
                uploads = emptyList(),
                isUploadingPost = true
            ),
            onEvent = {},
            screenMode = ScreenMode.REPOST
        )
    }
}
