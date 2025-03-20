# S1G2-Project

TODO: Add actual BVA's Values

TODO: Our Project is done when ...

Constraints of Pokemon Trading Card Game:

Cards:

    - All cards have a Name.

    - Some cards have been made illegal for tournament play

    - All cards have a Type from among the following:

        - Pokémon

             - All Pokémon cards are one or more of the energy types.

             - All Pokémon cards are either

                  - Basic

                  - Stage 1

                  - Stage 2

             - All Pokémon cards have the following attributes:

                  - HP
                       - If a Pokémon’s HP is ever less than or equal to the total damage on that Pokémon it is knocked out.
                            BVA Test Cases:
                            - HP = 1 (not knocked out)
                            - HP = 0 (is knocked out)

                  - Damage Counters

                       - Each counter represents 10 damage, so total damage = 10 * damage counters

                  - Attacks

                       - Each attack the Pokémon can use will have:

                            - Name

                            - Energy Requirement

                                 - Number and color are important here.

                            - Damage

                                 - Can be a fixed amount or vary based on the effects of the attack
                                    BVA Test Cases:
                                    - Normal damage amount
                                    - Test Pokémon has weakness
                                    - Test Pokémon has resistance 

                            - Effects

                                 - Any attack may or may not have effects. These effects vary greatly between cards, but often apply conditions 

                  - Abilities

                       - Abilities all have a name and an effect.

                       - Similar to Attack effects, the effects of abilities vary greatly.

                       - Unless the card says otherwise, an effect may be used multiple times per turn.

                       - Some effects are passive an should be monitored throughout gameplay.

                  - Weaknesses

                       - A Pokémon card may or may not have a weakness.

                       - If a Pokémon has a weakness, it is weak to a certain energy type. 

                       - Attacks from a Pokémon of the weakness type do double damage to this Pokémon (i.e. a Water type does double damage to a Pokémon that is weak to Water).

                  - Resistances

                       - A Pokémon card may or may not have resistances.

                       - A Pokémon with a resistance takes less damage when attacked by Pokémon of a certain type. 

                       - The amount and type(s) of resistance vary.

                  - Retreat Cost

                       - All Retreat Costs are colorless energy, so the color doesn't matter, only the number of energy.
                            - 1 energy short and attempt to retreat (fail)
                            - Exact amount of energy needed (success)

        - Trainer

             - All Trainer cards have

                  - Trainer Type

                       - Item

                       - Supporter

                       - Stadium

                  - Effects

        - Energy

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



Zones are complete when:

    - Hand: 
        - Each player should start the game with exactly 7 cards in their hand

        - *** Players are unable to view their opponents hand (unless otherwise specified) ***
            - We likely will not enforce this, as we want to run everything on a local machine and it would be difficult
            to distinguish between who is playing at any time

        - Any cards drawn from deck go into their hand

        - Players can get prize cards into their hand

    - Deck: 
        - Each player starts with a full deck of 60 Pokémon cards

        - Maximum of four copies of any card
            - Test exactly 3 cards and attempt to draw a 4th (success)
            - Test exactly 4 cards and attempt to draw a 5th (fail)

            i. EXCEPT basic energy cards

        - Maximum of 2 Energy types per deck
            - Test exactly 1 card and attempt to draw a 2nd (success)
            - Test exactly 2 cards and attempt to draw a 3rd (fail)

        - Nobody may view the deck or alter the deck (unless otherwise specified)

    - Prize Cards: 
        - Each player has their own 6 prize cards at the start of the game

        - Prize cards are face down and can not be viewed by anyone

        - When an opponents Pokémon is knocked out, player picks up a prize card into their hand

        - If all 6 prize cards are won by a player, they win the game
            - Test 5 prize cards do not give player a win
            - Test 6 prize cards do give player a win


    - In-Play Pokémon:
        - In-Play Pokémon consist of both Active and Benched Pokémon
            
        - On players turn:

            • Pokémon can evolve if the player has the next evolution of that Pokémon in their hand

            • Attach an energy card to a Pokémon up to once per turn (1 max!!!)
                
            • Use abilities



    - Active Pokémon: 
        - The top row of each players Pokémon in-play section has a single active Pokémon at all times

        - The active Pokémon takes damage when the opponent attacks

        - Are knocked out after their hp is <= 0
            
        - If either player is unable to have an active Pokémon for whatever reason, they lose
            - Test having exactly 1 active Pokémon does not result in a loss
            - Test having 0 active Pokémon does result in a loss

        - On their turn the active Pokémon can:
            - Retreat (if they have the specified energy cards)

                • After retreating or being knocked out the active Pokémon should be able to be replaced by a currently benched Pokémon

            - Attack (if they have the specified energy cards)


    Bench:
        - The bottom row of each players Pokémon in-play section has between 0-5 Pokémon

        - Every Pokémon on the bench is in-play

        - On their turn the player can choose to:

                • Add a Pokémon from their hand onto the bench

                • Move a Pokémon from the bench to active (if there is not currently an active Pokémon)

    
    Discard Pile:

        - Each player should have their own discard pile off to the side

        - All cards taken out of play go into the discard pile and are out of play

        - All attached cards (i.e energy cards) also go to the discard pile and are out of play



