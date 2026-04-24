# Claude Conversation Export — Legacy Code Refactoring
**Date:** 2026-04-23  
**Project:** Pokemon-TCG-Evolution  
**Model:** Claude Sonnet 4.6 (Thinking)

---

## Summary

This conversation covers two sessions of work:

1. **Analysis** — Identifying two refactoring areas in the Pokemon-TCG-Evolution codebase using principles from *Working Effectively with Legacy Code* by Michael Feathers.
2. **Implementation** — Fully implementing Refactoring #1: moving trainer card selection policy out of `Game.java` and into `Trainer.java` as three query methods (`requiresPokemonSelection`, `requiresEnergySelection`, `requiresGuiSwitchUpdate`), eliminating all hardcoded trainer name string comparisons from `Game`.

### Changes Made

| File | Change |
|------|--------|
| `src/main/java/main/Trainer.java` | Added `requiresPokemonSelection()`, `requiresEnergySelection()`, `requiresGuiSwitchUpdate()` methods |
| `src/main/java/main/Game.java` | Removed all `trainer.getName().equals(...)` comparisons; replaced with policy method calls |
| `Tests/main/TrainerTest.java` | Added 11 new unit tests for the three new policy methods |
| `Tests/main/GameTest.java` | Updated 9 EasyMock-based tests to expect policy methods instead of `getName()` |

### Changes NOT Made

- Refactoring #2 (Extract Interface for `DefaultTurnManager`) was identified but not implemented.
- Javadoc comments added during implementation were removed by the user (their preference).

### Build Results (Before → After)

| Metric | Before | After |
|--------|--------|-------|
| Tests | 348 | 359 (+11) |
| Line Coverage | 95% (992/1042) | 95% (994/1044) |
| Mutations Generated | 562 | 572 (+10) |
| Mutations Killed | 507 (90%) | 514 (90%) |
| Test Strength | 98% | 97% |
| Build | ✅ SUCCESS | ✅ SUCCESS |

---

## Conversation Transcript

---

### Turn 1 — User Command

> Find two areas to refactor based on Working Effectively with Legacy Code by Michael Feathers. Refer to specific chapters if you can.

**Context:** Active document was `DefaultTurnManager.java`. `mvn verify` had just completed successfully with 348 tests passing.

---

### Turn 1 — Gemini Response

*(Files explored: `DefaultTurnManager.java`, `Game.java`, `PlayerHandler.java`, `Trainer.java`, `Player.java`, test directory structure, `TrainerTest.java`, `Pokemon.java`)*

---

## Two Refactoring Areas Based on *Working Effectively with Legacy Code*

---

### Area 1: `handleUseTrainer` / `displayTrainerPokemonSelection` — **Parametrize Method (Chapter 13, "I Need to Make a Change but I Don't Know What Tests to Write")**

**The Problem — "Naming a Trainer by String"**

`Game.java` lines 383–463 contain a cluster of methods that all branch on `trainer.getName()` with hardcoded string literals like `"Switch"`, `"Potion"`, and `"Super Potion"`:

```java
// Game.java:398-399
String name = trainer.getName();
if (selectedPokemon == null && (name.equals("Switch") || name.equals("Potion") || name.equals("Super Potion"))) {

// Game.java:413
if(trainerName.equals("Potion") || trainerName.equals("Super Potion") || trainerName.equals("Switch")) {
```

Feathers calls this a **"Sensing Variable"** and **"Fake Objects"** problem (Chapter 13): the logic that *should* live in `Trainer` itself has leaked into `Game`, making the class untestable without wiring up a real `Trainer` object with the exact right name string. Adding a new trainer card (e.g., "Full Heal") means hunting down every place in `Game` that branches on `getName()` — a classic symptom of what Feathers calls **"Responsibility Creep."**

**The Feathers Fix — Move the Policy Back to `Trainer`**

Feathers advises extracting the branching behavior into the class that *owns* the data (Chapter 22, "I Need to Change a Monster Method..."). The `Trainer` class should expose two query methods that encode the policy:

```java
// Trainer.java — add these methods
public boolean requiresPokemonSelection() {
    String n = getName();
    return n.equals("Potion") || n.equals("Super Potion") || n.equals("Switch");
}

public boolean requiresEnergySelection() {
    return getName().equals("Super Potion");
}

public boolean requiresGuiSwitchUpdate() {
    return getName().equals("Switch");
}
```

