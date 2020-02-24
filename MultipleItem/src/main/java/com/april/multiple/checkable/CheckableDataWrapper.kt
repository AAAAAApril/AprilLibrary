package com.april.multiple.checkable

data class CheckableDataWrapper<T : Any> internal constructor(
    var data: T,
    var checked: Boolean = false
)