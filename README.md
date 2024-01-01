## Retakes

"Retake what was meant to be yours."

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