package model;

public record Player(Long id, String name, Long gameId, Role role)
{
    public boolean canRevealCards()
    {
        if (role() != null)
            return role().canSeeCardsColor() == false;

        return false;
    }

    public boolean canSeeMaskedColor()
    {
        if (role() != null)
            return role().canSeeCardsColor() == true;

        return false;
    }

    public boolean canGiveClue()
    {
        if (role() != null)
            return role().canSeeCardsColor() == true;

        return false;
    }
}