This eliminates all the string comparisons from `Game`, collapses the three-method chain into a clean dispatch, and makes both sides independently testable. A `Trainer` unit test can now verify selection behavior without any GUI; a `Game` unit test can inject a fake `Trainer` via an interface or subclass (an **Object Seam**, Chapter 4).

---

### Area 2: `DefaultTurnManager.passTurn` directly accessing `game` package-level fields — **Expose Static Method / Extract Interface (Chapter 25, "Dependency-Breaking Techniques")**

**The Problem — "Law of Demeter Violations in `DefaultTurnManager`"**

`DefaultTurnManager.java` reaches two levels deep into `Game`'s internals:

```java
// DefaultTurnManager.java:12-18
boolean hasActiveAlready = game.playerHandler.passTurn();
game.gui.updateTurn(game.playerHandler.getPlayerTurn());
boolean hasCards = game.playerHandler.drawCardFromDeck();
if(!hasCards) {
    Player winner = game.playerHandler.getCurrentPlayer();
    Player loser = game.playerHandler.getDefendingPlayer();
    game.gameIsOver(winner, loser);
}
```

`DefaultTurnManager` holds a reference to `Game` and navigates through `game.playerHandler` and `game.gui` directly. These are `protected` (package-private) fields — not encapsulated getters. Feathers identifies this as a **"Hidden Dependency"** (Chapter 11, "I Need to Make a Change. What Methods Should I Test?"): the only way to test `DefaultTurnManager.passTurn()` in isolation is to instantiate a full real `Game`, which drags in a `GUI`, a `PlayerHandler`, two `Player` objects, shuffled decks, and Swing components.

**The Feathers Fix — "Parameterize Constructor" + Narrow Interface**

Feathers' **"Extract Interface"** technique (Chapter 25) says: define the *minimum* interface `DefaultTurnManager` actually needs, then pass it in. Instead of taking the whole `Game`:

```java
// New interface
public interface TurnContext {
    boolean passTurn();            // delegates to playerHandler.passTurn()
    int getPlayerTurn();
    boolean drawCardFromDeck();
    Player getCurrentPlayer();
    Player getDefendingPlayer();
    void onGameOver(Player winner, Player loser);
    void onTurnChanged(int turn);
}
```

`Game` implements `TurnContext` (or a thin adapter wraps it), and `DefaultTurnManager` depends only on that interface:

```java
public class DefaultTurnManager implements TurnManager {
    private TurnContext context;
    public DefaultTurnManager(TurnContext context) { this.context = context; }
    // ...
}
```

Now `DefaultTurnManager` can be tested with a simple mock `TurnContext` — no GUI, no Swing, no deck shuffling. This is exactly the **"Introduce Static Setter"** / **"Parameterize Constructor"** pattern Feathers recommends in Chapter 11 when you need to break a dependency to get tests around a class without changing its outward behavior.

---

### Summary

| # | Location | Feathers Technique | Chapter |
|---|----------|--------------------|---------|
| 1 | `Game.handleUseTrainer` / `displayTrainerPokemonSelection` — hardcoded trainer name strings | Move policy into `Trainer` (Parametrize Method, Object Seam) | Ch. 13, 22 |
| 2 | `DefaultTurnManager` reaching into `game.playerHandler` / `game.gui` directly | Extract Interface / Parameterize Constructor to break the `Game` dependency | Ch. 11, 25 |

Both refactors follow Feathers' golden rule: **get the class under test first, then refactor** — neither change requires touching tests that already pass, and both make the affected class independently unit-testable.

---

### Turn 2 — User Command

> Lets implement #1.

---

### Turn 2 — Gemini Response

*(Files explored: `TrainerTest.java` to understand existing test structure before writing new tests)*

**Implementation Plan:**
1. Add failing tests first (TDD red phase) to `TrainerTest.java`
2. Add the three policy methods to `Trainer.java`
3. Refactor `Game.java` to use those methods
4. Update `GameTest.java` EasyMock expectations to match

---

#### Step 1 — New Tests Added to `TrainerTest.java`

11 tests added covering all three methods across Potion, Super Potion, Switch, Bill, and unknown trainers:

