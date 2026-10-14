package app.keemobile.kotpass.models

import org.redundent.kotlin.xml.Node

data class DatabaseContent(
    val meta: Meta,
    val group: Group,
    val deletedObjects: List<DeletedObject>,
    val unknownElements: List<Node> = listOf(),
    val unknownRootElements: List<Node> = listOf()
)
