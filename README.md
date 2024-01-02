<img src="https://github.com/JP-HellParadise/Retake/assets/24401452/1b05b13d-0a7b-403b-ba7a-9483bf965905" width="600">

## Retakes

"I strive for something beyond the strongest, that challenging me would be the most ridiculous thought ever, that fighting me would be a sin!"

Utilized for players who like roaming far from the spawnpoint but don't want items despawned before they get back to the death location.
Doesn't guarantee that you can re-obtain the items.

Highly configurable within the mod itself.

## Mod template

Using [CleanroomMC TemplateDevEnv](https://github.com/CleanroomMC/TemplateDevEnv/tree/overhaul). Licensed under MIT, it is made for public use.

Currently utilizes **Gradle 8.3** + **[RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) 1.3.24** + **Forge 14.23.5.2860**.

Enabled by default, both **coremod and mixin**.

### Instructions:

1. Clone this repository.
2. In the local repository, run the command `gradlew setupDecompWorkspace`
3. Open the project folder in IDEA.
4. Right-click in IDEA `build.gradle` of your project, and select `Link Gradle Project`, after completion, hit `Refresh All` in the gradle tab on the right.
5. Run `gradlew runClient` and `gradlew runServer`, or use the auto-imported run configurations in IntelliJ like `1. Run Client`.
