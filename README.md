[![Jitpack](https://jitpack.io/v/tomhula/FeedbackLib.svg)](https://jitpack.io/#tomhula/FeedbackLib)

# FeedbackLib

FeedbackLib is a library to help developers make more configurable messages and other feedbacks easily in their program. It makes it easier for both the developer and the user to configure different types of so-called feedbacks. (messages, sounds, titles, etc.) It works on anything implementing [Adventure](https://github.com/KyoriPowered/adventure). All platforms can be found [here](https://docs.adventure.kyori.net/platform/index.html).

---

## Installation

###### Maven

```xml
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    ...
    <dependency>
        <groupId>com.github.tomhula.FeedbackLib</groupId>
        <artifactId>feedbacklib</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

###### Gradle (Groovy)

```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    ...
    implementation 'com.github.tomhula.FeedbackLib:feedbacklib:<VERSION>'
}
```

###### Gradle (Kotlin)

```kotlin
repositories {
    ...
    maven("https://jitpack.io")
}

dependencies {
    ...
    implementation("com.github.tomhula.FeedbackLib:feedbacklib:<VERSION>")
}
```

---
## How it works

Lets give an example, where user wants to configure a server join message.
```yaml
join-message:
  message: "<red>Welcome to the server!"
  title:
    title: "<red>Welcome to the server! (title)"
    sub-title: "<light_purple>We hope you'll like it here."
    fade-in: 20
    stay: 20
    fade-out: 20
  action-bar: "<white>Type /help for help."
  sound:
    key: entity.experience_orb.pickup
    source: AMBIENT
    volume: 1.0
    pitch: 2.0
```
As you can see, the message is not just a message, but also a sound, title and action-bar message. The player will receive all these, when joining the server. Each "part" is called a Feedback.  
These are the currently available feedbacks:
- ChatFeedback
- SoundFeedback
- TitleFeedback
- ActionBarFeedback
- FeedbackComposition (number of feedbacks together, that's what you see in the config)

The names are pretty self explanatory.  
If you only want to use for ex. message and sound, you can leave the rest out. Or if you want just a simple message, you can write it directly as so:
```yaml
join-message: "<red>Welcome to the server!"
```
All the messages use [MiniMessage](https://docs.adventure.kyori.net/minimessage/index.html) formatting.

---
## Usage

### Obtaining Feedbacks instances

#### Creating Feedbacks in code

When you want to create a Feedback in code, you can use Kotlin's handy syntax.
```kotlin
/* Equivalent to the yaml example before. */
val feedbackComposition = buildFeedbackComposition {
    chatFeedback("Welcome to the server.") {
        color(NamedTextColor.RED)
    }
    titleFeedback {
        title("Welcome to the server! (title)") {
            color(NamedTextColor.RED)
        }
        subtitle("We hope you'll like it here.") {
            color(NamedTextColor.LIGHT_PURPLE)
        }
        fadeIn(20)
        stay(20)
        fadeOut(20)
    }
    actionBarFeedback("Type /help for help")
    soundFeedback("entity.experience_orb.pickup") {
        source(Sound.Source.AMBIENT)
        volume(1f)
        pitch(2f)
    }
}
```

#### Deserializing Feedbacks from a map
FeedbackLib doesn't directly support parsing from yaml or different formats. It is able to parse from a `Map<String, Any>`, which you can get from files using parser libraries.
```kotlin
 /* Equivalent to the examples above. */

 /* Imagine getting this map from SnakeYAML for example. */
 val map = mapOf(
     "message" to "<red>Welcome to the server!",
     "title" to mapOf<String, Any>(
         "title" to "<red>Welcome to the server! (title)",
         "sub-title" to "<light_purple>We hope you'll like it here.",
         "fade-in" to 20,
         "stay" to 20,
         "fade-out" to 20
     ),
     "action-bar" to "<white>Type /help for help.",
     "sound" to mapOf<String, Any>(
         "key" to "entity.experience_orb.pickup",
         "source" to "AMBIENT",
         "volume" to 1.0,
         "pitch" to 2.0
     )
 )

 /* The deserialize fun takes Any, because you can also pass it directly a String, in which case just a ChatFeedback would be present. */
 val feedbackComposition = FeedbackComposition.deserialize(map)
```

### Using the Feedbacks instances
Once you have the Feedback instance, you can simply call `apply(audience)` on it to "send" the feedback to the provided [Audience](https://docs.adventure.kyori.net/audiences.html).
```kotlin
feedbackComposition.apply(audience)
```

### Placeholders
The library also includes a `Placeholder` variant of all Feedbacks. You can deserialize maps with placeholders. All placeholders are enclosed in `{}`. eg. `{player_name}`.
```kotlin
/* Imagine getting this map from SnakeYAML for example. */
val map = mapOf(
    "message" to "<blue>{player} <red>Welcome to the server!",
    "title" to mapOf<String, Any>(
        "title" to "<blue>{player} <red>Welcome to the server! (title)"
    )
)

val placeholderFeedbackComposition = PlaceholderFeedbackComposition.deserialize(map)
```
Once you have got a `Placeholder` instance. You can use it like so:
```kotlin
val placeholderFeedbackComposition = PlaceholderFeedbackComposition.deserialize(map)
val player = ...
val placeholders = Placeholders.of("player", player.getName())

placeholderFeedbackComposition.apply(player, placeholders)
```

### Components
FeedbackLib also includes handy syntax to easily create Adventure Components in code:
```kotlin
val textComponent = buildTextComponent("Welcome ") { 
    color(NamedTextColor.RED)
    
    textComponent("Notch") {
        color(NamedTextColor.BLUE)
        decorate(TextDecoration.BOLD)
    }
    
    textComponent(" to the server.")
}
```
It's just a wrapper around [Adventure's builders](https://docs.adventure.kyori.net/text.html).
Similarly, you can build Keybind and Translatable components.
