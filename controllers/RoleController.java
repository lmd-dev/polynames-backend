package controllers;

import java.util.ArrayList;

import dao.GameDAO;
import dao.PlayerDAO;
import dao.RoleDAO;
import dto.SSEResponse;
import model.Game;
import model.Player;
import model.Role;
import webserver.WebServerContext;

public class RoleController
{
    /**
     * API Handler called to get the list of all available players roles
     * 
     * @param context
     */
    public static void findAll(WebServerContext context)
    {
        RoleDAO roleDAO = new RoleDAO();
        ArrayList<Role> roles = roleDAO.findAll();

        context.getResponse().json(roles);
    }

    /**
     * API Handler called to get the role of the given player
     * 
     * @param context
     */
    public static void findForPlayer(WebServerContext context)
    {
        Long playerId = Long.parseLong(context.getRequest().getParam("playerId"));

        PlayerDAO playerDAO = new PlayerDAO();
        Player player = playerDAO.find(playerId);

        if (player != null)
        {
            context.getResponse().json(player.role());
        }
        else
        {
            context.getResponse().notFound("");
        }
    }

    /**
     * API Handler called to choose the role of a player
     * 
     * @param context
     */
    public static void chooseRole(WebServerContext context)
    {
        try
        {
            Long playerId = Long.parseLong(context.getRequest().getParam("playerId"));
            Role role = context.getRequest().extractBody(Role.class);

            PlayerDAO playerDAO = new PlayerDAO();
            GameDAO gameDAO = new GameDAO();

            Player player = playerDAO.find(playerId);
            if (player == null)
            {
                context.getResponse().notFound("Player not found.");
                return;
            }

            Game game = gameDAO.find(player.gameId());
            if (game == null)
            {
                context.getResponse().notFound("Game not found.");
                return;
            }

            if (playerDAO.chooseRole(playerId, role.id()))
            {
                Player otherPlayer = playerDAO.findOtherPlayer(playerId);

                if (otherPlayer != null)
                {
                    RoleDAO roleDAO = new RoleDAO();
                    Role otherRole = roleDAO.findOtherRole(role.id());

                    playerDAO.chooseRole(otherPlayer.id(), otherRole.id());
                }

                context.getResponse().ok("");
                context.getSSE().emit(game.code(), new SSEResponse<Object>("role", playerDAO.findAll(game.id())));
            }
            else
            {
                context.getResponse().serverError("Can't choose role for player");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            context.getResponse().serverError("");
        }
    }
}
