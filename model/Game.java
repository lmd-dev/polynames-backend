package model;

import java.util.ArrayList;

public record Game(Long id, String code, String status, ArrayList<Round> rounds)
{
    /**
     * Get the last round of the fame
     * @return Last round or null
     */
    public Round lastRound()
    {
        int roundsSize = rounds().size();

        if (roundsSize != 0)
        {
            return rounds().get(roundsSize - 1);
        }

        return null;
    }

    /**
     * Try to reveal a card
     * @param player Player who reveal the card
     * @param card Card to reveal
     * @return true if the card has been revealed, else false
     */
    public boolean revealCard(Player player, Card card)
    {
        Round lastRound = lastRound();

        if (player.canRevealCards() && lastRound != null)
        {
            if (card.revealed() == false && lastRound.canNewCardBeRevealed())
            {
                lastRound.revealedCards().add(card);
                return true;
            }
        }

        return false;
    }

    /**
     * Indicates if the game is won
     * @param requiredBlueCards How many blue cards has to be revealed to win the game
     * @return true if the game is won, else false
     */
    public boolean isWon(int requiredBlueCards)
    {
        int revealedBlueCards = 0;

        for (Round round : rounds())
        {
            revealedBlueCards += round.revealedBlueCards();
        }

        return revealedBlueCards == requiredBlueCards;
    }

    /**
     * Indicates if the game is loast
     * @return true if the game is lost (black card revealed), else false
     */
    public boolean isLost()
    {
        for (Card card : lastRound().revealedCards())
        {
            if (card.isBlack())
                return true;
        }

        return false;
    }
}
