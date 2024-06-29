package controllers;

import dao.GameDAO;
import dao.PlayerDAO;
import dao.RoundDAO;
import dto.SSEResponse;
import model.Clue;
import model.Game;
import model.Player;
import model.Round;
import webserver.WebServerContext;

public class ClueController
{
    /**
     * API Handler called to set the clue for the current round of the game
     * @param context
     */
    public static void append(WebServerContext context)
    {
        try
        {
            String playerUId = context.getRequest().getParam("playerUId");
            Clue clue = context.getRequest().extractBody(Clue.class);

            PlayerDAO playerDAO = new PlayerDAO();
            GameDAO gameDAO = new GameDAO();
            RoundDAO roundDAO = new RoundDAO();

            Player player = playerDAO.findByUId(playerUId);

            Game game = gameDAO.find(player.gameId());

            if (player == null || game == null)
            {
                context.getResponse().notFound("");
                return;
            }

            Round lastRound = game.lastRound();

            if (player.canGiveClue() == false || lastRound == null || lastRound.isClueDefined())
            {
                context.getResponse().forbidden("");
                return;
            }

            if (roundDAO.updateRoundClue(lastRound.id(), clue))
            {
                context.getResponse().ok("");
                context.getSSE().emit(game.code(), new SSEResponse<Round>("clue", new Round(lastRound.id(), clue.clue(),
                        clue.nbWords(), lastRound.score(), lastRound.revealedCards())));
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        context.getResponse().serverError("");
    }
}
