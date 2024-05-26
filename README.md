# Kotpass 
![Build & Test](https://img.shields.io/github/actions/workflow/status/keemobile/kotpass/gradle.yml?label=Build%20%26%20Test)
[![](https://jitpack.io/v/keemobile/kotpass.svg)](https://jitpack.io/#keemobile/kotpass) [![codecov](https://codecov.io/gh/keemobile/kotpass/graph/badge.svg?token=59LMP3BOXJ)](https://codecov.io/gh/keemobile/kotpass) ![badge][badge-jvm]

[badge-jvm]: http://img.shields.io/badge/-JVM-DB413D.svg

The library offers reading and writing support for [KeePass](https://en.wikipedia.org/wiki/KeePass) (KDBX) files in Kotlin, including the latest format version 4.1. It is suitable for Mobile, Desktop, and Backend JVM projects. The functional style API makes it convenient for MVI-like architectures.

## See it in action

This library is used as backbone of [KeeMobile](https://keemobile.app) password manager, check it out:

[<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width='160'/>](https://play.google.com/store/apps/details?id=app.keemobile)

## Installation

`Kotpass` is published on jitpack.io. Add repository it to your ```build.gradle``` script:
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.keemobile:kotpass:0.9.0'
}
```

## Usage

### 🧬 [Api reference](https://keemobile.github.io/kotpass)

Reading from file:

``` kotlin
val credentials = Credentials.from(EncryptedValue.fromString("passphrase"))
val database = File("testfile.kdbx")
    .inputStream()
    .use { inputStream ->
        KeePassDatabase.decode(inputStream, credentials)
    }    
```
`KeePassDatabase` is represented as immutable object, in order to alter it use a set of modifier extensions. 

Each time new `KeePassDatabase` object is returned:

``` kotlin
val groupUuid = UUID.fromString("c997344c-952b-e02b-06a6-29510ce71a12")
val newDatabase = database
    .modifyMeta {
        copy(generator = "Lorem ipsum")
    }.modifyGroup(groupUuid) {
        copy(name = "Hello kotpass!")
    }
```
