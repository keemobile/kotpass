package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.constants.AutoTypeObfuscation

data class AutoTypeData(
    val enabled: Boolean,
    val obfuscation: AutoTypeObfuscation = AutoTypeObfuscation.None,
    val defaultSequence: String? = null,
    val items: List<AutoTypeItem> = listOf()
)