Set-up is complete when:

    - 2 separate players can be created

    - Their decks are created and assigned accordingly

    - Coin can be flipped to decide who gets to play first

    - 7 cards from each players deck can be distributed to their hand

    - Basic Pokémon can be assigned as active Pokémon or added to their bench (face down, unable to be viewed by their opponent)
        
        • If player does not have at least 1 basic Pokémon in their hand, they must reshuffle and their opponent may draw 1 extra card

        • If neither player has at least 1 basic Pokémon, they both reshuffle

        • Reshuffles continue accordingly until both players get at least 1 basic Pokémon and choose a basic Pokémon

        • Player must have at least 1 active Pokémon before the game continues

    - Top 6 cards of the deck should be placed down as Prize cards

    - All In-Play cards are flipped face up

    - Whoever won the coinflip is then able to begin playing first, the other player should not be able to make any changes while it is their opponents turn



Players Turn Functionality is Complete When:

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

            - Stadium cards rules: 

                • Only one Stadium card can be in play at a time

                • When a new Stadium card is played, it should replace the old one

                • Can not play a Stadium card if another Stadium card of the same name is already in play

        - Retreat your active Pokémon (only once per turn)

            - If you retreat, you can still attack that turn with your new Active Pokémon.

        - Use abilities (as many as you want as long as conditions are met)

            - Abilities should be able to be used by active Pokémon

            - Abilities should be able to be used by benched Pokémon


        - Able to attack

            - Active Pokémon must have the right amount and type of energy cards

                • If a player has the right amount and types of energy cards, they choose an attack
                
                • Attack should do the corresponding amount of damage to opponents active Pokémon

                    - If opponents Pokémon is weak to this specific type, then attack damage should be multiplied appropriately

                    - If opponents Pokémon is resistant to that specific type, then damage should be divided/mitigated appropriately

                • Update the new state of the opponents Pokémon, including HP, and if necessary, knocked out


        - End turn and continue to the next steps



TODO: 

In between turns:

    - If a Pokémon ever receives one of these special conditions, mark it with a marker (i.e poison marker, burn marker, etc.). Before the game continues between each turn, apply these special conditions in order.

    1. Poisoned: Between turns, apply a damage counter to signify 10 hp being depleted.

        - (Pokémon can only have 1 poison marker at a time, new ones simply replace old ones).

    2. Burned: Put 2 damage counters on burned Pokémon. Then, flip a coin, if heads, remove the special condition burned.

        - (Pokémon can only have 1 burn marker at a time, new ones simply replace old ones)

    3. Asleep: Turn the card counter-clockwise. This Pokémon cannot attack or retreat, between turns flip a coin, if heads, the Pokémon wakes up.

    4. Paralyzed: Turn the card clockwise. This Pokémon cannot attack or retreat, remove the special condition paralyzed in this step if Pokémon was paralyzed since the beginning of your last turn.

    5. Confused: Turn the card upside-down. Before attacking with this Pokémon, flip a coin. If heads, attack continues normally. If tails, no attack happens and put 3 damage counters on this Pokémon.

    - Note: Because Asleep, Paralyzed, and Confused all rotate the card, only the most recently applied effect is active. If a Pokémon is Asleep and it gets Paralyzed, it is no longer Asleep, only Paralyzed. This does not affect Burning or Poisoned, so a Pokémon could be Asleep, Burning, and Poisoned.




Ending the Game:

    - You win the game if any of the following occurs:

        - You take your last Prize card.

        - Your opponent has no more Pokémon in play. 

        - Your opponent must draw a card, and their deck is empty.

    - If a tie occurs, play Sudden Death

        - you play a new game, but each player only uses 1 Prize Card




Additional Rules:

    - Whenever a Pokémon is knocked out, its controller moves that card and all cards attached to it to the discard pile, then chooses a Pokémon from their bench to replace the knocked-out Pokémon. The other player puts one of their Prize cards into their hand without revealing it.

    - Colorless Energy Requirements and costs can be met with any Type of Energy.
