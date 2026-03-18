Listed directory main
Viewed Game.java:1-509
Viewed GameGUI.java:1-800
Viewed PlayerHandler.java:1-179
Viewed Player.java:1-219

Based on my review of the codebase (specifically focusing on [Game.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:0:0-0:0), [GameGUI.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:0:0-0:0), [PlayerHandler.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/PlayerHandler.java:0:0-0:0), and [Player.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Player.java:0:0-0:0)), there are several classic "code smells" as identified by Martin Fowler in his book *Refactoring*. 

Here are the most prominent code smells identified in this project:

### 1. Large Class (God Object)
A class that tries to do too much, often showing low cohesion.
*   **[GameGUI.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:0:0-0:0) (~900 lines):** This class is doing way too much. It handles defining UI layouts, rendering graphics via paint components, managing click events, handling localized strings, maintaining the application state, and directly formatting data from [Card](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Player.java:37:4-45:5) and [Pokemon](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Player.java:165:4-183:5) objects.
*   **[Game.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:0:0-0:0) (~500 lines):** This acts as the God object for your main logic loop. It delegates heavily, but still holds the responsibility for knowing the intimate details of every turn step, menu selection, attack processing, and trainer functionality.

### 2. Long Method
A method that contains too many lines of code, making it hard to read, understand, and reuse.
*   **`GameGUI.java -> paintComponent()` (approx. 140 lines):** This method handles rendering the entire state of the board in a single block of code. It contains the logic for rendering the background, player 1 & 2's bench, active Pokemon, decks, discard piles, strings, and prize cards all at once without breaking them up into smaller, private helper methods.

### 3. Switch Statements / Type Checking
Using `switch` statements or multiple `if/else` ladders (especially with type checking) instead of leveraging polymorphism. 
*   **`instanceof` Checks:** Both [Game.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:0:0-0:0) and [GameGUI.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:0:0-0:0) are loaded with `if (card instanceof Pokemon) {...} else if (card instanceof Trainer) {...}` instead of using polymorphic methods natively on the [Card](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Player.java:37:4-45:5) interface/superclass.
*   **Effect hardcoding:** In [Game.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:0:0-0:0), [handleUseTrainer(Trainer trainer)](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:277:4-297:5) uses string matching like `trainer.getName().equals("Switch")` or `"Potion"` to decide how the logic flows. The [Trainer](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:277:4-297:5) class should ideally implement polymorphic behavior so the [Game](cci:2://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:8:0-507:1) doesn't need to know the specific name to apply its effect. 

### 4. Middle Man
A class that exists mostly just to delegate calls to another component.
*   **[PlayerHandler.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/PlayerHandler.java:0:0-0:0):** Most of the methods in this class simply forward method calls perfectly onto the [Player](cci:2://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Player.java:4:0-217:1) class. For example: 
    *   [getCurrentPlayerHand()](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/PlayerHandler.java:62:4-64:5) returns `currentPlayer.handAsList()`
    *   [activeCanAddEnergy()](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/PlayerHandler.java:66:4-68:5) returns `currentPlayer.canAddEnergy()`
    *   [drawCardFromDeck()](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/PlayerHandler.java:140:4-142:5) returns `currentPlayer.drawCard()`
    Fowler recommends cutting out the middle man if it's not actually applying meaningful behavior around the delegation.

### 5. Primitive Obsession
Using primitive data types to represent domain concepts, leading to rigid and error-prone code.
*   **Status String returns:** In [Player.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Player.java:0:0-0:0) and [PlayerHandler.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/PlayerHandler.java:0:0-0:0), the [evolvePokemon](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Player.java:165:4-183:5) function relies on returning raw String literals such as `"Active"`, `"Bench"`, `"Error"`, or `"JustPlayed"`. These should just be an `Enum` or result objects.
*   **Coin Flips:** Expecting and matching raw strings like `"Heads"` and `"Tails"`.
*   **State configuration:** [GameGUI.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:0:0-0:0) is flooded with boolean state primitives (`confirmPokemonState`, `activeTurn`, [waitForAction](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:639:4-645:5), `cancelled`) rather than using a state machine pattern for turn steps.

### 6. Duplicated Code
Identical or largely similar code fragments scattered across the software.
*   **[GameGUI.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:0:0-0:0) (Player 1 vs Player 2 Logic):** All GUI logic is cleanly duplicated for player 1 and player 2. You will find almost identical branches for `if (playerTurn == 1) {...} else {...}` inside [makeActiveCard](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:292:4-302:5), [addBenchCard](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:304:4-313:5), [removeBenchCard](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:315:4-323:5), and [removePrizeCard](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:660:4-675:5). 
*   **Rendering:** Drawing the board for player 1 uses the same logic (just reversed coordinates) as drawing it for player 2 in [paintComponent()](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:80:8-220:9).

### 7. Message Chains
When a client asks for one object, and that object asks for another, and so on.
*   **[Game.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:0:0-0:0) to [Pokemon.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Pokemon.java:0:0-0:0):** You often chain through the handler to get what you want, like `playerHandler.getCurrentPlayer().getActivePokemon().getAttacks()`. This exposes your inner class relationships improperly to the GUI and the primary Game Loop.

### 8. Magic Numbers
Putting hardcoded, unexplained numbers deep within the code logic.
*   **[GameGUI.java](cci:7://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:0:0-0:0):** Fractions and coordinate logic like [(marginSide*3)/2](cci:2://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:8:0-507:1), `15`, [(frameWidth*5)/7](cci:2://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:8:0-507:1) are heavily hardcoded into positioning the board, making it mathematically frustrating to adjust without breaking the layout. 

### Where You Might Start Refactoring:
1. **Extract Class:** Break [GameGUI](cci:2://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/GameGUI.java:14:0-894:1) into multiple UI components (e.g., `BoardRenderer`, `ActionMenu`, `PlayerStatusPanel`).
2. **Replace Conditional with Polymorphism:** Move the effect implementation of [Trainer](cci:1://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Game.java:277:4-297:5) cards onto subclasses or command objects instead of checking their English `String` name. 
3. **Remove Middle Man:** Evaluate if [PlayerHandler](cci:2://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/PlayerHandler.java:4:0-176:1) is strictly necessary, or if its functionality could just be part of a `GameController` or delegated naturally to the [Player](cci:2://file:///home/seb/Documents/School/25-26/Spring/csse375/Pokemon-TCG-Evolution/main/Player.java:4:0-217:1) turns.
