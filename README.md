# DEVELOPMENT: JavaGSM
This is a repo under development to create a cross-platform game server manager suite. Java was picked because it supports all platforms out of the box and is a personal favorite of Scarsz.

# Usage
JavaGSM obviously requires Java, minimum Java 7. Java 7 is standard on all modern distros, you should be good to go from the get-go.

**Windows**: Download the latest build at http://scarsz.tech:8080/job/JavaGSM/lastSuccessfulBuild/artifact/target/JavaGSM.jar and run it from `cmd` via `java -jar JavaGSM.jar`

**Linux & OS X**: Run `curl -o JavaGSM.jar http://scarsz.tech:8080/job/JavaGSM/lastSuccessfulBuild/artifact/target/JavaGSM.jar; chmod +x JavaGSM.jar`. You can now run JavaGSM from `./JavaGSM.jar`.

# Contributing
**Know Java?** Great! Create a new class file under `com.gameservermanagers.JavaGSM.servers` and create a method `public static void install(File destination)`. It will be automatically linked to the main lists through reflection. All you have to do is get the server installed through `install` and you're good to go.

**Don't know Java?** Not a problem. You can contribute by helping report errors/bugs to the GitHub's issues page or by sending a donation to the developers either in the form of a Steam gift to get development for a specific game server going or sending some spare change through PayPal and get us a drink. The choice is yours.
