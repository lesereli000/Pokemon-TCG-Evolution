# S1G2-Project

Our project is done when we have tested each of the BVA cases outline below and ensured proper behavior in each case.

Constraints of Pokémon Trading Card Game:

Cards:

    - All cards have a Name. (commit e6f24b89c0808445175e24bb2151ca89d42f92df)
        - BVA: Strings
            - The empty string: If a card's name is empty, the program should throw an exception showing that the card could not be found.

    - All cards have a Type from among the following:

        - Pokémon (commit e6f24b89c0808445175e24bb2151ca89d42f92df)
             - All Pokémon cards are one or more of the energy types. (commit e6f24b89c0808445175e24bb2151ca89d42f92df)

             - All Pokémon cards are either (commit e6f24b89c0808445175e24bb2151ca89d42f92df)
                  - Basic
                  - Stage 1
                  - Stage 2

             - All Pokémon cards have the following attributes:
                  - HP 
                       - If a Pokémon’s HP is ever less than or equal to the total damage on that Pokémon it is knocked out. (commit db3eee16aa14bf28520c9029529f9a868d00c237)
                            - BVA: Interval (0, INT_MAX]
                                 - HP = 0: is knocked out
                                 - HP = 1: not knocked out
                                 - HP = INT_MAX: not knocked out
                                 - HP = INT_MAX + 1: throw an exception. If this ever happens, we should be worried.

                  - Damage Counters (DC) (commit e6f24b89c0808445175e24bb2151ca89d42f92df)
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

                            - Energy Requirement (commit 312d9e489022f7be1ecdf2b552b5ad9efc8e9e3e)
                                 - Number and color are important here.
                                 - BVA: Collections
                                     - An empty collection: This is a valid attack cost
                                     - Collection contains one energy: This is a valid attack cost
                                     - Collection contains more than one energy: This is a valid attack cost
                                     - Collection contains maximum potential energy cost: This is a valid attack cost
                                     - Collection contains duplicates: This is a valid attack cost
            
                            - Damage (commit 312d9e489022f7be1ecdf2b552b5ad9efc8e9e3e)
                                 - Can be a fixed amount or vary based on the effects of the attack
                                 - BVA:
                                     - Normal damage amount: attack should deal an unmodified amount of damage
                                     - Test Pokémon has weakness: attack should deal double damage
                                     - Test Pokémon has resistance: attack should deal less damage based on amount of resistance
                                     - An attack that deals fixed damage: attack should deal the correct amount of damage
                                     - Each attack with unique effects: attack should function as described

                  - Weaknesses (commit 8817eaf34415c3784b4f783a7c77c32ffd2c6dc9)
                       - A Pokémon card may or may not have a weakness.
                       - If a Pokémon has a weakness, it is weak to a certain energy type.
                       - Attacks from a Pokémon of the weakness type do double damage to this Pokémon (i.e. a Water type does double damage to a Pokémon that is weak to Water).

                  - Resistances (commit 8817eaf34415c3784b4f783a7c77c32ffd2c6dc9)
                       - A Pokémon card may or may not have resistances.
                       - A Pokémon with a resistance takes less damage when attacked by Pokémon of a certain type.
                       - The amount and type(s) of resistance vary.

                  - Retreat Cost (RC) (commit 6834cfa4c8a3d1bf942bd78bd1414f87d600f74f)
                       - All Retreat Costs are colorless energy, so the color doesn't matter, only the number of energy.
                            - 1 energy short and attempt to retreat (fail)
                            - Exact amount of energy needed (success)
                       - BVA: Interval [0-5]
                            - RC = -1: invalid retreat cost. Throw an exception
                            - RC = 0: Retreat is free
                            - RC = 5: Discard 5 energy from the Pokémon, then retreat.
                            - RC = 6: Higher than any printed RC. Throw an exception

        - Trainer (commit eb0b1c068539e701c6f0667ac44d92ba0d58408f)
             - All Trainer cards have
                  - Trainer Type
                       - Item
                       - Supporter
                       - Stadium
                  - Effects
                       - These effects vary greatly between cards, and must be implemented individually

        - Energy (commit e6f24b89c0808445175e24bb2151ca89d42f92df)
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

    - Hand: (commit 9bd5b4f5f54890a7542f61d4e21c792a474acade)
        - Each player should start the game with exactly 7 cards in their hand
        - Players are unable to view their opponents hand (unless otherwise specified)
            - We likely will not enforce this, as we want to run everything on a local machine and it would be difficult to distinguish between who is playing at any time
        - Any cards drawn from deck go into their hand

    - Deck: (commit 97db8ea0bb955a99a8bec145b075411f30ae629e)
        - Each player starts with a full deck of 60 Pokémon cards
        - Maximum of four copies of any card, EXCEPT basic energy cards
            - Test exactly 3 cards and add a 4th: successfully adds the card
            - Test exactly 4 cards and add a 5th: throw an exception showing which card there are too many copies of
        - Maximum of 2 Energy types per deck
            - Test exactly 1 card and attempt to draw a 2nd (success)
            - Test exactly 2 cards and attempt to draw a 3rd (fail)
        - Nobody may view the deck or alter the deck (unless otherwise specified)

    - Prize Cards: (commit d643f0881414cceaab2d5ebc05ebbfc817fff9c2)
        - Each player has their own 6 prize cards at the start of the game
        - Prize cards are face down and can not be viewed by anyone
        - When an opponents Pokémon is knocked out, player picks up a prize card into their hand
        - If all 6 prize cards are won by a player, they win the game
            - Test 5 prize cards do not give player a win
            - Test 6 prize cards do give player a win

    - Active Pokémon: (commit 87db9f503cd9b8a4dea35b243bd03cfa3a624ca6)
        - The top row of each players Pokémon in-play section has a single active Pokémon at all times
        - The active Pokémon takes damage when the opponent attacks
        - Are knocked out after their hp is <= 0
        - If either player is unable to have an active Pokémon for whatever reason, they lose
            - Test having exactly 1 active Pokémon does not result in a loss
            - Test having 0 active Pokémon does result in a loss

    - Bench: (commit e6f24b89c0808445175e24bb2151ca89d42f92df)
        - The bottom row of each players Pokémon in-play section has between 0-5 Pokémon
        - Every Pokémon on the bench is in-play
        - On their turn the player can choose to:
                • Add a Pokémon from their hand onto the bench
                • Move a Pokémon from the bench to active (if there is not currently an active Pokémon)

