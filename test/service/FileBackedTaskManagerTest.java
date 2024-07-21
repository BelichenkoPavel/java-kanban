package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new FileBackedTaskManager();
    }

    @Test
    public void testSaveAndLoadEmpty() {
        taskManager.save();
        taskManager = FileBackedTaskManager.loadFromFile();

        assertEquals(0, taskManager.getAllTasks().size(), "Список задач должен быть пустым");
        assertEquals(0, taskManager.getAllSubTasks().size(), "Список подзадач должен быть пустым");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков должен быть пустым");
    }

    @Test
    public void shouldSaveAndLoad() {
        taskManager.createTask(new Task("Название задачи 1", Status.NEW, "Описание задачи"));
        taskManager.createTask(new Task("Название задачи 2", Status.IN_PROGRESS, "Описание задачи"));

        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);

        taskManager.createSubTask(new SubTask("Подзадача 1", Status.NEW, "Описание подзадачи", epic.getId()));
        taskManager.createSubTask(new SubTask("Подзадача 2", Status.DONE, "Описание подзадачи", epic.getId()));

        taskManager.save();

        taskManager = FileBackedTaskManager.loadFromFile();

        assertEquals(2, taskManager.getAllTasks().size(), "Количество задач должно быть 2");
        assertEquals(2, taskManager.getAllSubTasks().size(), "Количество подзадач должно быть 2");
        assertEquals(1, taskManager.getAllEpics().size(), "Количество эпиков должно быть 1");

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubTasks();

        taskManager = FileBackedTaskManager.loadFromFile();

        assertEquals(0, taskManager.getAllTasks().size(), "Список задач должен быть пустым");
        assertEquals(0, taskManager.getAllSubTasks().size(), "Список подзадач должен быть пустым");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков должен быть пустым");
    }
}