```java
// --- requiresPokemonSelection ---

@Test
public void testRequiresPokemonSelectionForPotion() {
    Trainer t = new Trainer("Potion", "Remove up to 2 damage counters from 1 of your Pokemon.");
    assertTrue(t.requiresPokemonSelection());
}

@Test
public void testRequiresPokemonSelectionForSuperPotion() {
    Trainer t = new Trainer("Super Potion",
            "Discard 1 Energy card attached to your own Pokemon in order to remove up to 4 damage counters from that Pokemon.");
    assertTrue(t.requiresPokemonSelection());
}

@Test
public void testRequiresPokemonSelectionForSwitch() {
    Trainer t = new Trainer("Switch", "Switch 1 of your own Benched Pokemon with your Active Pokemon.");
    assertTrue(t.requiresPokemonSelection());
}

@Test
public void testRequiresPokemonSelectionFalseForBill() {
    Trainer t = new Trainer("Bill", "Draw 2 cards.");
    assertFalse(t.requiresPokemonSelection());
}

@Test
public void testRequiresPokemonSelectionFalseForUnknown() {
    Trainer t = new Trainer("Unknown", "Some random effect");
    assertFalse(t.requiresPokemonSelection());
}

// --- requiresEnergySelection ---

@Test
public void testRequiresEnergySelectionForSuperPotion() {
    Trainer t = new Trainer("Super Potion",
            "Discard 1 Energy card attached to your own Pokemon in order to remove up to 4 damage counters from that Pokemon.");
    assertTrue(t.requiresEnergySelection());
}

@Test
public void testRequiresEnergySelectionFalseForPotion() {
    Trainer t = new Trainer("Potion", "Remove up to 2 damage counters from 1 of your Pokemon.");
    assertFalse(t.requiresEnergySelection());
}

@Test
public void testRequiresEnergySelectionFalseForSwitch() {
    Trainer t = new Trainer("Switch", "Switch 1 of your own Benched Pokemon with your Active Pokemon.");
    assertFalse(t.requiresEnergySelection());
}

@Test
public void testRequiresEnergySelectionFalseForBill() {
    Trainer t = new Trainer("Bill", "Draw 2 cards.");
    assertFalse(t.requiresEnergySelection());
}

// --- requiresGuiSwitchUpdate ---

@Test
public void testRequiresGuiSwitchUpdateForSwitch() {
    Trainer t = new Trainer("Switch", "Switch 1 of your own Benched Pokemon with your Active Pokemon.");
    assertTrue(t.requiresGuiSwitchUpdate());
}

@Test
public void testRequiresGuiSwitchUpdateFalseForPotion() {
    Trainer t = new Trainer("Potion", "Remove up to 2 damage counters from 1 of your Pokemon.");
    assertFalse(t.requiresGuiSwitchUpdate());
}

@Test
public void testRequiresGuiSwitchUpdateFalseForBill() {
    Trainer t = new Trainer("Bill", "Draw 2 cards.");
    assertFalse(t.requiresGuiSwitchUpdate());
}
```

---

#### Step 2 — Policy Methods Added to `Trainer.java`

```java
public boolean requiresPokemonSelection() {
    String n = getName();
    return n.equals("Potion") || n.equals("Super Potion") || n.equals("Switch");
}

public boolean requiresEnergySelection() {
    return getName().equals("Super Potion");
}

public boolean requiresGuiSwitchUpdate() {
    return getName().equals("Switch");
}
```

*(Note: Javadoc comments were initially added and later removed by the user.)*

---

#### Step 3 — `Game.java` Refactored

**`handleUseTrainer` before:**
```java
if(trainer.getName().equals("Switch")) {
    playerPokemon.remove(playerHandler.getActivePokemon());
    playerPokemon.removeAll(playerHandler.getHandPokemon());
}
// ...
String name = trainer.getName();
if (selectedPokemon == null && (name.equals("Switch") || name.equals("Potion") || name.equals("Super Potion"))) {
    return;
}
// ...
if(trainer.getName().equals("Switch") && selectedPokemon != null) {
    gui.replaceActiveCard(currentPlayer, (Card) selectedPokemon);
}
```

**`handleUseTrainer` after:**
```java
if(trainer.requiresGuiSwitchUpdate()) {
    playerPokemon.remove(playerHandler.getActivePokemon());
    playerPokemon.removeAll(playerHandler.getHandPokemon());
}
// ...
if (selectedPokemon == null && trainer.requiresPokemonSelection()) {
    return;
}
// ...
if(trainer.requiresGuiSwitchUpdate() && selectedPokemon != null) {
    gui.replaceActiveCard(currentPlayer, (Card) selectedPokemon);
}
```

