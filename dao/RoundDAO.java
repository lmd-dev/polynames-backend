package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import model.Clue;
import model.Round;

public class RoundDAO extends DAO
{
    /**
     * Finds all rounds for a game
     * @param gameId Id of the game to find rounds
     * @return List of found rounds
     */
    public ArrayList<Round> findAll(Long gameId)
    {
        ArrayList<Round> rounds = new ArrayList<>();

        try
        {
            String query = "SELECT * FROM round WHERE id_game = ?;";

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, gameId);
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                CardDAO cardDAO = new CardDAO();
                Long roundId = results.getLong("id");

                rounds.add(new Round(roundId, results.getString("clue"), results.getInt("nb_cards_to_find"),
                        results.getInt("score"), cardDAO.findRevealedCardOnRound(roundId)));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return rounds;
    }

    /**
     * Creates new round for the given game
     * @param gameId Id of the game to create a new round
     * @return Created round or null
     */
    public Round createRound(Long gameId)
    {
        try
        {
            String query = "INSERT INTO round (id_game) VALUES (?);";
            PreparedStatement statement = this.database().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, gameId);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
            {
                Long roundId = generatedKeys.getLong(1);

                return new Round(roundId, "", 0, 0, new ArrayList<>());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Updates round with new clue data
     * @param roundId Id of the round to update
     * @param clue Clue to set on the round
     * @return true if update success, else false
     */
    public boolean updateRoundClue(Long roundId, Clue clue)
    {
        try
        {
            String query = "UPDATE round SET clue = ?, nb_cards_to_find = ? WHERE id = ?;";

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setString(1, clue.clue());
            statement.setInt(2, clue.nbWords());
            statement.setLong(3, roundId);

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
     * Updates score of the round
     * @param roundId Id of the round to update
     * @param score New score of the round
     * @return true if update success, else false
     */
    public boolean updateRoundScore(Long roundId, int score)
    {
        try
        {
            String query = "UPDATE round SET score = ? WHERE id = ?;";

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setInt(1, score);
            statement.setLong(2, roundId);

            statement.executeUpdate();

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}
