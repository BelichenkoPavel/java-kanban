package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Task;
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

class HistoryListTypeToken extends TypeToken<List<Task>> {
}

public class HttpTaskManagerHistoryTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();
    String baseUrl = "http://localhost:8080/history";

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void testGetHistoryList() throws IOException, InterruptedException {
        Task task1 = new Task("Test", Status.NEW, "Testing", Duration.ofHours(5), LocalDateTime.now());
        Task task2 = new Task("Test", Status.NEW, "Description", Duration.ofHours(5), LocalDateTime.now().plusHours(5));
        manager.createTask(task1);
        manager.createTask(task2);

        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем запрос на получение пустой истории
        HttpResponse<String> responseEmptyList = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, responseEmptyList.statusCode());

        ArrayList<Task> tasksEmptyList = gson.fromJson(responseEmptyList.body(), new HistoryListTypeToken().getType());

        assertEquals(0, tasksEmptyList.size(), "Некорректное количество задач");

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());

        // вызываем запрос на получение истории
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        ArrayList<Task> tasks = gson.fromJson(response.body(), new HistoryListTypeToken().getType());

        assertEquals(2, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testPost404() throws IOException, InterruptedException {
        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();

        // вызываем несуществующий эндпоинт
        HttpResponse<String> responseEmptyList = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, responseEmptyList.statusCode());
    }
}