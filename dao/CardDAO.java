package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;

import model.Card;
import model.Color;
import model.Word;

public class CardDAO extends DAO
{
    /**
     * Find all cards for a game
     * 
     * @param gameId          Id of the game to return cards
     * @param withMaskedColor Indicate if the color of hidden cards has to be
     *                        extract
     * @return List of cards available for the given game
     */
    public ArrayList<Card> findAll(Long gameId, boolean withMaskedColor)
    {
        ArrayList<Card> cards = new ArrayList<>();

        try
        {
            String query = """
                        SELECT card.id as cardId, word.value as word, color.name as color, card.id_round as round
                        FROM card
                        JOIN word ON word.id = card.id_word
                        JOIN color ON color.id = card.id_color
                        WHERE card.id_game = ?
                        ORDER BY card.`order`
                    """;

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, gameId);
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                String color = results.getString("color");
                boolean revealed = results.getLong("round") != 0;

                cards.add(new Card(results.getLong("cardId"), results.getString("word"),
                        revealed || withMaskedColor ? color : "", revealed));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return cards;
    }

    /**
     * Find all cards revealed during the given round
     * 
     * @param roundId id of the round during with cards has been revealed
     * @return List of cards revealed during the given round
     */
    public ArrayList<Card> findRevealedCardOnRound(Long roundId)
    {
        ArrayList<Card> cards = new ArrayList<>();

        try
        {
            String query = """
                        SELECT card.*, word.value as word, color.name as color
                        FROM card
                        JOIN color ON color.id = card.id_color
                        JOIN word ON word.id = card.id_word
                        WHERE card.id_round = ?;
                    """;

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, roundId);
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                cards.add(new Card(results.getLong("id"), results.getString("word"), results.getString("color"), true));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return cards;
    }

    /**
     * Find card from its id
     * 
     * @param cardId Id of the card to find
     * @return Found card or null
     */
    public Card find(Long cardId)
    {
        try
        {
            String query = """
                        SELECT card.*, word.value as word, color.name as color
                        FROM card
                        JOIN color ON color.id = card.id_color
                        JOIN word ON word.id = card.id_word
                        WHERE card.id = ?;
                    """;

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, cardId);
            ResultSet results = statement.executeQuery();

            if (results.next())
            {
                return new Card(results.getLong("id"), results.getString("word"), results.getString("color"),
                        results.getLong("id_round") != 0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create cards for the game
     * 
     * @param gameId Id of the game to create cards
     */
    public void createCardsForGame(Long gameId)
    {
        try
        {
            ArrayList<Color> remainingCardsColors = colorsToBeApplied();

            WordDAO wordDAO = new WordDAO();
            ArrayList<Word> words = wordDAO.findRandomWords(remainingCardsColors.size());

            Random randomGenerator = new Random();
            int cardOrder = 0;

            for (Word word : words)
            {
                int colorPosition = randomGenerator.nextInt(remainingCardsColors.size());
                this.createCard(gameId, word.id(), remainingCardsColors.get(colorPosition).id(), cardOrder++);
                remainingCardsColors.remove(colorPosition);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Create a new card
     * 
     * @param gameId  Id of the game the card is attached to
     * @param wordId  Id of the word associated to the card
     * @param colorId Id of the color associated to the card
     * @param order   Position of the card in the game cards
     */
    private void createCard(Long gameId, Long wordId, Long colorId, int order)
    {
        try
        {
            String query = "INSERT INTO card (id_game, id_word, id_color, `order`) VALUES (?, ?, ?, ?);";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, gameId);
            statement.setLong(2, wordId);
            statement.setLong(3, colorId);
            statement.setLong(4, order);
            statement.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get list of colors to apply to new cards
     * 
     * @return List of colors to apply to new cards for the game
     */
    private ArrayList<Color> colorsToBeApplied()
    {
        ArrayList<Color> remainingCardsColors = new ArrayList<>();
        ColorDAO colorDAO = new ColorDAO();
        ArrayList<Color> colors = colorDAO.findAll();
        for (Color color : colors)
        {
            for (int i = 0; i < color.occurrence(); ++i)
                remainingCardsColors.add(color);
        }

        return remainingCardsColors;
    }

    /**
     * Set the given card revealed during the given round
     * 
     * @param cardId  Id of the card to update
     * @param roundId Id of the round
     * @return true if update success, else false
     */
    public boolean revealOnRound(Long cardId, Long roundId)
    {
        try
        {
            String query = "UPDATE card SET id_round = ? WHERE id = ?;";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, roundId);
            statement.setLong(2, cardId);
            statement.executeUpdate();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gives the number of blue cards to revealed to win the game
     * @return Number of required blue cards to win a game
     */
    public int getRequiredBlueCardToWin()
    {
        try
        {
            String query = "SELECT occurrence FROM color WHERE name = 'blue';";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            ResultSet results = statement.executeQuery();

            if (results.next())
                return results.getInt("occurrence");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }
}
