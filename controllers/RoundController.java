package controllers;

import dao.GameDAO;
import dao.PlayerDAO;
import dao.RoundDAO;
import dto.SSEResponse;
import model.Game;
import model.Player;
import model.Round;
import webserver.WebServerContext;

public class RoundController
{
    /**
     * API handler called to find all rounds for a game
     * @param context
     */
    public static void findAll(WebServerContext context)
    {
        try
        {
            String gameCode = context.getRequest().getParam("gameCode");

            GameDAO gameDAO = new GameDAO();
            Game game = gameDAO.findByCode(gameCode);

            if (game == null)
            {
                context.getResponse().notFound("");
                return;
            }

            RoundDAO roundDAO = new RoundDAO();
            context.getResponse().json(roundDAO.findAll(game.id()));
        }
        catch (Exception e)
        {
            System.err.println(e.getStackTrace());
            context.getResponse().serverError("");
        }
    }

    /**
     * API handler called to terminate a round
     * @param context
     */
    public static void finish(WebServerContext context)
    {
        try
        {
            Long playerId = Long.parseLong(context.getRequest().getParam("playerId"));

            PlayerDAO playerDAO = new PlayerDAO();
            GameDAO gameDAO = new GameDAO();

            Player player = playerDAO.find(playerId);

            if (player == null)
            {
                context.getResponse().notFound("");
                return;
            }

            Game game = gameDAO.find(player.gameId());

            if(game == null)
            {
                context.getResponse().notFound("");
                return;
            }

            if(player.canRevealCards() && game.lastRound().isClueDefined())
            {
                RoundDAO roundDAO = new RoundDAO();
                Round round = roundDAO.createRound(game.id());
                context.getResponse().ok("");
                context.getSSE().emit(game.code(), new SSEResponse<Round>("new-round", round));
            }
            else
            {
                context.getResponse().forbidden("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            context.getResponse().serverError("");
        }
    }
}
