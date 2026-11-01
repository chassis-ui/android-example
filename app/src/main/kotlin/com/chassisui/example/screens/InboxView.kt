package com.chassisui.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chassisui.example.ChatMessage
import com.chassisui.example.ChatThread
import com.chassisui.example.LocalAppModel
import com.chassisui.example.R
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.ChassisType
import com.chassisui.example.chassis.Icon
import com.chassisui.example.chassis.rememberPageTransitions
import com.chassisui.example.components.Avatar
import com.chassisui.example.components.Badge
import com.chassisui.example.components.BarButton
import com.chassisui.example.components.Disclosure
import com.chassisui.example.components.EmptyState
import com.chassisui.example.components.InlineBar
import com.chassisui.example.components.LargeBar
import com.chassisui.example.components.ListRow
import com.chassisui.example.components.ListSeparator
import com.chassisui.example.components.PlainField
import kotlinx.coroutines.launch

/** The stack of the Inbox tab: a list, which pushes to a conversation. */
@Composable
fun InboxView() {
    val navController = rememberNavController()
    val transitions = rememberPageTransitions()
    NavHost(
        navController,
        startDestination = "threads",
        enterTransition = transitions.enter,
        exitTransition = transitions.exit,
        popEnterTransition = transitions.popEnter,
        popExitTransition = transitions.popExit,
    ) {
        composable("threads") {
            ThreadsScreen(onOpen = { navController.navigate("chat/${it.id}") })
        }
        composable("chat/{id}") { entry ->
            ChatView(entry.arguments?.getString("id").orEmpty(), navController)
        }
    }
}

/** A list with swipe actions and badges. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThreadsScreen(onOpen: (ChatThread) -> Unit) {
    val model = LocalAppModel.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colorResource(R.color.color_context_default_bg_even),
        topBar = { LargeBar("Inbox", scrollBehavior) },
    ) { padding ->
        if (model.threads.isEmpty()) {
            Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
                EmptyState(
                    icon = ChassisIcon.Inbox,
                    title = "Inbox is empty",
                    message = "New messages will show up here.",
                )
            }
        } else {
            LazyColumn(Modifier.padding(padding).consumeWindowInsets(padding).fillMaxSize()) {
                items(model.threads, key = { it.id }) { thread ->
                    ThreadRow(thread, onOpen = { onOpen(thread) })
                    ListSeparator()
                }
            }
        }
    }
}

@Composable
private fun ThreadRow(thread: ChatThread, onOpen: () -> Unit) {
    val model = LocalAppModel.current
    val scope = rememberCoroutineScope()
    val state = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            when (state.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> SwipeAction(
                    ChassisIcon.Trash,
                    "Delete",
                    colorResource(R.color.color_context_danger_base_color),
                    colorResource(R.color.color_context_danger_contrast_color),
                    Alignment.CenterEnd,
                )
                SwipeToDismissBoxValue.StartToEnd -> SwipeAction(
                    ChassisIcon.Check,
                    "Read",
                    colorResource(R.color.color_context_primary_base_color),
                    colorResource(R.color.color_context_primary_contrast_color),
                    Alignment.CenterStart,
                )
                SwipeToDismissBoxValue.Settled -> {}
            }
        },
        onDismiss = { direction ->
            if (direction == SwipeToDismissBoxValue.EndToStart) {
                model.delete(thread.id)
            } else {
                model.markRead(thread.id)
                // The row stays, and the swipe goes back
                scope.launch { state.reset() }
            }
        },
        // TalkBack has no swipe, so the same two actions are on the row
        modifier = Modifier.semantics {
            customActions = listOf(
                CustomAccessibilityAction("Read") { model.markRead(thread.id); true },
                CustomAccessibilityAction("Delete") { model.delete(thread.id); true },
            )
        },
    ) {
        ListRow(
            title = thread.name,
            subtitle = thread.messages.lastOrNull()?.text.orEmpty(),
            onClick = onOpen,
            leading = { Avatar(thread.name, dimensionResource(R.dimen.size_list_image)) },
            trailing = {
                if (thread.unread > 0) {
                    Badge(
                        thread.unread.toString(),
                        Modifier.semantics { contentDescription = "${thread.unread} unread" },
                    )
                }
                Disclosure()
            },
        )
    }
}

@Composable
private fun SwipeAction(
    icon: ChassisIcon,
    title: String,
    background: Color,
    foreground: Color,
    alignment: Alignment,
) {
    Box(
        contentAlignment = alignment,
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = dimensionResource(R.dimen.space_list_padding_x)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_2xsmall)),
        ) {
            Icon(icon, dimensionResource(R.dimen.size_icon_glyph_small), tint = foreground)
            Text(title, style = ChassisType.labelMedium.style, color = foreground)
        }
    }
}

/** A conversation in the message tokens, with a field that stays above the keyboard. */
@Composable
fun ChatView(threadId: String, navController: NavController) {
    val model = LocalAppModel.current
    val thread = model.threads.firstOrNull { it.id == threadId }
    val messages = thread?.messages.orEmpty()
    var draft by rememberSaveable { mutableStateOf("") }
    val list = rememberLazyListState()

    LaunchedEffect(threadId) {
        model.markRead(threadId)
    }
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            list.scrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = colorResource(R.color.color_message_bg_chat),
        topBar = { InlineBar(thread?.name.orEmpty(), onBack = { navController.popBackStack() }) },
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall)),
                modifier = Modifier
                    .background(colorResource(R.color.color_message_bg_form))
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(dimensionResource(R.dimen.space_context_small)),
            ) {
                PlainField(
                    value = draft,
                    onValueChange = { draft = it },
                    style = ChassisType.bodyMedium.style,
                    color = colorResource(R.color.color_message_fg_form),
                    caret = colorResource(R.color.color_context_primary_base_color),
                    placeholder = "Message",
                    placeholderColor = colorResource(R.color.color_context_default_fg_subtle),
                    singleLine = false,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Message" },
                )
                BarButton(
                    ChassisIcon.Send,
                    "Send",
                    {
                        model.send(draft, threadId)
                        draft = ""
                    },
                    enabled = draft.isNotEmpty(),
                )
            }
        },
    ) { padding ->
        LazyColumn(
            state = list,
            contentPadding = PaddingValues(dimensionResource(R.dimen.space_context_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall), Alignment.Bottom),
            modifier = Modifier.padding(padding).fillMaxSize(),
        ) {
            items(messages, key = { it.id }) { message ->
                Bubble(message)
            }
        }
    }
}

@Composable
private fun Bubble(message: ChatMessage) {
    val margin = dimensionResource(R.dimen.size_context_2xlarge)
    Row(
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (message.isMine) margin else dimensionResource(R.dimen.space_unit_0))
            .padding(end = if (message.isMine) dimensionResource(R.dimen.space_unit_0) else margin),
    ) {
        Text(
            message.text,
            style = ChassisType.bodyMedium.style,
            color = colorResource(
                if (message.isMine) R.color.color_message_fg_sent else R.color.color_message_fg_received,
            ),
            modifier = Modifier
                .background(
                    colorResource(
                        if (message.isMine) R.color.color_message_bg_sent else R.color.color_message_bg_received,
                    ),
                    RoundedCornerShape(dimensionResource(R.dimen.border_radius_context_2xlarge)),
                )
                .padding(
                    horizontal = dimensionResource(R.dimen.space_context_small),
                    vertical = dimensionResource(R.dimen.space_context_xsmall),
                ),
        )
    }
}
