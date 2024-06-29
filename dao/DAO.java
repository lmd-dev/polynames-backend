package dao;

import java.sql.SQLException;

import database.MySQLDatabase;
import env.Env;

public class DAO
{
    private MySQLDatabase db;

    /**
     * Constructor
     */
    public DAO()
    {
        this.db = null;
    }

    /**
     * Get the current connection to the database. Create connection if not yet
     * established.
     * 
     * @return The curront connection to the database.
     * @throws SQLException
     */
    public MySQLDatabase database() throws SQLException
    {
        if (this.db == null)
        {
            this.db = new MySQLDatabase(Env.get("DB_HOST"), Env.getInt("DB_PORT"), Env.get("DB_NAME"),
                    Env.get("DB_USER"), Env.get("DB_PASSWORD"));
        }

        return this.db;
    }
}
