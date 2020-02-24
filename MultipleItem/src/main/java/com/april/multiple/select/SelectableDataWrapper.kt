package com.april.multiple.select

data class SelectableDataWrapper<T : Any> internal constructor(
    var data: T,
    var selected: Boolean = false
)