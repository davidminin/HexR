# HexR
A project I made on my own time throughout 2015. I utilized Java and the LibGDX framework to create a fun and challenging phone puzzle game. Everything you see was created by me but I did recieve some help from friends with some of the textures. 

The game was originally intended to be cross platform but due to time and developer fees I have decided to just release for Android. If you wish to look at the game files the base content will be found in core and assets can be found in android. The game cannot be found in the android store just yet but I plan to touch up the code and finaly release it. Everything in the game works right now except leaderboards.

##Screenshots and Gifs of Gameplay:

These will be added soon...

#Highlights of the Project

##Scalability:

HexR is an android application and therefor it must be scalable. The entire game has been made with every phone screen in mind, so practically every collision detection and element in the game utilizes dynamic sizes. For this reason HexR should be able to work just fine on any device.

##Leaderboards:

This is the final feature that I wish to implement into the game. This is close to completion but due to LibGdx being slightly outdated there are a lot of system errors that I need to work around. I actually paused development on this project because of this but plan to fix this and release it sometime in the summer.

##Hexagon Grid Algorithm

This was actually quite a challenge to complete so I decided to include it. The game generates a grid with indicies (x, y) where x represents the row index and y represents the column index. Since the game requires to place shapes and pop any combination of lines I figured there must be some sort of pattern to the tiles placed and the lines that can be created from each point. After rigirous plotting with different sample peices I was able to come up with a general mathematical forumala that returns the start and end indixes of a line based on a given start index (or starting tile). This not only saves a lot of running time, but also dramatically reduced the performance levels of the game which initially used recurssion to determine which lines need to be checked.

##Dynamic Menu System

The menu system is meant to blend in great visual design and user interfacing. Every screen is made with the central hexagon grid in mind and changes the user interface based on what is needed to be presented. For example the trophy tab uses each hex in the grid as a clickable trophy and the skin tab uses the hexagon to demonstrate the graphical skin change. The menu uses in game elements and breaks away from using conventional forms and lists.

#GameState and Trophy Reading/Loading

Since the game is endless it makes sense to  be able to return to where you previously left off. HexR uses basic file reading and writing to save the state of the board along with any progress you might have with trophies and trophies you have already unlocked. 
