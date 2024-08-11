package service.http.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
    protected TaskManager taskManager;

    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(exchange, body);

        switch (endpoint) {
            case GET_ONE: {
                try {
                    processGetById(exchange);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            case GET_LIST: {
                try {
                    processGetList(exchange);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            case GET_SUBTASKS_OF_EPIC: {
                try {
                    processGetSubtaskOfEpicList(exchange);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            case CREATE: {
                try {
                    processCreate(exchange, body);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                sendModify(exchange);
                break;
            }
            case UPDATE: {
                try {
                    processUpdate(exchange, body);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                sendModify(exchange);
                break;
            }
            case DELETE: {
                try {
                    processDelete(exchange);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                sendModify(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    protected Endpoint getEndpoint(HttpExchange exchange, String body) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE;
        }

        if (requestMethod.equals("GET")) {
            String[] requestPathParts = requestPath.split("/");

            if (requestPathParts.length == 3) {
                return Endpoint.GET_ONE;
            }

            if (requestPathParts.length == 4) {
                return Endpoint.GET_SUBTASKS_OF_EPIC;
            }

            return Endpoint.GET_LIST;
        }

        if (requestMethod.equals("POST")) {
            if (body.isEmpty()) {
                return Endpoint.UNKNOWN;
            }

            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Optional<Integer> id = getIdFromJson(jsonObject);
            if (id.isEmpty()) {
                return Endpoint.CREATE;
            }

            return Endpoint.UPDATE;
        }

        return Endpoint.UNKNOWN;
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendModify(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, 0);
        h.close();
    }

    public void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    public void sendHasInteractions(HttpExchange h) throws IOException {
        byte[] resp = "Not Acceptable".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    public void sendInternalServerError(HttpExchange h, Exception e) throws IOException {
        String message = "Internal Server Error:" + e.getMessage();
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    public void sendMethodNotAllowed(HttpExchange h) throws IOException {
        String message = "Method Not Allowed";
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(405, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void processGetById(HttpExchange h) throws IOException {
        sendMethodNotAllowed(h);
    }

    protected void processGetList(HttpExchange h) throws IOException {
        sendMethodNotAllowed(h);
    }

    protected void processCreate(HttpExchange h, String body) throws IOException {
        sendMethodNotAllowed(h);
    }

    protected void processGetSubtaskOfEpicList(HttpExchange h) throws IOException {
        sendMethodNotAllowed(h);
    }

    protected void processDelete(HttpExchange h) throws IOException {
        sendMethodNotAllowed(h);
    }

    protected void processUpdate(HttpExchange h, String body) throws IOException {
        sendMethodNotAllowed(h);
    }

    protected Optional<Integer> getIdFromJson(JsonObject jsonObject) {
        try {
            return Optional.of(jsonObject.get("id").getAsInt());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    enum Endpoint { GET_ONE, GET_LIST, CREATE, UPDATE, DELETE, GET_SUBTASKS_OF_EPIC, UNKNOWN }
}
