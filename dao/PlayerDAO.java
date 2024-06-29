package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import database.MySQLDatabase;
import model.Player;

public class PlayerDAO extends DAO
{
    /**
     * Find all players registered for the game
     * 
     * @param gameId Id of the game to find players
     * @return List of players
     */
    public ArrayList<Player> findAll(Long gameId)
    {
        ArrayList<Player> players = new ArrayList<>();

        try
        {
            String query = """
                SELECT *, id_game as gameId 
                FROM player 
                WHERE id_game = ? 
                ORDER BY id;
            """;

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, gameId);
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                players.add(makePlayerFromResult(results));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return players;
    }

    /**
     * Find player from its id
     * 
     * @param playerId Id of the player to find
     * @return Found player or null
     */
    public Player find(Long playerId)
    {
        try
        {
            String query = """
                        SELECT player.*, game.id as gameId
                        FROM player
                        JOIN game ON game.id = player.id_game
                        WHERE player.id = ?;
                    """;

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, playerId);
            ResultSet results = statement.executeQuery();

            if (results.next())
            {
                return this.makePlayerFromResult(results);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find the second player of the game
     * 
     * @param playerId Id of the first player
     * @return Found player or null
     */
    public Player findOtherPlayer(Long playerId)
    {
        try
        {
            String query = """
                SELECT player.*, game.id as gameId
                FROM player
                JOIN game ON game.id = player.id_game
                WHERE player.id != ? AND id_game = ( SELECT id_game FROM player WHERE player.id = ?);
            """;

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, playerId);
            statement.setLong(2, playerId);
            ResultSet results = statement.executeQuery();

            if (results.next())
            {
                return makePlayerFromResult(results);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Creates new player
     * 
     * @param gameId     If of the game to register player
     * @param playerName Name of the player
     * @return Created player or null
     */
    public Player create(Long gameId, String playerName)
    {
        try
        {
            MySQLDatabase db = this.database();

            String query = """
                INSERT INTO player (id_game, name) 
                VALUES (?, ?);
            """;

            PreparedStatement statement = db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, gameId);
            statement.setString(2, playerName);
            statement.execute();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
            {
                Long playerId = generatedKeys.getLong(1);
                return this.find(playerId);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Delete player
     * 
     * @param gameId     If of the game to register player
     * @return true if suppression is sucessfull, else false
     */
    public boolean remove(Long playerId)
    {
        try
        {
            String query = "DELETE FROM player WHERE id = ?;";

            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, playerId);
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
     * Chooses role for the given player
     * 
     * @param playerId Id of the player to update role
     * @param roleId   Id of the role to apply to player
     * @return true if applying success, else false
     */
    public boolean chooseRole(Long playerId, Long roleId)
    {
        try
        {
            String query = """
                UPDATE player 
                SET id_role = ? 
                WHERE id = ?;
            """;
            
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, roleId);
            statement.setLong(2, playerId);
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
     * Make player record from given data
     * 
     * @param result Data to create player record
     * @return Player record or null
     */
    private Player makePlayerFromResult(ResultSet result)
    {
        try
        {
            RoleDAO roleDAO = new RoleDAO();

            return new Player(
                result.getLong("id"), 
                result.getString("name"), 
                result.getLong("gameId"),
                roleDAO.find(result.getLong("id_role"))
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