**`displayTrainerPokemonSelection` before:**
```java
String trainerName = trainer.getName();
if(trainerName.equals("Potion") || trainerName.equals("Super Potion") || trainerName.equals("Switch")) {
    String trainerText = "";
    if(trainer.getName().equals("Switch")) trainerText = "selectPokSwitch";
    if(!trainer.getName().equals("Switch")) trainerText = "selectPokPot";
    // ...
```

**`displayTrainerPokemonSelection` after:**
```java
if(trainer.requiresPokemonSelection()) {
    String trainerText = trainer.requiresGuiSwitchUpdate() ? "selectPokSwitch" : "selectPokPot";
    // ...
```

**`displayTrainerEnergySelection` before:**
```java
if(trainer.getName().equals("Super Potion")) {
```

**`displayTrainerEnergySelection` after:**
```java
if(trainer.requiresEnergySelection()) {
```

---

#### Step 4 — `GameTest.java` Updated

9 existing tests that mocked `Trainer` with `expect(trainer.getName()).andReturn("Potion").anyTimes()` etc. were updated to instead set up the policy method expectations:

**Before (example):**
```java
expect(trainer.getName()).andReturn("Potion").anyTimes();
```

**After (example):**
```java
expect(trainer.requiresGuiSwitchUpdate()).andReturn(false).anyTimes();
expect(trainer.requiresPokemonSelection()).andReturn(true).anyTimes();
expect(trainer.requiresEnergySelection()).andReturn(false).anyTimes();
```

Tests updated:
- `testHandleTrainerAction`
- `testHandleUseTrainerSwitch`
- `testPotionTrainer`
- `testSwitchTrainer`
- `testBillTrainer`
- `testPotionTrainer_noPokemonSelected_displaysMessage`
- `testSuperPotionTrainer`
- `testDisplayTrainerPokemonSelectionCancelled`
- `testDisplayTrainerEnergySelectionCancelled`

**Final test run result:**

```
Tests run: 359, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

### Turn 3 — User Action (mvn verify)

User ran `mvn verify` confirming the full build including mutation testing passed:

```
>> Line Coverage (for mutated classes only): 994/1044 (95%)
>> Generated 572 mutations Killed 514 (90%)
>> Mutations with no coverage 42. Test strength 97%
>> Ran 748 tests (1.31 tests per mutation)
BUILD SUCCESS
```

---

### Turn 4 — User Action (Removed Javadoc comments)

User manually removed the Javadoc comments from the three new methods in `Trainer.java`, keeping only the method signatures and bodies. This is a valid style preference — the method names are self-documenting.

Final state of the three methods in `Trainer.java`:

```java
public boolean requiresPokemonSelection() {
    String n = getName();
    return n.equals("Potion") || n.equals("Super Potion") || n.equals("Switch");
}

public boolean requiresEnergySelection() {
    return getName().equals("Super Potion");
}

public boolean requiresGuiSwitchUpdate() {
    return getName().equals("Switch");
}
```

---

### Turn 5 — User Command

> @[/export-conversation]

Requested export of this full conversation to a markdown file in `docs/`.

---

## File Change Summary

### `src/main/java/main/Trainer.java`
- **Added** `requiresPokemonSelection()` method
- **Added** `requiresEnergySelection()` method  
- **Added** `requiresGuiSwitchUpdate()` method

### `src/main/java/main/Game.java`
- **`handleUseTrainer`**: Removed 3 `getName()` string comparisons → replaced with `requiresGuiSwitchUpdate()` and `requiresPokemonSelection()` calls
- **`displayTrainerPokemonSelection`**: Removed local `trainerName` variable + 4 `getName()` comparisons → replaced with `requiresPokemonSelection()` guard and `requiresGuiSwitchUpdate()` ternary
- **`displayTrainerEnergySelection`**: Removed 1 `getName()` comparison → replaced with `requiresEnergySelection()` call

### `Tests/main/TrainerTest.java`
- **Added** 11 new unit tests (5 for `requiresPokemonSelection`, 4 for `requiresEnergySelection`, 3 for `requiresGuiSwitchUpdate`)

### `Tests/main/GameTest.java`
- **Updated** 9 existing EasyMock-based tests: replaced `getName()` stubs with policy method stubs (`requiresPokemonSelection`, `requiresEnergySelection`, `requiresGuiSwitchUpdate`)
