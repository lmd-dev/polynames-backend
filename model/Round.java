package model;

import java.util.ArrayList;

public record Round(Long id, String clue, int nbCardsToFind, int score, ArrayList<Card> revealedCards)
{
    public boolean canNewCardBeRevealed()
    {
        return clue.equals("") == false && isFinish() == false;
    }

    public int revealedBlueCards()
    {
        int count = 0;

        for (Card card : revealedCards())
        {
            if (card.isBlue())
                ++count;
        }

        return count;
    }

    public boolean isFinish()
    {
        if(revealedCards().size() >= nbCardsToFind + 1)
            return true;

        for(Card card: revealedCards)
        {
            if(card.isGrey())
                return true;
        }

        return false;
    }

    public boolean isClueDefined()
    {
        return clue().equals("") == false;
    }

    public int getScore()
    {
        int score = 0;
        int nbBlueCards = 0;

        ArrayList<Card> cards = revealedCards();

        for (int i = 0; i < cards.size(); ++i)
        {
            Card card = cards.get(i);

            if (card.isBlue())
            {
                ++nbBlueCards;

                if (nbBlueCards <= nbCardsToFind())
                    score += nbBlueCards;
                else
                    score += nbBlueCards * nbBlueCards;
            }
        }

        return score;
    }
}
