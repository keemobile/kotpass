package io.github.anvell.kotpass.constants

internal enum class HeaderFieldId {
    EndOfHeader,
    Comment,
    CipherId,
    Compression,
    MasterSeed,
    TransformSeed,
    TransformRounds,
    EncryptionIV,
    InnerRandomStreamKey,
    StreamStartBytes,
    InnerRandomStreamId,
    KdfParameters,
    PublicCustomData
}
