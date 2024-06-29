package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Role;

public class RoleDAO extends DAO
{
    /**
     * Find all available roles
     * @return List of Role
     */
    public ArrayList<Role> findAll()
    {
        ArrayList<Role> roles = new ArrayList<>();

        try
        {
            String query = "SELECT * FROM role ORDER BY name ASC;";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            ResultSet results = statement.executeQuery();

            while (results.next())
            {
                roles.add(makeRoleFromResults(results));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return roles;
    }

    /**
     * Find role from its id
     * @param roleId Id of the role to find
     * @return Foudn role or null
     */
    public Role find(Long roleId)
    {
        try
        {
            String query = "SELECT * FROM role WHERE id = ?;";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, roleId);
            ResultSet results = statement.executeQuery();

            if (results.next())
            {
                return makeRoleFromResults(results);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find the second available role
     * @param roleId Id of the first availalbe role
     * @return Found role or null
     */
    public Role findOtherRole(Long roleId)
    {
        try
        {
            String query = "SELECT * FROM role WHERE id != ?;";
            PreparedStatement statement = this.database().prepareStatement(query, 0);
            statement.setLong(1, roleId);
            ResultSet results = statement.executeQuery();

            if (results.next())
            {
                return makeRoleFromResults(results);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Makes role record from data
     * @param result Data to make record
     * @return Role record or null
     */
    private Role makeRoleFromResults(ResultSet result)
    {
        try
        {
            return new Role(result.getLong("id"), result.getString("name"), result.getInt("canSeeCardsColor") == 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
