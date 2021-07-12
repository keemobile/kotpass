package io.github.anvell.kotpass.resources

import io.github.anvell.kotpass.models.Group

internal object GroupRes {

    val BasicXml = """
    <Group>
        <UUID>tbwabsUKQEu3LmJ/wwKMpA==</UUID>
        <Name>Lorem</Name>
        <Notes />
        <IconID>${Group.DefaultIconId}</IconID>
        <Times>
            <CreationTime>${TimeDataRes.DateTimeText}</CreationTime>
            <LastModificationTime>${TimeDataRes.DateTimeText}</LastModificationTime>
        </Times>
        <IsExpanded>False</IsExpanded>
        <DefaultAutoTypeSequence />
        <EnableAutoType>null</EnableAutoType>
        <EnableSearching>null</EnableSearching>
        <LastTopVisibleEntry>AAAAAAAAAAAAAAAAAAAAAA==</LastTopVisibleEntry>
        <Group>
            <UUID>tbwabsaKQEu3LmJ/wwKMpA==</UUID>
            <Name>Ipsum</Name>
            <Notes />
            <IconID>${Group.DefaultIconId}</IconID>
            <IsExpanded>True</IsExpanded>
            <DefaultAutoTypeSequence />
            <EnableAutoType>null</EnableAutoType>
            <EnableSearching>null</EnableSearching>
            <LastTopVisibleEntry>AAAAAAAAAAAAAAAAAAAAAA==</LastTopVisibleEntry>
        </Group>
        ${EntryRes.BasicXml}
    </Group>
    """.trimIndent()
}
