package application;

import controllers.CardController;
import controllers.ClueController;
import controllers.GameController;
import controllers.PlayerController;
import controllers.RoleController;
import controllers.RoundController;
import env.Env;
import webserver.WebServer;

/**
 * Polynames application
 */
public class Polynames
{
    // Internal webserver managing API request and Server side events
    private WebServer webServer;

    /**
     * Constructor
     */
    public Polynames()
    {
        webServer = null;
    }

    /**
     * Starts the application
     */
    public void start()
    {
        if (this.tryToStartWebServer(Env.getInt("WEBSERVER_PORT")))
        {
            this.setupApiRoutes();
        }
    }

    /**
     * Starts the webserver
     * @param listeningPort Port the webserver has to listen to
     * @return true if webserver starts successfully, else false
     */
    private boolean tryToStartWebServer(int listeningPort)
    {
        try
        {
            this.webServer = new WebServer();
            this.webServer.listen(listeningPort);

            System.out.println(String.format("Server started and listening on %d", listeningPort));

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Setups all the routes of the API
     */
    private void setupApiRoutes()
    {
        this.webServer.getRouter().get("/games/:gameCode", GameController::find);
        this.webServer.getRouter().get("/games/:gameCode/players", PlayerController::findAll);
        this.webServer.getRouter().get("/games/:gameCode/rounds", RoundController::findAll);
        this.webServer.getRouter().get("/games/:gameCode/cards/:playerUId", CardController::findAll);
        this.webServer.getRouter().post("/games", GameController::createGame);
        this.webServer.getRouter().post("/games/:gameCode", GameController::joinGame);
        this.webServer.getRouter().post("/games/:gameCode/cards/:playerUId/:cardId", CardController::reveal);
        this.webServer.getRouter().post("/games/:gameCode/rounds/:playerUId", RoundController::finish);
        this.webServer.getRouter().post("/games/:gameCode/clues/:playerUId", ClueController::append);
        this.webServer.getRouter().delete("/games/:gameCode/players/:playerUId", PlayerController::remove);

        this.webServer.getRouter().get("/players/:playerUId", PlayerController::find);

        this.webServer.getRouter().get("/roles", RoleController::findAll);
        this.webServer.getRouter().get("/roles/:playerUId", RoleController::findForPlayer);
        this.webServer.getRouter().post("/roles/:playerUId", RoleController::chooseRole);
    }
}
