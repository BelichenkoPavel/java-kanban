package service.http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;
import service.http.HttpTaskServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(exchange, body);

        switch (endpoint) {
            case GET_TASK: {
                Optional<Integer> id = getId(exchange);

                if (id.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                Task task = taskManager.getTask(id.get());

                if (task == null) {
                    sendNotFound(exchange);
                    return;
                }

                try {
                    String data = HttpTaskServer.getGson().toJson(task);
                    sendText(exchange, data);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            case GET_TASKS: {
                try {
                    List<Task> taskList = taskManager.getAllTasks();
                    String data = HttpTaskServer.getGson().toJson(taskList);
                    sendText(exchange, data);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                break;
            }
            case CREATE_TASK: {
                try {
                    Task task = HttpTaskServer.getGson().fromJson(body, Task.class);
                    taskManager.createTask(task);
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
            case UPDATE_TASK: {
                try {
                    Task task = HttpTaskServer.getGson().fromJson(body, Task.class);
                    taskManager.updateTask(task.getId(), task);
                } catch (Exception e) {
                    sendInternalServerError(exchange, e);
                    break;
                }
                sendModify(exchange);
                break;
            }
            case DELETE_TASK: {
                Optional<Integer> id = getId(exchange);

                if (id.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                try {
                    taskManager.deleteTask(id.get());
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
            return Endpoint.DELETE_TASK;
        }

        if (requestMethod.equals("GET")) {
            String[] requestPathParts = requestPath.split("/");

            if (requestPathParts.length == 3) {
                return Endpoint.GET_TASK;
            }

            return Endpoint.GET_TASKS;
        }

        if (requestMethod.equals("POST")) {
            if (body.isEmpty()) {
                return Endpoint.UNKNOWN;
            }

            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Optional<Integer> id = getIdFromJson(jsonObject);
            if (id.isEmpty()) {
                return Endpoint.CREATE_TASK;
            }

            return Endpoint.UPDATE_TASK;
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

    enum Endpoint {GET_TASKS, GET_TASK, CREATE_TASK, UPDATE_TASK, DELETE_TASK, UNKNOWN}
}