Set-up:

    - Players select language to play in (commit c9e7bd15a3a463bed1d852925a5018fd2c0e458c)
    - 2 players are created (commit f5d2e9e7a7969be820539ff19b6fdfd389b4dfc4)
    - Their decks, and the cards in them, are created and assigned (commit f5d2e9e7a7969be820539ff19b6fdfd389b4dfc4)
    - Coin is flipped to decide who gets to play first (commit edbbfaa58f5579d575df19cbf4e88fd25bc301fb)
         - BVA: Boolean
             - 0: player 1 goes first
             - 1: player 2 goes first
             - any other true value: throw an exception showing that the coin flip has failed
    - Each player draws 7 cards from their deck to their hand (commit f5d2e9e7a7969be820539ff19b6fdfd389b4dfc4)
    - Basic Pokémon can be assigned as active Pokémon or added to their bench (face down, unable to be viewed by their opponent) (commit 2f638dca1f3577cd1c5ae897c789a50094b8ed03)
        • If player does not have at least 1 basic Pokémon in their hand, they must reshuffle and their opponent may draw 1 extra card
        • If neither player has at least 1 basic Pokémon, they both reshuffle
        • Reshuffles continue accordingly until both players get at least 1 basic Pokémon and choose a basic Pokémon
        • Player must have at least 1 active Pokémon before the game continues
    - Top 6 cards of the deck should be placed down as Prize cards (commit f5d2e9e7a7969be820539ff19b6fdfd389b4dfc4)
    - Whoever won the coinflip is then able to begin playing first, the other player should not be able to make any changes while it is their opponents turn (commit f5d2e9e7a7969be820539ff19b6fdfd389b4dfc4)



Players Turn Functionality is Complete When: 

    - Able to switch turns between player (so that only the person who should play is able to play) (commit f61d9a164868885410fbc76e3adbe6de4a47fd08)
    - Able to draw a card and add it to their hand (commit 85c52d53f45e779a12e45f34e0c45f79896236a7)
    - Able to do all of the following, in any order:
        - Put basic Pokémon cards from your hand onto your bench (as many times as you want, max of 5 on bench) (commit 43cdb3e8dd32b8a3c7aa7ecf90d2502639d27296)
        - Evolve your Pokémon (as many times as you want following the evolution rules) (commit 9d1926c0b9932f47677f16ccd67b6ca3bcc78a23)
            • To play a Stage 1 or Stage 2 Pokémon, you must have had the previous stage (basic for stage 1 and stage 1 for stage 2)
            • When a Pokémon evolves, it keeps all cards attached to it (Energy cards, evolution cards, etc.) and any damage counters
            • A Pokémon should not be able to use attacks of previous evolution
        - Attach an Energy card from your hand to one of your Pokémon (only once per turn) (commit 2affe060477453f43c33846cc6cc6fec0a6b6ece)
            - Player should be able to place energy card under a Pokémon to indicate usable energy
        - Play trainer cards (as many as you want) (commit eb0b1c068539e701c6f0667ac44d92ba0d58408f)
            - When a trainer card is played, all of the necessary rules and actions should be performed
        - Retreat your active Pokémon (commit 9d45f2af5d5af40f70298f8ecb5d40ed5cfea1ee)
            - If you retreat, you can still attack that turn with your new Active Pokémon.
        - Able to attack (commit c69fb480e8d64556cc6a58a8fe55099c521e9adb)
            - Active Pokémon must have the right amount and type of energy cards
                • If a player has the right amount and types of energy cards, they choose an attack
                • Attack should do the corresponding amount of damage to opponents active Pokémon
                    - If opponents Pokémon is weak to this specific type, then attack damage should be multiplied appropriately
                    - If opponents Pokémon is resistant to that specific type, then damage should be divided/mitigated appropriately
                • Update the new state of the opponents Pokémon, including HP, and if necessary, knocked out
        - End turn and continue to the next steps (commit f61d9a164868885410fbc76e3adbe6de4a47fd08)

    
Ending the Game:

    - You win the game if any of the following occurs:
        - You take your last Prize card. (commit 194768a5f3239222b0f82e0a394b7b58ffc820fc)
        - Your opponent has no more Pokémon in play. (commit 9459809dc408f82d8f226b443ca5aaec387b49f1)
        - Your opponent must draw a card, and their deck is empty. (commit 85c52d53f45e779a12e45f34e0c45f79896236a7)


Additional Rules: 

    - Whenever a Pokémon is knocked out, its controller chooses a Pokémon from their bench to replace the knocked-out Pokémon. The other player puts one of their Prize cards into their hand without revealing it. (commit d643f0881414cceaab2d5ebc05ebbfc817fff9c2)
    - Colorless Energy Requirements and costs can be met with any Type of Energy. (commit 200d0672a8d8df771f4857cb5e8ea9bdf25b6ef2)
