# Kotpass 
![Build & Test](https://github.com/anvell/kotpass/actions/workflows/gradle.yml/badge.svg) [![](https://jitpack.io/v/anvell/kotpass.svg)](https://jitpack.io/#anvell/kotpass)

Library provides reading/writing support for KDBX files in Kotlin.

Created specifically for **KeeMobile** Password Manager, check it out:

[<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width='200'/>](https://play.google.com/store/apps/details?id=app.keemobile)

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
    implementation 'com.github.anvell:kotpass:0.4.0'
}
```

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
