package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.constants.PredefinedIcon
import java.util.*

sealed interface DatabaseElement {
    val uuid: UUID
    val times: TimeData?
    val icon: PredefinedIcon
    val tags: List<String>
}
