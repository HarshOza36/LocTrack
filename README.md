# LocTrack
A sample location tracking application connected with Firebase. This app was created as a part of Mobile Communication and Computing (MCC) Project.

Steps :

- Sign up with google into firebase, create a new project,Name it something as you want.

- Now, Go to Realtime Database and create a database.

- Now in ```app/src/debug/res/values/google_maps_api.xml```, go to https://developers.google.com/maps/documentation/android/signup, Select the firebase project and get your Google Maps api and paste the code by replacing ```YOUR_KEY_HERE```

- Go back to firebase console into your project 

- Now click on the plus icon to ```Add app``` . This connects your app to the firebase console

<p align="center">
  <img width="360" height="100" src="https://user-images.githubusercontent.com/42001739/96955608-a020d400-1513-11eb-9b53-96f7e6104f32.png">
</p>

- After that select your own Platform , For Us it is Android


<p align="center">
  <img width="360" height="50" src="https://user-images.githubusercontent.com/42001739/96955692-d0687280-1513-11eb-9624-87243b4c588e.png">
</p>



- Now you will get a page as shown below. Enter your app package name , App name as you did in Android Studio and then , get your SHA1 signature in  ```app/src/debug/res/values/google_maps_api.xml``` and then click Register app
<p align="center">
  <img  src="https://user-images.githubusercontent.com/42001739/96956170-1114bb80-1515-11eb-8ffc-dae55d3c3e52.png">
</p>


- Now you will have to download the config file provided and paste it in the app folder shown that is ```google-services.json``` be pasted in the app folder

- After that you need to add Firebase SDK as shown on that page to the project level and app level gradle files.

- Finally , Run the 4th step which verifies the connection.

- After that, you can refer to the app/src/main/ folder for XML designing and Android developing.

---
