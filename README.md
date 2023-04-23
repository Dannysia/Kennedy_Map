# Kennedy_Map

<p align="center">
  <img width=200px src="https://user-images.githubusercontent.com/90657408/233819160-89a153ff-e8ed-400e-ac06-028ab689ecfa.png">
  <img width=200px src="https://user-images.githubusercontent.com/90657408/233820038-ee551698-537f-41ee-805f-4bb7d599dee7.png">
  <img width=200px src="https://user-images.githubusercontent.com/90657408/233820420-12652341-50a4-49c1-a632-7f521f1342ca.png">

</p>

The N.A.R.A. (Navigation and Reservation App) for the James Lehr Kennedy Engineering build was a term project for Software Engineering ECCS3421 for android devices made in Android Studio using Java. The project's aim was to develop a navigation and reservation system for the James Lehr Kennedy Engineering building to simplify route planning and room booking. By providing graphical route displays and preventing scheduling conflicts, it enhances the building's efficiency and make it more accessible for new students.

An APK is provided, but currently the server backend is down since it was self hosted.

# Content
This repository contains the Gradle Java project which would be compiled into Android APKs for use on Android devices using Java 8. 
Directories:
* ```app/src/main/java/edu/onu/kennedy_map``` - Contains the source code for the project
* ```app/release```                           - Contains the most recent premade APK for the project

# Requirements
The following dependencies will need to be downloaded for further development:
* Java 8 JDK        - Initial project was created in Java 8, although no Java 8 specific features were used. Feel free to port to newer versions.
* Android 11 SDK    - App was initial made using Android 11, API level 30
* Various libraries - Should be installed automatically through maven, check ```build.gradle```

# Usage
General usage of the app are as follows:
1. Open up the application. Sign up or Sign in to your N.A.R.A account.
2. Use the general purpose map on the main screen (step 3), press the "Reserve a Room" button to go to the reservation screen (steps 4-6), or press the "Navigate to a Room" button to go to the path finding screen (steps 7).
3. Use the floor selector radio button to view a map of the respective floor. You may use two-finger pinching to zoom in and out of the map.
4. Select a room to reserve with the scrolling picker button. You will see a list of current reservations for the selected room.
5. Add an reservation for the selected room by setting the relevant times and dates of your reservation. Press "Reserve Room".
6. Cancel reservations by clicking on the three dot dropdown menu in the top-right of the Reservation screen and press "Cancel" on them in the "View Reservations" screen.
7. Select two rooms using the two scrolling picker objects. 
8. Press "Show me the path!" to show the animation of the path being overlaid the floor maps. If a path spans multiple floors, you can view the continuation of the path by clicking the respective floor radio buttons.
