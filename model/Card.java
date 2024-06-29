package model;

public record Card(Long id, String word, String color, boolean revealed)
{
    public boolean isBlack()
    {
        return color.equals("black");
    }

    public boolean isGrey()
    {
        return color.equals("grey");
    }

    public boolean isBlue()
    {
        return color.equals("blue");
    }
}
