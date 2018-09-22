# HexR
An android application I decided to make on my own time. I utilized Java and the [LibGDX framework](https://libgdx.badlogicgames.com/) to create a fun and challenging puzzle game. Everything you see was created by me but I did recieve some help from friends for some of the textures. 

The game was originally intended to be cross platform (hence the use of LibGdx) but due to developer fees I have decided against releasing it for IOS. As time went on, a lack of free time halted me from actually finishing the product. If you wish to look at the game files, the class files will be found in "core" and the assets can be found under "android." 

## Screenshots and Gifs of Gameplay:

![alt text](https://i.imgur.com/wzDVuZu.jpg)

### Gifs
Gameplay 1: https://gyazo.com/0a0a2dd5d4d608fe94b302c555f5b6b0

Gameplay 2: https://gyazo.com/023899fa1c655e61ece993f383522c25

Start/Restart: https://gyazo.com/f1430d55158a32a4c9fa651ee899a01b

Menu: https://gyazo.com/f6ed10d65af2e21722b68dec0a77aa51

# Highlights of the Project

## Scalability:

HexR is an android application and therefor it must be visually scalable to different devices. The entire game has been made with every phone screen in mind; so practically every collision detection and element in the game utilizes dynamic sizes. For this reason HexR should be able to work just fine on any device and the limited use of new apis allow it to scale back all the way to Android 2.1.

## Google Play:

Using the google play api I was able to hook up Leaderboards and Trophy collection into the game. If you would progress through the trophies and score new highscores, the game would actually publish it all to your google play account. It would also allow for local and global leaderboards.

## Hexagon Grid Algorithm

This was actually quite a challenge to complete so I decided to write about it. The game generates a grid and assigns an index to each hex (x, y) where x represents the row index and y represents the column index. Think of it as a regular 2d grid but with some blank spaces that form a hexagonal grid. My original approach was to recursively step through the hexs and determine if a line is full. This was a slow and tedious approach. Since the game requires the user to constantly place shapes and pop lines I needed something faster.

I started to rigirously plot different sample shapes and determing which coordinates I would need to check. Because the board can be described as a grid I was able to come up with a general mathematical forumala for checking lines. The formula, in constant time, returns the start and end indexes of all the lines I must check. Just through observation of the board you can see I will at most have to check for 3 lines. However, since I will never have to check more than 9 hexagons in a line, by calculating my lines in constant time I have eliminated the hard work of the algorithm. From there I can sequentially check the hexagons to see if it is full or not and the cost of doing this will never be high.

Thanks to the extra time spent finding a more intricate solution, I got rid of whatever gameplay lag I had with the old algorithm. Significantly boosting the performance of the game and keeping it at 60fps on older android devices.

## Dynamic Menu System

For the menu system I decided to try something creative. I kept the idea of the hexagonal grid and used it throughout the different menu states. The "skin" tab uses the grid to demonstrate variations in how the game looks. The "trophy" tab makes the hexagons clickable to demonstrate which trophies are available. The "leaderboard" tab uses the grid to showcase a crown that you click to see your highscore. The most interesting problem with this was making the hexagon entity generic enough for all of this repurposing. Keeping track of the menu states is trivial but repurposing the hexagon objects proved to be an interesting problem.

## GameState and Trophies

Since the game is endless it makes sense to be able to return to where you previously left off. HexR uses basic file reading and writing to save the state of the board along with any progress you might have with trophies (ones you unlocked and/or currently are completing). This gives the user incentive to come back and try to beat their highscores and continue unlocking content for the game.
