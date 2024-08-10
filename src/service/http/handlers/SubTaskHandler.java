package service.http.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;
import service.TaskManager;
import service.http.HttpTaskServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(exchange, body);

        switch (endpoint) {
            case GET_SUBTASK: {
                Optional<Integer> id = getId(exchange);

                if (id.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                SubTask subTask = taskManager.getSubTask(id.get());

                if (subTask == null) {
                    sendNotFound(exchange);
                    return;
                }

                try {
                    String data = HttpTaskServer.getGson().toJson(subTask);
                    sendText(exchange, data);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            case GET_SUBTASKS: {
                List<SubTask> taskList = taskManager.getAllSubTasks();
                try {
                    String data = HttpTaskServer.getGson().toJson(taskList);
                    sendText(exchange, data);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            case CREATE_SUBTASK: {
                try {
                    SubTask subTask = HttpTaskServer.getGson().fromJson(body, SubTask.class);
                    taskManager.createSubTask(subTask);
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                    break;
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                sendModify(exchange);
                break;
            }
            case UPDATE_SUBTASK: {
                SubTask subTask = HttpTaskServer.getGson().fromJson(body, SubTask.class);
                try {
                    taskManager.updateSubTask(subTask, subTask.getId());
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                sendModify(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                Optional<Integer> id = getId(exchange);

                if (id.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                try {
                    taskManager.deleteSubTask(id.get());
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

    private Endpoint getEndpoint(HttpExchange exchange, String body) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_SUBTASK;
        }

        if (requestMethod.equals("GET")) {
            String[] requestPathParts = requestPath.split("/");

            if (requestPathParts.length == 3) {
                return Endpoint.GET_SUBTASK;
            }

            return Endpoint.GET_SUBTASKS;
        }

        if (requestMethod.equals("POST")) {
            if (body.isEmpty()) {
                return Endpoint.UNKNOWN;
            }

            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Optional<Integer> id = getIdFromJson(jsonObject);
            if (id.isEmpty()) {
                return Endpoint.CREATE_SUBTASK;
            }

            return Endpoint.UPDATE_SUBTASK;
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

    enum Endpoint {GET_SUBTASKS, GET_SUBTASK, CREATE_SUBTASK, UPDATE_SUBTASK, DELETE_SUBTASK, UNKNOWN}
}
