# Distributed Snake

Distributed Snake is a collaborative game for Android. Simply launch the App and all
people in the same WiFi network can join your game (if your WiFi supports mDNS). Enjoy
a classic Snake game where users take turns to control the reptile.

## Installation instructions 
 
- Make sure to install ant and protobuf-compiler first
- Go to your sdk location and include <sdk_location>/tools in your path
- Restart your terminal
- Go to test01 and execute ```android update project -p .```
- ```cd ../appcombat_v7```
- ```android update project -p .```
- ```cd ../test01```
- ```ant clean```
- DONE! To compile it on your phone: ```ant gen_instrument_install```
This will only install the app to your phone, not launch it.

If you want to use eclipse, you can also import the two projects (appcompat_v7 is a dependecy). Eclipse will show compilation errors (missing protobuf generated files), that can be solved by running 'ant gen' in the 'test01' folder.