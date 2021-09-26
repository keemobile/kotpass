package io.github.anvell.kotpass.constants

import io.github.anvell.kotpass.xml.FormatXml

enum class MemoryProtectionFlags(val value: String) {
    Title(FormatXml.Tags.Meta.MemoryProtection.ProtectTitle),
    UserName(FormatXml.Tags.Meta.MemoryProtection.ProtectUserName),
    Password(FormatXml.Tags.Meta.MemoryProtection.ProtectPassword),
    Url(FormatXml.Tags.Meta.MemoryProtection.ProtectUrl),
    Notes(FormatXml.Tags.Meta.MemoryProtection.ProtectNotes);
}
