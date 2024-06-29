package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Color;

public class ColorDAO extends DAO
{
    /**
     * Find all available colors
     * @return List of available colors for cards
     */
    public ArrayList<Color> findAll()
    {
        ArrayList<Color> colors = new ArrayList<>();

        try
        {
            String query = "SELECT * FROM color;";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                colors.add(new Color(results.getLong("id"), results.getString("name"), results.getInt("occurrence")));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return colors;
    }
}
