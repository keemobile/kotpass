# Kotpass

Library provides reading/writing support for KDBX files in Kotlin.

## Installation

`Kotpass` has not yet been published to any public repository.

Suggested installation methods:

• Setup and distribute via [Local Maven repository](https://docs.gradle.org/current/userguide/declaring_repositories.html)

## Usage

Reading from file:

``` kotlin
val credentials = Credentials.from(EncryptedValue.fromString("passphrase"))
val database = File("testfile.kdbx")
    .inputStream()
    .use { inputStream ->
        KeePassDatabase.decode(inputStream, credentials)
    }    
```
Database is represented as immutable object, in order to alter it use a set of modifier extensions. 

Each time new Database object is returned:

``` kotlin
val groupUuid = UUID.fromString("c997344c-952b-e02b-06a6-29510ce71a12")
val newDatabase = database
    .modifyMeta {
        copy(generator = "Lorem ipsum")
    }.modifyGroup(groupUuid) {
        copy(name = "Hello kotpass!")
    }
```
