package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import model.Game;

public class GameDAO extends DAO
{
    /**
     * Find the game attached to the given id
     * @param gameId Id of the game to find
     * @return The found game or null
     */
    public Game find(Long gameId)
    {
        try
        {
            String query = "SELECT * FROM game WHERE id = ?;";

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, gameId);
            ResultSet results = statement.executeQuery();

            if (results.next())
            {
                return this.makeGameFromResult(results);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find the game from its unique code
     * @param gameCode Unique code of the game
     * @return The found game or null
     */
    public Game findByCode(String gameCode)
    {
        try
        {
            String query = "SELECT * FROM game WHERE code = ?;";

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setString(1, gameCode);
            ResultSet results = statement.executeQuery();

            if (results.next())
            {
                return this.makeGameFromResult(results);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new game
     * @return The created game or null
     */
    public Game create()
    {
        try
        {
            String query = "INSERT INTO game (STATUS) VALUES ('InProgress');";

            PreparedStatement statement = this.database().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.execute();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
            {
                Long gameId = generatedKeys.getLong(1);

                this.createCards(gameId);
                this.createRound(gameId);

                return this.find(gameId);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * remove a game from its id
     * @param gameId Id of the game to remove
     * @return true if the game has been removed, else false
     */
    public boolean remove(Long gameId)
    {
        try
        {
            String query = "DELETE FROM game WHERE id = ?;";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, gameId);
            statement.execute();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Make game record from data in the result set
     * @param result Data to create the game
     * @return Game record or null
     */
    private Game makeGameFromResult(ResultSet result)
    {
        try
        {
            Long gameId = result.getLong("id");
            RoundDAO roundDAO = new RoundDAO();

            return new Game(gameId, result.getString("code"), result.getString("status"), roundDAO.findAll(gameId));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create cards for the game
     * @param gameId Id of the game to create cards
     */
    private void createCards(Long gameId)
    {
        try
        {
            CardDAO cardDAO = new CardDAO();
            cardDAO.createCardsForGame(gameId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Create round for the game
     * @param gameId Id of the game to create round
     */
    private void createRound(Long gameId)
    {
        try
        {
            RoundDAO roundDAO = new RoundDAO();
            roundDAO.createRound(gameId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
