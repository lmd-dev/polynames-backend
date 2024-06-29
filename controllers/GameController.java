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

public class GameController
{
    /**
     * API Handler called to get a game from its code
     * @param context
     */
    public static void find(WebServerContext context)
    {
        String gameCode = context.getRequest().getParam("gameCode");

        GameDAO gameDAO = new GameDAO();
        Game game = gameDAO.findByCode(gameCode);

        if(game == null)
        {
            context.getResponse().notFound("");
            return;
        }

        context.getResponse().json(game);
    }

    /**
     * API Handler called to create a new game
     * 
     * @param context
     */
    public static void createGame(WebServerContext context)
    {
        try
        {
            GameDAO dao = new GameDAO();
            Game createdGame = dao.create();

            if (createdGame != null)
            {
                context.getResponse().ok(createdGame.code());
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        context.getResponse().serverError("");
    }

    /**
     * API Handler called to join a game
     * 
     * @param context
     */
    public static void joinGame(WebServerContext context)
    {
        try
        {
            String gameCode = context.getRequest().getParam("gameCode");
            Player player = context.getRequest().extractBody(Player.class);

            GameDAO gameDAO = new GameDAO();
            PlayerDAO playerDAO = new PlayerDAO();

            Game game = gameDAO.findByCode(gameCode);

            if (game == null)
            {
                context.getResponse().notFound("");
                return;
            }

            ArrayList<Player> registeredPlayers = playerDAO.findAll(game.id());

            if (registeredPlayers.size() < 2)
            {
                player = playerDAO.create(game.id(), player.name());
                context.getResponse().ok(player.id().toString());

                if(registeredPlayers.size() == 1)
                {
                    Player firstPlayer = registeredPlayers.get(0);
                    if(firstPlayer.role() != null)
                    {
                        RoleDAO roleDAO = new RoleDAO();
                        Role playerRole = roleDAO.findOtherRole(firstPlayer.role().id());
                        playerDAO.chooseRole(player.id(), playerRole.id());

                        player = new Player(player.id(), player.name(), player.gameId(), playerRole);
                    }
                }

                registeredPlayers.add(player);
                context.getSSE().emit(gameCode, new SSEResponse<ArrayList<Player>>("join-game", registeredPlayers));
            }
            else
            {
                context.getResponse().forbidden("");
            }

        }
        catch (Exception e)
        {
            context.getResponse().serverError("");
        }
    }
}
