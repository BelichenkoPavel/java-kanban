package service.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;
import service.http.HttpTaskServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(exchange);

        switch (endpoint) {
            case GET_HISTORY: {
                List<Task> history = taskManager.getHistory();
                try {
                    String data = HttpTaskServer.getGson().toJson(history);
                    sendText(exchange, data);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }

        return Endpoint.UNKNOWN;
    }

    enum Endpoint {GET_HISTORY, UNKNOWN}
}
