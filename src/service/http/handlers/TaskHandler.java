package service.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void processGetById(HttpExchange exchange) throws IOException {
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


        String data = Managers.getGson().toJson(task);
        sendText(exchange, data);
    }

    @Override
    protected void processGetList(HttpExchange exchange) throws IOException {
        List<Task> taskList = taskManager.getAllTasks();
        String data = Managers.getGson().toJson(taskList);
        sendText(exchange, data);
    }

    @Override
    protected void processCreate(HttpExchange exchange, String body) throws IOException {
        try {
            Task task = Managers.getGson().fromJson(body, Task.class);
            taskManager.createTask(task);
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

        taskManager.deleteTask(id.get());
    }

    @Override
    protected void processUpdate(HttpExchange h, String body) throws IOException {
        Task task = Managers.getGson().fromJson(body, Task.class);
        taskManager.updateTask(task.getId(), task);

    }
}
