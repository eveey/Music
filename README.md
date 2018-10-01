# Music
Music rebels

![Music rebels](https://github.com/eveey/Music/blob/master/app/src/main/assets/web_hi_res_round_512.png)

Sample Android app in [Kotlin](https://kotlinlang.org/) for searching artists on [Spotify](https://www.spotify.com/).

## Building the app
* [Gradle](https://gradle.org/) build system
* Requires [Java 8](https://java.com/en/download/faq/java8.xml)

Important note: to use the services you need to register on [Spotify Developer Console](https://developer.spotify.com/console/) in order to obtain client secrets and add them to the project local.properties file:

 clientID="<your_client_id>"
 
 scheme="<your_scheme>"
 
 host="<your_host>"
 
## Authentication
* Using [Spotify Android SDK](https://github.com/spotify/android-sdk) for authentication and artist search. For using the app you need to login or sign up for a Spotify account.

## Architecture
* MVVM (Model-View-ViewModel)
* [Android Arhitecture Components](https://developer.android.com/topic/libraries/architecture/)
* [LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveData)
* [RxKotlin](https://github.com/ReactiveX/RxKotlin)
* [RxRelay](https://github.com/JakeWharton/RxRelay)

## Dependency management
* [Google/Dagger](https://github.com/google/dagger) - Dependency injection.

## Network
* [Square/Retrofit](https://github.com/square/retrofit) - HTTP RESTful connections.
* [OkHttp 3](https://square.github.io/okhttp/3.x/okhttp/) - HTTP client.
* [Square/Moshi](https://github.com/square/moshi) - Network JSON deserializer.

## Unit tests
* [JUnit4](https://junit.org/junit4/)
* [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin)

## Other
### Image loading
* [Glide](https://github.com/bumptech/glide) - 
An image loading and caching library for Android focused on smooth scrolling.
### Logging
* [Timber](https://github.com/JakeWharton/timber)
### Code quality
* [Ktlint](https://ktlint.github.io/) - An anti-bikeshedding Kotlin linter with built-in formatter.
* [Detekt](https://github.com/arturbosch/detekt) - Static code analysis for Kotlin.

## Roadmap:
* [Espresso](https://developer.android.com/training/testing/espresso/) - UI testing framework.
* [ProGuard](https://www.guardsquare.com/en/products/proguard) - Code obfuscation.
* [CircleCI](https://circleci.com/) - Continuous integration.
* [Firebase](https://firebase.google.com/) - Analytics.

## Wunderlist:
* [Android Jetpack/Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) - Android navigation framework.

## Disclaimer:
```PERSONAL PROJECT - NOT INTENDED FOR COMMERCIAL USE```


