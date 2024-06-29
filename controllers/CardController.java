package controllers;

import java.util.ArrayList;

import dao.CardDAO;
import dao.GameDAO;
import dao.PlayerDAO;
import dao.RoundDAO;
import dto.SSEResponse;
import model.Card;
import model.Game;
import model.Player;
import model.Round;
import webserver.WebServerContext;

public class CardController
{
    /**
     * API Handler called to get the list of all cards visible for the player
     * @param context
     */
    public static void findAll(WebServerContext context)
    {
        ArrayList<Card> cards = new ArrayList<>();

        try
        {
            String playerUId = context.getRequest().getParam("playerUId");

            PlayerDAO playerDAO = new PlayerDAO();
            Player player = playerDAO.findByUId(playerUId);

            CardDAO cardDAO = new CardDAO();
            cards = cardDAO.findAll(player.gameId(), player.canSeeMaskedColor());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        context.getResponse().json(cards);
    }

    /**
     * API Handler called to reveal a card
     * 
     * @param context
     */
    public static void reveal(WebServerContext context)
    {
        try
        {
            String playerUId = context.getRequest().getParam("playerUId");
            Long cardId = Long.parseLong(context.getRequest().getParam("cardId"));

            PlayerDAO playerDAO = new PlayerDAO();
            GameDAO gameDAO = new GameDAO();
            RoundDAO roundDAO = new RoundDAO();
            CardDAO cardDAO = new CardDAO();

            Player player = playerDAO.findByUId(playerUId);
            Game game = gameDAO.find(player.gameId());
            Card card = cardDAO.find(cardId);

            if (player == null || game == null || card == null)
            {
                context.getResponse().notFound("");
                return;
            }

            if (game.revealCard(player, card))
            {
                context.getResponse().ok("");

                Round lastRound = game.lastRound();
                int score = lastRound.getScore();

                cardDAO.revealOnRound(cardId, lastRound.id());
                roundDAO.updateRoundScore(lastRound.id(), score);

                context.getSSE().emit(game.code(), new SSEResponse<Card>("reveal", card));
                context.getSSE().emit(game.code(), new SSEResponse<Round>("score", new Round(lastRound.id(),
                        lastRound.clue(), lastRound.nbCardsToFind(), score, lastRound.revealedCards())));

                if (game.isLost())
                {
                    context.getSSE().emit(game.code(),
                            new SSEResponse<ArrayList<Card>>("lost-game", cardDAO.findAll(game.id(), true)));
                    gameDAO.remove(game.id());
                }
                else if (game.isWon(cardDAO.getRequiredBlueCardToWin()))
                {
                    context.getSSE().emit(game.code(),
                            new SSEResponse<Object>("won-game", cardDAO.findAll(game.id(), true)));
                    gameDAO.remove(game.id());
                }
                else if (lastRound.isFinish())
                {
                    Round newRound = roundDAO.createRound(game.id());
                    context.getSSE().emit(game.code(), new SSEResponse<Round>("new-round", newRound));
                }

                return;
            }
            else
            {
                context.getResponse().forbidden("");
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
