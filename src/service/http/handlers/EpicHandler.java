package service.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.SubTask;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processGetById(HttpExchange exchange) throws IOException {
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

        String data = Managers.getGson().toJson(epic);
        sendText(exchange, data);
    }

    @Override
    protected void processGetList(HttpExchange exchange) throws IOException {
        List<Epic> epicList = taskManager.getAllEpics();

        String data = Managers.getGson().toJson(epicList);
        sendText(exchange, data);
    }

    @Override
    protected void processCreate(HttpExchange exchange, String body) throws IOException {
        try {
            Epic epic = Managers.getGson().fromJson(body, Epic.class);
            epic.initSubTasks();
            taskManager.createEpic(epic);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
            return;
        }
        sendModify(exchange);
    }

    @Override
    protected void processGetSubtaskOfEpicList(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isEmpty()) {
            sendNotFound(exchange);
            return;
        }

        Epic epic = taskManager.getEpic(id.get());
        List<SubTask> epicList = taskManager.getSubTasksOfEpic(epic);
        String data = Managers.getGson().toJson(epicList);
        sendText(exchange, data);
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isEmpty()) {
            sendNotFound(exchange);
            return;
        }

        try {
            taskManager.deleteEpic(id.get());
        } catch (Exception e) {
            sendInternalServerError(exchange, e);
            return;
        }
        sendModify(exchange);
    }
}
