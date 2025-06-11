package com.abdownloadmanager.shared.utils

import androidx.compose.runtime.*
import ir.amirab.util.flow.DerivedStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun <T> MutableStateFlow<T>.collectAsModifiableState(): MutableState<T> {
    val upStream = this
    val state = remember(this) {
        mutableStateOf(upStream.value)
    }
    LaunchedEffect(this) {
        upStream.onEach {
            state.value = it
        }.launchIn(this)
        snapshotFlow { state.value }
            .onEach { each ->
                upStream.update { each }
            }.launchIn(this)
    }
    return state
}

@OptIn(FlowPreview::class)
fun <T> MutableStateFlow<T>.asMutableState(
    scope: CoroutineScope,
): MutableState<T> {
    val upStream = this
    val state = mutableStateOf(upStream.value)
    upStream.onEach {
        state.value = it
    }.launchIn(scope)
    snapshotFlow { state.value }
        .onEach { each ->
            upStream.update { each }
        }.launchIn(scope)
    return state
}

@OptIn(FlowPreview::class)
fun <T> StateFlow<T>.asState(
    scope: CoroutineScope,
): State<T> {
    val upStream = this
    val state = mutableStateOf(upStream.value)
    upStream.onEach {
        state.value = it
    }.launchIn(scope)
    return state
}

@OptIn(FlowPreview::class)
fun <T> Flow<T>.asState(
    scope: CoroutineScope,
    initialValue:T,
): MutableState<T> {
    val upStream = this
    val state = mutableStateOf(initialValue)
    upStream.onEach {
        state.value = it
    }.launchIn(scope)
    return state
}

fun <T>MutableState<T>.asState(): State<T> {
    return this as State<T>
}

@OptIn(FlowPreview::class)
fun <T> State<T>.asStateFlow(): StateFlow<T> {
    val getValue = { value }
    return DerivedStateFlow(
        getValue = getValue,
        flow = snapshotFlow(getValue)
    )
}