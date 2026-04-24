# Seams Analysis: Working Effectively with Legacy Code (Chapter Mapping)

In Michael Feathers' *Working Effectively with Legacy Code*, different chapters address specific scenarios where dependencies prevent testing and safe modification. Introducing a **seam**—a place where you can alter behavior without editing in that place—is the core solution.

Here are 6 locations in your codebase where seams can be introduced, each mapped to a specific scenario/chapter from the book:

## 1. Chapter 8: How Do I Add a Feature?
**The Scenario:** You need to add a new feature to an untested area, but you don't want to modify existing code and risk breaking it.

**Location:** `Trainer.java` (Line 13)
```java
private static final Map<String, TrainerEffect> effectRegistry = new HashMap<>();
```
**The Seam:** Currently, `Trainer` registers its effects in a static block. If you want to add a new effect without modifying `Trainer.java` (Sprout Class technique), you can introduce a seam by allowing external classes to register their own `TrainerEffect` implementations at runtime via a public `registerEffect(String name, TrainerEffect effect)` method, rather than hardcoding them in the `Trainer` class.

## 2. Chapter 9: I Can't Get This Class into a Test Harness
**The Scenario:** A class is hard to instantiate in a test because its constructor does too much work or creates hidden dependencies (the "Irritating Parameter" or "Hidden Dependency" problem).

**Location:** `PlayerHandler.java` (Line 27)
```java
protected void createPlayers() {
    player1 = new Player("Player 1");
    player2 = new Player("Player 2");
}
```
**The Seam:** You cannot test `PlayerHandler` without also invoking `Player` (and its file I/O for decks). Introduce an **Object Seam** using the *Parameterize Constructor* technique: pass the `Player` instances directly into the `PlayerHandler` constructor.

## 3. Chapter 10: I Can't Run This Method in a Test Harness
**The Scenario:** A specific method has a hidden dependency (like randomness, time, or external state) that makes its execution non-deterministic and untestable.

**Location:** `Deck.java` (Line 42)
```java
Random rand = new Random();
```
**The Seam:** Creating a `Random` instance *inside* the deck makes shuffling unpredictable. Introduce a seam using *Parameterize Method* or *Parameterize Constructor* to inject the `Random` object (e.g., `new Random(123)` for tests). *(Note: Your `SetupGame.java` actually does this correctly!)*

## 4. Chapter 14: Dependencies on Libraries Are Killing Me
**The Scenario:** The code is tightly coupled to a third-party framework or standard library, making automated testing impossible without triggering actual library behavior (like opening UI windows).

**Location:** `GameGUI.java` (e.g., Line 138)
```java
JOptionPane.showMessageDialog(frame, message);
```
**The Seam:** Directly calling `JOptionPane` means tests will block waiting for human interaction. Introduce an **Object Seam** by extracting a `UserPrompter` interface. Inject this interface into `GameGUI` (or `Game`), allowing you to use a `MockUserPrompter` in automated tests.

## 5. Chapter 20: This Class Is Too Big and I Don't Want It to Get Any Bigger
**The Scenario:** A "God Class" does too much, making it hard to understand, test, or safely modify without side effects.

**Location:** `Game.java`
* `Game.java` is your largest file (600+ lines), handling everything from game loops and UI interactions to turn management and attack logic.
**The Seam:** To safely split this class, introduce seams using *Extract Class* or *Extract Interface*. For instance, create a `TurnManager` interface and inject it into `Game`. This allows you to carve off responsibilities without breaking the rest of the file.

## 6. Chapter 22: I Need to Change a Monster Method and I Can't Write Tests for It
**The Scenario:** A single method is extremely long and complex, making it impossible to write a targeted test for a specific piece of its internal logic.

**Location:** `Game.java:handleInstantDrop` (Lines 102–168)
* This 66-line method handles multiple complex `if/else` conditions for playing items like Energies, Trainers, and Basic Pokemon directly from the UI.
**The Seam:** Introduce seams *inside* the method using *Sprout Method* or *Extract Method*. For example, extracting `handleInstantEnergyAttach()` creates a seam where you can override that specific logic in a subclass (using *Extract and Override Call*) to test it in isolation.
