package webserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("unused")
public class WebServer
{
    //Listening port
    private int listeningPort;

    //HttpServer instance
    private HttpServer server;

    //Webserver router
    private WebServerRouter router;

    //Server side events manager
    private WebServerSSE sse;

    public WebServer()
    {
        this.listeningPort = 80;
        this.server = null;
        this.router = new WebServerRouter();
        this.sse = new WebServerSSE(router);
    }

    public WebServerRouter getRouter()
    {
        return this.router;
    }

    public void listen(int listeningPort) throws IOException
    {
        this.listeningPort = listeningPort;
        this.server = HttpServer.create(new InetSocketAddress(this.listeningPort), 0);

        this.server.createContext("/", (HttpExchange exchange) -> {
            processRequest(exchange);
        });

        this.server.start();
    }

    private void processRequest(HttpExchange exchange)
    {
        WebServerContext context = new WebServerContext(exchange, this.sse);

        try
        {
            WebServerRequest request = context.getRequest();

            if (request.getMethod().equals("OPTIONS"))
            {
                processPreflightRequest(context);
            }
            else
            {
                WebServerRoute route = this.router.findRoute(request);
                request.setParams(route.extractParams(request.getPath()));

                route.run(context);
            }
        }
        catch (WebServerRouteNotFoundException exception)
        {
            context.getResponse().notFound("Not found");
        }
        catch (Exception exception)
        {
            context.getResponse().serverError("Serveur error");
        }
    }

    private void processPreflightRequest(WebServerContext context)
    {
        context.getResponse().ok("");
    }

    public final WebServerSSE getSSE()
    {
        return sse;
    }
}
