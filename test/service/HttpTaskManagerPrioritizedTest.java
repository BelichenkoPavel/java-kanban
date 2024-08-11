package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Task;
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

class PrioritizedListTypeToken extends TypeToken<List<Task>> {
}

public class HttpTaskManagerPrioritizedTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();
    String baseUrl = "http://localhost:8080/prioritized";

    public HttpTaskManagerPrioritizedTest() throws IOException {
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
    @DisplayName("Проверка получения списка задач в порядке приоритета")
    public void testGetPrioritizedList() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", Status.NEW, "Description", Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task("Test 2", Status.NEW, "Description", Duration.ofHours(1), LocalDateTime.now().minusHours(2));
        Task task3 = new Task("Test 3", Status.NEW, "Description", Duration.ofHours(1), LocalDateTime.now().minusHours(4));

        URI url = URI.create(baseUrl);
        HttpRequest requestEmptyList = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем запрос на получение пустого списка задач в порядке приоритета
        HttpResponse<String> responseEmptyList = client.send(requestEmptyList, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, responseEmptyList.statusCode());

        ArrayList<Task> tasksEmptyList = gson.fromJson(responseEmptyList.body(), new PrioritizedListTypeToken().getType());

        assertEquals(0, tasksEmptyList.size(), "Некорректное количество задач");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем запрос на получение списка задач в порядке приоритета
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        ArrayList<Task> tasks = gson.fromJson(response.body(), new PrioritizedListTypeToken().getType());

        assertEquals(3, tasks.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Попытка получения списка задач по несуществующему эндпоинту")
    public void testPost404() throws IOException, InterruptedException {
        URI url = URI.create(baseUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();

        // вызываем несуществующий эндпоинт
        HttpResponse<String> responseEmptyList = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, responseEmptyList.statusCode());
    }
}