# Photo-Gallery-Application

My partner (@Eric Zhang) and I's implementation of an Android Photo Gallery Application with a user-friendly UI and fully functional capabilities of a traditional photo gallery application. This project was created using Android Studio (Java & XML).

The core of the project lies within the State Design Pattern and use of OOP with Album and Photo classes. The application allows for the switch of 4 different states whenever the user performs an action that prompts the switch. All 4 states inherit a parent class "State." 
* The HomeScreen state displays saved albums that have previously been created and accepts signals through a search button that provides functionality to search for photos and albums as well as signals from a create album button. 
* The AlbumDisplay state displays a specific album's photos with buttons to navigate to previous and next photos and rename the album. 
* The PhotoDisplay state provides a master display of all photos with buttons to navigate to previous and next photos. Furthermore, in this state you are also able to retrieve the album the photo is a part of, delete the photo, add the photo to an album, set and edit and remove tags (such as location or people). 
* The SearchResults state display all relevant photos that follow after using any of the search bars in other states. In this state, the user is able to navigate between photos, curate a list of photos and create an album with them, or continue searching. 

All of the above mentioned states revolve around signal handling through buttons and features that Android Studio provided such as drop down menus, search bars, etc.

While developing the functionality and backend, we also paid very careful attention to the user-interface of the application. From the aesthetics (colors, shapes, layout) to precise button and feature placement on the screen, we made sure to make the interface as the most user-friendly it can be. We were ambitious with our implementation but diligently precise with our execution.
