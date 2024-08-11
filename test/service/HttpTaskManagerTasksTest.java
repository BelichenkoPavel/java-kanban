package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import service.http.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskListTypeToken extends TypeToken<List<Task>> {
}

public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = Managers.getGson();

    String baseUrl = "http://localhost:8080/tasks";
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Добавление задачи")
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        String taskJson = "{\"name\":\"Test\",\"status\":\"NEW\",\"description\":\"Testing task 2\",\"duration\":\"PT5M\",\"startTime\":\"2024-08-10T01:06:50.4736799\"}";

        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась сущность
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.get(0).getName(), "Некорректное имя задачи");


        // создаем задачу с пересечением по времени
        HttpResponse<String> responseDateCollision = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responseDateCollision.statusCode());

        // проверяем, что новая задача не создалась
        tasksFromManager = manager.getAllTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Обновление задачи")
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test", Status.NEW, "Description", Duration.ofHours(5), LocalDateTime.now());
        manager.createTask(task);
        String newName = "New name";
        task.setName(newName);

        String taskJson = gson.toJson(task);

        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что задача обновилась
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(newName, tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Удаление задачи")
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test", Status.NEW, "Description", Duration.ofHours(5), LocalDateTime.now());
        manager.createTask(task);

        URI url = URI.create(baseUrl + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что задача удалена
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Получение списка задач")
    public void testGetTasksList() throws IOException, InterruptedException {
        Task task1 = new Task("Test", Status.NEW, "Description", Duration.ofHours(5), LocalDateTime.now());
        Task task2 = new Task("Test", Status.NEW, "Description", Duration.ofHours(5), LocalDateTime.now().plusHours(5));
        manager.createTask(task1);
        manager.createTask(task2);

        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        ArrayList<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(2, tasks.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Получение задачи по id")
    public void testGetTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // пытаемся получить несуществующую задачу
        HttpResponse<String> responseNotFound = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, responseNotFound.statusCode());

        Task task = new Task("Test", Status.NEW, "Testing", Duration.ofHours(5), LocalDateTime.now());
        manager.createTask(task);

        // пытаемся получить существующую задачу
        HttpResponse<String> responseOk = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, responseOk.statusCode());

        Task responseTask = gson.fromJson(responseOk.body(), Task.class);

        assertEquals(task.getName(), responseTask.getName(), "Некорректное имя задачи");
    }
} 