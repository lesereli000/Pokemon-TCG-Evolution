# S1G2-Project

Our project is done when we have tested each of the BVA cases outline below and ensured proper behavior in each case. 312d9e489022f7be1ecdf2b552b5ad9efc8e9e3e

Constraints of Pokémon Trading Card Game:

Cards:

    - All cards have a Name. (commit e6f24b89c0808445175e24bb2151ca89d42f92df)
        - BVA: Strings
            - The empty string: If a card's name is empty, the program should throw an exception showing that the card could not be found.

    - All cards have a Type from among the following:

        - Pokémon (commit #e6f24b8)
             - All Pokémon cards are one or more of the energy types. (commit e6f24b8)

             - All Pokémon cards are either (commit e6f24b8)
                  - Basic
                  - Stage 1
                  - Stage 2

             - All Pokémon cards have the following attributes:
                  - HP 
                       - If a Pokémon’s HP is ever less than or equal to the total damage on that Pokémon it is knocked out. (commit db3eee1)
                            - BVA: Interval (0, INT_MAX]
                                 - HP = 0: is knocked out
                                 - HP = 1: not knocked out
                                 - HP = INT_MAX: not knocked out
                                 - HP = INT_MAX + 1: throw an exception. If this ever happens, we should be worried.

                  - Damage Counters (DC) (commit e6f24b8)
                       - Each counter represents 10 damage, so total damage = 10 * DC
                            - BVA: Count
                                 - DC = -1: The program should throw an exception
                                 - DC = 0: Total damage = 0
                                 - DC = 1: Total damage = 10
                                 - DC > 1: Total damage = 10 * DC
                                 - DC > HP/10: The Pokémon should faint.

                  - Attacks 
                       - Each attack the Pokémon can use will have:
                            - Name (commit 312d9e489022f7be1ecdf2b552b5ad9efc8e9e3e)
                                 - BVA: Strings
                                     - The empty string: If an attack's name is empty, the program should throw an exception showing the card and attack that could not be found.

                            - Energy Requirement (commit 312d9e4)
                                 - Number and color are important here.
                                 - BVA: Collections
                                     - An empty collection: This is a valid attack cost
                                     - Collection contains one energy: This is a valid attack cost
                                     - Collection contains more than one energy: This is a valid attack cost
                                     - Collection contains maximum potential energy cost: This is a valid attack cost
                                     - Collection contains duplicates: This is a valid attack cost
            
                            - Damage (commit 312d9e4)
                                 - Can be a fixed amount or vary based on the effects of the attack
                                 - BVA:
                                     - Normal damage amount: attack should deal an unmodified amount of damage
                                     - Test Pokémon has weakness: attack should deal double damage
                                     - Test Pokémon has resistance: attack should deal less damage based on amount of resistance
                                     - An attack that deals fixed damage: attack should deal the correct amount of damage
                                     - Each attack with unique effects: attack should function as described

                  - Weaknesses (commit 8817eaf)
                       - A Pokémon card may or may not have a weakness.
                       - If a Pokémon has a weakness, it is weak to a certain energy type.
                       - Attacks from a Pokémon of the weakness type do double damage to this Pokémon (i.e. a Water type does double damage to a Pokémon that is weak to Water).

                  - Resistances (commit 8817eaf)
                       - A Pokémon card may or may not have resistances.
                       - A Pokémon with a resistance takes less damage when attacked by Pokémon of a certain type.
                       - The amount and type(s) of resistance vary.

                  - Retreat Cost (RC) (commit 6834cfa)
                       - All Retreat Costs are colorless energy, so the color doesn't matter, only the number of energy.
                            - 1 energy short and attempt to retreat (fail)
                            - Exact amount of energy needed (success)
                       - BVA: Interval [0-5]
                            - RC = -1: invalid retreat cost. Throw an exception
                            - RC = 0: Retreat is free
                            - RC = 5: Discard 5 energy from the Pokémon, then retreat.
                            - RC = 6: Higher than any printed RC. Throw an exception

        - Trainer (commit eb0b1c0)
             - All Trainer cards have
                  - Trainer Type
                       - Item
                       - Supporter
                       - Stadium
                  - Effects
                       - These effects vary greatly between cards, and must be implemented individually

        - Energy (commit e6f24b8)
             - All energy cards are one of the 11 energy types:
                  • Grass
                  • Fire
                  • Water
                  • Lightning
                  • Psychic
                  • Fighting
                  • Darkness
                  • Metal
                  • Fairy
                  • Dragon
                  • Colorless
             - All energy cards are worth 1 energy.



Zones:

    - Hand: (commit 85c52d5)
        - Each player should start the game with exactly 7 cards in their hand
        - Players are unable to view their opponents hand (unless otherwise specified)
            - We likely will not enforce this, as we want to run everything on a local machine and it would be difficult to distinguish between who is playing at any time
        - Any cards drawn from deck go into their hand

    - Deck: (commit 4a9b2bf)
        - Each player starts with a full deck of 60 Pokémon cards
        - Maximum of four copies of any card, EXCEPT basic energy cards
            - Test exactly 3 cards and add a 4th: successfully adds the card
            - Test exactly 4 cards and add a 5th: throw an exception showing which card there are too many copies of
        - Maximum of 2 Energy types per deck
            - Test exactly 1 card and attempt to draw a 2nd (success)
            - Test exactly 2 cards and attempt to draw a 3rd (fail)
        - Nobody may view the deck or alter the deck (unless otherwise specified)

    - Prize Cards: (commit 6f54b4c)
        - Each player has their own 6 prize cards at the start of the game
        - Prize cards are face down and can not be viewed by anyone
        - When an opponents Pokémon is knocked out, player picks up a prize card into their hand
        - If all 6 prize cards are won by a player, they win the game
            - Test 5 prize cards do not give player a win
            - Test 6 prize cards do give player a win

    - Active Pokémon: (commit 58db1a1)
        - The top row of each players Pokémon in-play section has a single active Pokémon at all times
        - The active Pokémon takes damage when the opponent attacks
        - Are knocked out after their hp is <= 0
        - If either player is unable to have an active Pokémon for whatever reason, they lose
            - Test having exactly 1 active Pokémon does not result in a loss
            - Test having 0 active Pokémon does result in a loss

    - Bench: (commit e6f24b8)
        - The bottom row of each players Pokémon in-play section has between 0-5 Pokémon
        - Every Pokémon on the bench is in-play
        - On their turn the player can choose to:
                • Add a Pokémon from their hand onto the bench
                • Move a Pokémon from the bench to active (if there is not currently an active Pokémon)

Set-up:

    - Players select language to play in (commit a92f6a2)
    - 2 players are created (commit 991c2c9)
    - Their decks, and the cards in them, are created and assigned (commit 991c2c9)
    - Coin is flipped to decide who gets to play first (commit 37568b4)
         - BVA: Boolean
             - 0: player 1 goes first
             - 1: player 2 goes first
             - any other true value: throw an exception showing that the coin flip has failed
    - Each player draws 7 cards from their deck to their hand (commit 991c2c9)
    - Basic Pokémon can be assigned as active Pokémon or added to their bench (face down, unable to be viewed by their opponent) (commit 0fd23cd)
        • If player does not have at least 1 basic Pokémon in their hand, they must reshuffle and their opponent may draw 1 extra card
        • If neither player has at least 1 basic Pokémon, they both reshuffle
        • Reshuffles continue accordingly until both players get at least 1 basic Pokémon and choose a basic Pokémon
        • Player must have at least 1 active Pokémon before the game continues
    - Top 6 cards of the deck should be placed down as Prize cards (commit 991c2c9)
    - Whoever won the coinflip is then able to begin playing first, the other player should not be able to make any changes while it is their opponents turn (commit 991c2c9)



Players Turn Functionality is Complete When: (commit )

    - Able to switch turns between player (so that only the person who should play is able to play)
    - Able to draw a card and add it to their hand
    - Able to do all of the following, in any order:
        - Put basic Pokémon cards from your hand onto your bench (as many times as you want, just max of 5 on bench)
        - Evolve your Pokémon (as many times as you want following the evolution rules)
            • To play a Stage 1 or Stage 2 Pokémon, you must have had the previous stage (basic for stage 1 and stage 1 for stage 2)
            • When a Pokémon evolves, it keeps all cards attached to it (Energy cards, evolution cards, etc.) and any damage counters
            • A Pokémon should not be able to use attacks or abilities of previous evolution unless a card says so
        - Attach an Energy card from your hand to one of your Pokémon (only once per turn)
            - Player should be able to place energy card under a Pokémon to indicate usable energy
        - Play trainer cards (as many as you want, but only one Supporter card and one Stadium card per turn)
            - When a trainer card is played, all of the necessary rules and actions should be peroformed, then it should automatically be put in the discard pile
            - Only one Supporter card should be allowed to be played per turn
            - Only one Stadium card should be allowed to be played per turn
        - Retreat your active Pokémon
            - If you retreat, you can still attack that turn with your new Active Pokémon.
        - Able to attack
            - Active Pokémon must have the right amount and type of energy cards
                • If a player has the right amount and types of energy cards, they choose an attack
                • Attack should do the corresponding amount of damage to opponents active Pokémon
                    - If opponents Pokémon is weak to this specific type, then attack damage should be multiplied appropriately
                    - If opponents Pokémon is resistant to that specific type, then damage should be divided/mitigated appropriately
                • Update the new state of the opponents Pokémon, including HP, and if necessary, knocked out
        - End turn and continue to the next steps

    
Ending the Game: (commit )

    - You win the game if any of the following occurs:
        - You take your last Prize card.
        - Your opponent has no more Pokémon in play.
        - Your opponent must draw a card, and their deck is empty.


Additional Rules: (commit )

    - Whenever a Pokémon is knocked out, its controller moves that card and all cards attached to it to the discard pile, then chooses a Pokémon from their bench to replace the knocked-out Pokémon. The other player puts one of their Prize cards into their hand without revealing it.
    - Colorless Energy Requirements and costs can be met with any Type of Energy.
