package service.http.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.SubTask;
import service.TaskManager;
import service.http.HttpTaskServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(exchange, body);

        switch (endpoint) {
            case GET_EPIC: {
                Optional<Integer> id = getId(exchange);

                if (id.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                Epic epic = taskManager.getEpic(id.get());

                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }

                try {
                    String data = HttpTaskServer.getGson().toJson(epic);
                    sendText(exchange, data);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    return;
                }
                break;
            }
            case GET_SUBTASKS_OF_EPIC: {
                Optional<Integer> id = getId(exchange);

                if (id.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                Epic epic = taskManager.getEpic(id.get());
                List<SubTask> epicList = taskManager.getSubTasksOfEpic(epic);
                try {
                    String data = HttpTaskServer.getGson().toJson(epicList);
                    sendText(exchange, data);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    return;
                }
                break;
            }
            case GET_EPICS: {
                List<Epic> epicList = taskManager.getAllEpics();
                try {
                    String data = HttpTaskServer.getGson().toJson(epicList);
                    sendText(exchange, data);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    return;
                }
                break;
            }
            case CREATE_EPIC: {
                try {
                    Epic epic = HttpTaskServer.getGson().fromJson(body, Epic.class);
                    epic.initSubTasks();
                    taskManager.createEpic(epic);
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                    return;
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    return;
                }
                sendModify(exchange);
                break;
            }
            case DELETE_EPIC: {
                Optional<Integer> id = getId(exchange);

                if (id.isEmpty()) {
                    sendText(exchange, "");
                    return;
                }

                try {
                    taskManager.deleteEpic(id.get());
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    return;
                }
                sendModify(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(HttpExchange exchange, String body) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_EPIC;
        }

        if (requestMethod.equals("GET")) {
            String[] requestPathParts = requestPath.split("/");

            if (requestPathParts.length == 3) {
                return Endpoint.GET_EPIC;
            }

            if (requestPathParts.length == 4) {
                return Endpoint.GET_SUBTASKS_OF_EPIC;
            }

            return Endpoint.GET_EPICS;
        }

        if (requestMethod.equals("POST")) {
            if (body.isEmpty()) {
                return Endpoint.UNKNOWN;
            }

            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Optional<Integer> id = getIdFromJson(jsonObject);
            if (id.isEmpty()) {
                return Endpoint.CREATE_EPIC;
            }
        }

        return Endpoint.UNKNOWN;
    }

    private Optional<Integer> getIdFromJson(JsonObject jsonObject) {
        try {
            return Optional.of(jsonObject.get("id").getAsInt());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    enum Endpoint {GET_EPICS, GET_EPIC, GET_SUBTASKS_OF_EPIC, CREATE_EPIC, DELETE_EPIC, UNKNOWN}
}
