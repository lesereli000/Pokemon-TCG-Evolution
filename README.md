# S1G2-Project

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

                  - Damage Counters

                       - Each counter represents 10 damage, so total damage = 10 * damage counters

                  - Attacks

                       - Each attack the Pokémon can use will have:

                            - Name

                            - Energy Requirement

                                 - Number and color are important here.

                            - Damage

                                 - Can be a fixed amount or vary based on the effects of the attack

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



TODO: Zones:

    - Hand

    - Deck

    - Prize Cards

    - Active Poke

    - Bench

    - In Play

    - Discard Pile



Set-up:

    1. Players

        a. There are two players, who each have one deck

    2. Deck

        a. 60 cards

        b. Maximum of four copies of any card

            i. EXCEPT basic energy cards

        c. Maximum of 2 Energy types per deck


Gameplay setup:

    1. Flip coin. Winner decides which player goes first

    2. Shuffle your 60-card deck

    3. Draw the top 7 cards into your hand

    4. If you have ONE OR MORE basic Pokémon in your hand, put ONE face down as your active Pokémon

        - If you do not have any basic Pokémon in your hand: reshuffle hand back into the deck until you get at least 1 basic Pokémon. Every time opponent must reshuffle because they had no basic Pokémon, you may draw 1 extra card

    5. If you have more than ONE basic Pokémon in your hand, put up to 5 additional basic pokemon face down on your bench

    6. Put the top 6 cards of your deck off to the side face down as your Prize cards

    7. Both players flip their Active and Benched Pokémon face up and start the game! 


Players alternate turns starting with whoever won the coin flip above.



Every turn:



    1. Draw a card
        - If there are no cards in your deck at the beginning of your turn, you cannot draw a card, and therefore you lose

    2. Do any of the following options, in any order:
        - Put basic Pokémon cards from your hand onto your bench (as many times as you want)

        - Evolve your Pokémon (as many times as you want up to 5 on the bench)

            - If you have a card in your hand that says “Evolves from X,” and X is the name of a Pokémon you had in play at the beginning of your turn, you may play that card in your hand on top of Pokémon X. 

            - You may evolve a Basic Pokémon to a Stage 1 Pokémon or a Stage 1 Pokémon to a Stage 2 Pokémon. When a Pokémon evolves, it keeps all cards attached to it (Energy cards, Evolution cards, etc.) and any damage counters on it. Any effects of attacks or Special Conditions affecting the Pokémon—such as Asleep, Confused, or Poisoned—end when it evolves. A Pokémon cannot use the attacks or Abilities of its previous Evolution unless a card says so.

        - Attach an Energy card from your hand to one of your Pokémon (only once per turn)

            - Take an Energy card from your hand and put it under your Active Pokémon or one of your Benched Pokémon to indicate that this is Energy it can use. 

        - Play trainer cards (as many as you want, but only one Supporter card and one Stadium card per turn)

            - When you play any Trainer card, do what it says and obey the rule at the bottom of the card, and then put it in the discard pile. You can play as many Item cards as you like. Supporter cards are played like Item cards, but you can play only one Supporter card each turn.

            - Stadium cards have a few special rules: 

                • A Stadium card stays in play when you play it.

                • Only one Stadium card can be in play at a time.

                • If a new one comes into play, discard the old one and end its effects.

                • You can’t play a Stadium card if a card with the same name is already in play.

                • You can play only one Stadium card each turn


        - Retreat your active Pokémon (only once per turn)

            - To retreat, you must discard 1 Energy from your Active Pokémon for each listed in its Retreat Cost. If none are listed, it retreats for free. Then, you switch that retreating Pokémon with a Pokémon from your Bench. Keep all damage counters and all attached cards with each Pokémon when they switch. Pokémon that are Asleep or Paralyzed cannot retreat.

            - When your Active Pokémon goes to your Bench (whether it retreated or got there some other way), some things do go away—Special Conditions and any effects from attacks.

            - If you retreat, you can still attack that turn with your new Active Pokémon.

        - Use abilities (as many as you want as long as conditions are met)

            - Abilities can be used from active or benched Pokémon

    3. Attack

        - To attack, the active Pokémon must have the right amount of energy cards prior to attacking (Make sure they are the right kinds of energy cards). Then player chooses to use the attack and does X damage to opponents active Pokémon.

        - If the opponents active Pokémon has relevant weakness/resistance to the Pokémon of that specific type, then damage is multiplied/mitigated.

        - Put 1 damage counter on opponents Pokémon for each 10 damage done to opponents Pokémon.

        - Check if any Pokémon were knocked out (total damage >= hp).

    4. End of Turn



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
