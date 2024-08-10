package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
}

public class HttpTaskManagerSubTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    String baseUrl = "http://localhost:8080/subtasks";
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskManagerSubTasksTest() throws IOException {
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
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        manager.createEpic(epic);
        String subTaskJson = "{\"name\":\"Test\",\"status\":\"NEW\",\"description\":\"Testing subtask 2\",\"idEpic\":1,\"duration\":\"PT5M\",\"startTime\":\"2024-08-10T01:06:50.4736799\"}";

        // создаём HTTP-клиент и запрос
        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        // вызываем запрос на создание подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна подзадача с корректным именем
        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");

        // создаем подзадачу с пересечением по времени
        HttpResponse<String> responseDateCollision = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responseDateCollision.statusCode());

        // проверяем, что новая подзадача не создалась
        tasksFromManager = manager.getAllSubTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        manager.createEpic(epic);
        SubTask subtask = new SubTask("Test", Status.NEW, "Description", epic.getId(), Duration.ofHours(5), LocalDateTime.now());
        manager.createSubTask(subtask);
        String newName = "New name";
        subtask.setName(newName);

        String subTaskJson = gson.toJson(subtask);
        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        // вызываем запрос на обновление подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась сущность
        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals(newName, tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        manager.createEpic(epic);
        SubTask subtask = new SubTask("Test", Status.NEW, "Description", epic.getId(), Duration.ofHours(5), LocalDateTime.now());
        manager.createSubTask(subtask);

        URI url = URI.create(baseUrl + "/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем запрос на обновление подзадачи
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась сущность
        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество Подзадач");
    }

    @Test
    public void testGetSubTasksList() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        manager.createEpic(epic);
        SubTask task1 = new SubTask("Test", Status.NEW, "Testing", epic.getId(), Duration.ofHours(5), LocalDateTime.now());
        SubTask task2 = new SubTask("Test", Status.NEW, "Description", epic.getId(), Duration.ofHours(5), LocalDateTime.now().plusHours(5));
        manager.createSubTask(task1);
        manager.createSubTask(task2);

        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание подзадач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        ArrayList<SubTask> subTasks = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertEquals(2, subTasks.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        URI url = URI.create(baseUrl + "/" + 2);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // пытаемся получить несуществующую подзадачу
        HttpResponse<String> responseNotFound = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, responseNotFound.statusCode());

        Epic epic = new Epic("Test", "Description");
        manager.createEpic(epic);
        SubTask subtask = new SubTask("Test", Status.NEW, "Description", epic.getId(), Duration.ofHours(5), LocalDateTime.now());
        manager.createSubTask(subtask);

        // пытаемся получить существующую подзадачу
        HttpResponse<String> responseOk = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, responseOk.statusCode());

        SubTask responseSubTask = gson.fromJson(responseOk.body(), SubTask.class);

        assertEquals(subtask.getName(), responseSubTask.getName(), "Некорректное имя подзадачи");
    }
} 