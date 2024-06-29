package controllers;

import java.util.ArrayList;

import dao.GameDAO;
import dao.PlayerDAO;
import dto.SSEResponse;
import model.Game;
import model.Player;
import webserver.WebServerContext;

public class PlayerController
{
    /**
     * API handler called to get a player from its id
     * @param context
     */
    public static void find(WebServerContext context)
    {
        Long playerId = Long.parseLong(context.getRequest().getParam("playerId"));

        PlayerDAO playerDAO = new PlayerDAO();
        Player player = playerDAO.find(playerId);

        if (player == null)
        {
            context.getResponse().notFound("");
            return;
        }

        context.getResponse().json(player);
    }

    /**
     * API handler called to get the players list for the given game
     * @param context
     */
    public static void findAll(WebServerContext context)
    {
        String gameCode = context.getRequest().getParam("gameCode");

        GameDAO gameDAO = new GameDAO();
        Game game = gameDAO.findByCode(gameCode);

        if (game == null)
        {
            context.getResponse().notFound("");
            return;
        }

        PlayerDAO playerDAO = new PlayerDAO();
        context.getResponse().json(playerDAO.findAll(game.id()));
    }

    /**
     * API handler called to remove a player from a game
     * @param context
     */
    public static void remove(WebServerContext context)
    {
        try
        {
            String gameCode = context.getRequest().getParam("gameCode");
            Long playerId = Long.parseLong(context.getRequest().getParam("playerId"));

            PlayerDAO playerDAO = new PlayerDAO();
            Player player = playerDAO.find(playerId);

            if (player == null)
            {
                context.getResponse().notFound("");
                return;
            }

            if (playerDAO.remove(playerId) == false)
            {
                context.getResponse().serverError("");
                return;
            }

            context.getResponse().ok("");

            ArrayList<Player> registeredPlayers = playerDAO.findAll(player.gameId());
            if (registeredPlayers.size() == 0)
            {
                GameDAO gameDAO = new GameDAO();
                gameDAO.remove(player.gameId());
            }
            else
            {
                context.getSSE().emit(gameCode, new SSEResponse<ArrayList<Player>>("leave-game", registeredPlayers));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
