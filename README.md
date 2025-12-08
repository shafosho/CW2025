# Tetris - COMP2042 Coursework

## GitHub
https://github.com/shafosho/CW2025

## Compilation Instructions
* You can run using the Launcher.java or maven.

**Prerequisites:**
* Java Development Kit (JDK) 21 or higher.

**Steps to Compile and Run:**
The easiest way to run the application is using the `Launcher` class.

1.  **Open the Project** in IntelliJ IDEA.
2.  Navigate to the file:  
    `src/main/java/com/comp2042/gui/Launcher.java`
3.  **Run 'Launcher.main()'**.

---

**Dependencies:**
* `org.openjfx:javafx-controls:21.0.6`
* `org.openjfx:javafx-fxml:21.0.6`
* `org.openjfx:javafx-media:21.0.6`
* `org.junit.jupiter:junit-jupiter-api:5.10.0` (Test Scope)

---

## Implemented and Working Properly

### 1. Refactoring & Maintenance
* **Index:** I changed the [j][i] to [i][j]
* **Reorganization:** The java classes have all been organized and put into their corresponding packages.
* **Single Responsibility Principle:** The massive `GuiController` class was refactored by extracting specific responsibilities into helper classes:
    * `InputHandler` for keyboard input.
    * `GameViewInitializer` for setting up the grid.
    * `PreviewInitializer` for managing the "Next" and "Hold" UI.
* **Brick Abstraction:** Introduced a `BrickBase` abstract class to handle common matrix logic, removing significant code duplication across all individual brick classes (`TBrick`, `LBrick`, etc.).
* **Data Encapsulation:** Created immutable data classes (`ViewData`, `MoveEvent`, `NextShapeInfo`) to safely pass game state between the Logic and GUI layers without exposing internal objects.
* **Magic Numbers:** I removed all magic numbers and replaced them with named constants.

### 2. Game Extensions
* **Start Menu:** Added a dedicated start screen (`startMenuPage.fxml`) with options to Start Game, View High Scores, or Exit.
* **High Score System:** Implemented a persistent high score board that saves the top 3 scores to a local file (`highscores.dat`) and allows players to enter their names upon achieving a high score.
* **Leveling System:** The game difficulty increases dynamically. Every 10 lines cleared increments the level and increases the speed of the falling bricks.
* **Hold Brick:** Implemented the "Hold" mechanic. Players can press **'C'** to store the current brick and swap it out later.
* **Next Brick Queue:** The UI now displays the next **3 upcoming bricks** instead of just one, allowing for better strategic planning.
* **Shadow (Ghost) Piece:** A semi-transparent shadow appears at the bottom of the board to show exactly where the current brick will land, improving accuracy.
* **Hard Drop:** Players can press **'SPACE'** to instantly drop the brick to the bottom and lock it in place.
* **Sound System:** Added background music with a **Mute** toggle button in the UI.
* **Pause Functionality:** Players can pause the game using **'P'**, which stops the game loop and hides the board.
* **Line-Cleared Score POP:** Basically every time a line is cleared the score will pop out instead of just floating.
* **Removed the soft-drop scores:** I felt like it would be too easy that way, so I removed it

---

## Implemented but Not Working Properly
* **None identified:** All implemented features (Hold, Shadow, Levels, High Scores, Sound) appear to be functioning as expected during testing.
* A feature that can be considered as such is the window display, when you open the startmenu as a full-screen, when you start game, it will shrink and not stay as full-screen.

---

## Features Not Implemented
* **Collect the stars:** Originally planned to add a feature where you can collect stars as the brick falls, but it was out of scope due to time constraints and the complexity of it all.

---

## New Java Classes

| Class | Location | Purpose |
| :--- | :--- | :--- |
| **InputHandler** | `com.comp2042.gui` | Extracts keyboard event handling logic from `GuiController`, mapping specific keys to game actions. |
| **SoundManager** | `com.comp2042.gui` | Manages audio playback, including background music initialization and mute toggling functionality. |
| **PreviewInitializer** | `com.comp2042.gui` | Handles the graphical setup and refreshing for the "Next Brick" queue and "Hold Brick" display panels. |
| **GameViewInitializer**| `com.comp2042.gui` | Handles the initialization of the main game grid and background rectangles to reduce clutter in `GuiController`. |
| **MenuController** | `com.comp2042.gui` | Controls the Start Menu logic and handles scene navigation (Start -> Game, Start -> High Scores). |
| **HighScoreController**| `com.comp2042.gui` | Controls the High Score display screen, reading data from the manager and populating the UI. |
| **HighScoreManager** | `com.comp2042.gameLogic`| Manages the loading, saving, and sorting of high score data to/from the `highscores.dat` file. |
| **CurrentBrick** | `com.comp2042.gameLogic`| Encapsulates the state of the active falling brick (shape and rotation) to separate it from the board logic. |
| **BrickBase** | `com.comp2042.logic.bricks`| Abstract base class implementing `Brick`. Holds the `brickMatrix` to prevent code duplication in subclasses. |
| **ScoreEntry** | `com.comp2042.data` | A simple data class representing a single high score entry (Name + Score). |
| **MoveEvent** | `com.comp2042.data` | Represents a player or system action (Left, Right, Down) to be processed by the controller. |

---

## Modified Java Classes

| Class | Changes Made | Reason |
| :--- | :--- | :--- |
| **Main.java** | Changed `start` method to load `startMenuPage.fxml` instead of the game directly. | To implement the new Start Menu navigation flow. |
| **GuiController.java**| Removed huge chunks of logic (input, init, sound) and delegated them to helper classes. Added `bindScore` for live updates. | To adhere to the Single Responsibility Principle and make the class maintainable. |
| **GameController.java**| Added handling for `onHardDropEvent` and `onHoldEvent`. Added score/level binding logic. | To support the new gameplay features (Hard Drop, Hold, Leveling). |
| **SimpleBoard.java** | Added `holdBrick`, `nextBricks` (Queue), and `getShadowY` logic. Refactored constructors. | To implement the core logic for the Hold, Queue, and Shadow features. |
| **RandomBrickGenerator**| Updated to return `Brick` objects and manage the new queue system. | To support the 3-brick preview queue. |
| **Score.java** | Added `level` and `lines` properties and logic to calculate level based on lines cleared. | To implement the progressive difficulty system. |
| **Brick Classes (I, J, L..)**| Removed `getShapeMatrix` implementation and extended `BrickBase`. | To remove duplicate code and improve inheritance structure. |

---

## Unexpected Problems
* **JavaFX Sound Dependency:** I initially encountered `ClassNotFound` errors when trying to play music. I realized `javafx-media` was not included in the default dependencies.
    * *Solution:* Added `org.openjfx:javafx-media` to the `pom.xml` file.
* **Shadow Rendering Lag:** Drawing the shadow brick every single frame caused slight performance stuttering on the first implementation.
    * *Solution:* Optimized the refresh logic to only redraw the shadow when the active brick actually moves or rotates, rather than on every game tick.
 
* **Video file lag:** I had underestimated the downloading of the demo video as I have an old laptop and very bad wifi and it caused my late submission, I sincerely apologize.
