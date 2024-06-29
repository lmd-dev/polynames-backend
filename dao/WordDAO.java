package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Word;

public class WordDAO extends DAO
{
    /**
     * Finds list of random words
     * @param numberOfWords Number of words to include in the returned list
     * @return List of random words
     */
    public ArrayList<Word> findRandomWords(int numberOfWords)
    {
        ArrayList<Word> words = new ArrayList<>();

        try
        {
            String query = "SELECT id, value, RAND() as random FROM word ORDER BY random LIMIT ?;";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setInt(1, numberOfWords);
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                words.add(new Word(results.getLong("id"), results.getString("value")));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return words;
    }
}
