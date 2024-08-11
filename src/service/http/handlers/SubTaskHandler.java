package service.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processGetById(HttpExchange exchange) throws IOException {
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


        String data = Managers.getGson().toJson(subTask);
        sendText(exchange, data);
    }

    @Override
    protected void processGetList(HttpExchange exchange) throws IOException {
        List<SubTask> taskList = taskManager.getAllSubTasks();

        String data = Managers.getGson().toJson(taskList);
        sendText(exchange, data);
    }

    @Override
    protected void processCreate(HttpExchange exchange, String body) throws IOException {
        try {
            SubTask subTask = Managers.getGson().fromJson(body, SubTask.class);
            taskManager.createSubTask(subTask);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isEmpty()) {
            sendNotFound(exchange);
            return;
        }

        taskManager.deleteSubTask(id.get());
    }

    @Override
    protected void processUpdate(HttpExchange h, String body) throws IOException {
        SubTask subTask = Managers.getGson().fromJson(body, SubTask.class);
        taskManager.updateSubTask(subTask, subTask.getId());
    }
}
