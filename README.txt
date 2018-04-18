Installation manual Wireless Storage Medium

For this application you need:
- A Raspberry Pi
- A computer with Eclipse
- The files that can be found at https://github.com/JKleinRot/NedapUniversity_WirelessSoftwareMedium/tags. Use the version tagged final. Clone this version to a directory on your computer

To prepare the Raspberry Pi and setup a network between your computer and the Raspberry Pi, follow the instruction in the pdf 'RaspberryPiSetup'.

First start the server application on the Raspberry Pi. Open the 'build.gradle' file in Eclipse and adjust the password on line 42 with the password of your Raspberry Pi. In a command window type 'ssh pi@192.168.1.1' and fill in your password to connect. In a separate command window navigate to the directory where you cloned the project. Type './gradlew build && ./gradlew deploy' to build the gradle project on the Raspberry Pi. The server application should now be running on the Raspberry Pi.

Thereafter start the client application on your computer. In the command window where you build the gradle project type 'java -jar WirelessStorageMedium.jar'. The client application should now be running on your computer.