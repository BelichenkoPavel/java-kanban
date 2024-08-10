package service.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;
import service.http.HttpTaskServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(exchange, body);

        switch (endpoint) {
            case GET_LIST: {
                List<Task> history = taskManager.getPrioritizedTasks();
                try {
                    String data = HttpTaskServer.getGson().toJson(history);
                    sendText(exchange, data);
                } catch (IOException e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(HttpExchange exchange, String body) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equals("GET")) {
            return Endpoint.GET_LIST;
        }

        return Endpoint.UNKNOWN;
    }

    enum Endpoint {GET_LIST, UNKNOWN}
}
