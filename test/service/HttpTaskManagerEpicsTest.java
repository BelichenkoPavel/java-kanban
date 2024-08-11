package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

class EpicListTypeToken extends TypeToken<List<Epic>> {
}

public class HttpTaskManagerEpicsTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = Managers.getGson();

    String baseUrl = "http://localhost:8080/epics";
    HttpClient client = HttpClient.newHttpClient();

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
    @DisplayName("Добавить Эпик")
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём json запрос эпика
        String epicJson = "{\"name\":\"Test\",\"description\":\"Testing task\"}";

        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем запрос на создание нового эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась сущность
        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Удалить Эпик")
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        manager.createEpic(epic);

        URI url = URI.create(baseUrl + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем запрос на удаление эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась сущность
        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(0, epicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    @DisplayName("Получить список Эпиков")
    public void testGetEpicsList() throws IOException, InterruptedException {
        Epic task1 = new Epic("Test 1", "Description 1");
        Epic task2 = new Epic("Test 2", "Description 2");
        manager.createEpic(task1);
        manager.createEpic(task2);

        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем запрос на получение списка эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        ArrayList<Epic> epics = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertEquals(2, epics.size(), "Некорректное количество эпиков");
    }

    @Test
    @DisplayName("Получить Эпик по id")
    public void testGetEpicById() throws IOException, InterruptedException {
        URI url = URI.create(baseUrl + "/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем запрос на получение эпика по несуществующему id
        HttpResponse<String> responseNotFound = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, responseNotFound.statusCode());

        Epic epic = new Epic("Test", "Testing");
        manager.createEpic(epic);

        // вызываем запрос на получение эпика по существующему id
        HttpResponse<String> responseOk = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, responseOk.statusCode());

        Epic responseEpics = gson.fromJson(responseOk.body(), Epic.class);

        assertEquals(epic.getName(), responseEpics.getName(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Получить список подзадач данного эпика")
    public void testGetSubTasksOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Description");
        manager.createEpic(epic);
        SubTask task1 = new SubTask("Test", Status.NEW, "Testing", epic.getId(), Duration.ofHours(5), LocalDateTime.now());
        SubTask task2 = new SubTask("Test", Status.NEW, "Description", epic.getId(), Duration.ofHours(5), LocalDateTime.now().plusHours(5));
        manager.createSubTask(task1);
        manager.createSubTask(task2);


        URI url = URI.create(baseUrl + "/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем запрос на получение списка подзадач данного эпика
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        ArrayList<SubTask> subTasks = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertEquals(2, subTasks.size(), "Некорректное количество подзадач");
    }
} 