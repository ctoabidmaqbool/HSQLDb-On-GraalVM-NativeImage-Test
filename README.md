# HSQLDb-On-GraalVM-NativeImage-Test

A simple application with Java 11+, JavaFX 15+, GraalVM and HSQLDb.

## Documentation

Read about this sample [here](https://docs.gluonhq.com/#_hellofx). This project is modified version for that's sample project with HSQLDb implementation.

## Quick Instructions

We use [GluonFX plugin](https://docs.gluonhq.com/) to build a native image for platforms including desktop and android.
Please follow the prerequisites as stated [here](https://docs.gluonhq.com/#_requirements).

### Desktop

Run the application on JVM/HotSpot:

    mvn gluonfx:run

Run the application and explore all scenarios to generate config files for the native image with:

    mvn gluonfx:runagent

Build a native image using:

    mvn gluonfx:build

Run the native image app:

    mvn gluonfx:nativerun

### Android

Build a native image for Android using:

    mvn gluonfx:build -Pandroid

Package the native image as an 'apk' file:

    mvn gluonfx:package -Pandroid

Install it on a connected android device:

    mvn gluonfx:install -Pandroid

Run the installed app on a connected android device:

    mvn gluonfx:nativerun -Pandroid

**Let me know for more info / any problem you faced.**