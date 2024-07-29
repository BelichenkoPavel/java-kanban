package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Название задачи", Status.NEW, "Описание задачи");
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач должен быть пустым");
        taskManager.createTask(task);
        assertEquals(1, taskManager.getAllTasks().size(), "Количество задач должно быть равным 1");

        assertEquals(task, taskManager.getTask(task.getId()), "Задачи не совпадают");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Список задач не должен быть пустым");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void getHistory() {
        Task task1 = new Task(0, "Название задачи 1", Status.NEW, "Описание задачи");
        Task task2 = new Task(1, "Название задачи 2", Status.IN_PROGRESS, "Описание задачи");
        Task task3 = new Task(2, "Название задачи 3", Status.DONE, "Описание задачи");

        // Создаем задачи
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        // Просмартиваем задачи
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        assertArrayEquals(new Task[]{task1, task2, task3}, taskManager.getHistory().toArray(), "Задачи не совпадают");

        // Повторно просмариваем задачу
        taskManager.getTask(task2.getId());
        assertArrayEquals(new Task[]{task1, task3, task2}, taskManager.getHistory().toArray(), "Задачи не совпадают");

    }

    @Test
    void deleteTask() {
        Task deletedTask = new Task("Название задачи 2", Status.NEW, "Описание задачи");
        taskManager.createTask(new Task("Название задачи 1", Status.NEW, "Описание задачи"));
        taskManager.createTask(deletedTask);

        assertEquals(2, taskManager.getAllTasks().size(), "Количество задач должно быть равным 2");

        taskManager.deleteTask(deletedTask.getId());
        assertEquals(1, taskManager.getAllTasks().size(), "Количество задач должно быть равным 1");
    }

    @Test
    void deleteAllTasks() {
        taskManager.createTask(new Task("Название задачи 1", Status.NEW, "Описание задачи"));
        taskManager.createTask(new Task("Название задачи 2", Status.NEW, "Описание задачи"));

        assertEquals(2, taskManager.getAllTasks().size(), "Количество задач должно быть равным 2");

        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач после удаления должен быть пустым");
    }

    @Test
    void updateTask() {
        Task task = new Task(1, "Название задачи", Status.NEW, "Описание задачи");
        taskManager.createTask(task);


        Task updatedTask = new Task(1, "Измененная задача", Status.IN_PROGRESS, "Доработанное описание задачи");
        taskManager.updateTask(1, updatedTask);

        assertEquals(updatedTask, taskManager.getTask(1), "Задача должна быть изменена");
    }

    @Test
    void addNewSubTask() {
        Epic epic = taskManager.createEpic(new Epic(1, "Создать работающий проект", "5 спринт"));

        SubTask subTask = new SubTask("Создать работающий проект", Status.IN_PROGRESS, "5 спринт", 1);
        assertEquals(0, taskManager.getAllSubTasks().size(), "Список подзадач должен быть пустым");
        taskManager.createSubTask(subTask);
        assertEquals(1, taskManager.getAllSubTasks().size(), "Количество подзадач должно быть равным 1");

        assertEquals(subTask, taskManager.getSubTask(subTask.getId()));

        final List<SubTask> subTasks = taskManager.getSubTasksOfEpic(epic);

        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void deleteSubTask() {
        taskManager.createEpic(new Epic(1, "Создать работающий проект", "5 спринт"));

        SubTask subTask1 = new SubTask("Создать работающий проект", Status.DONE, "5 спринт", 1);
        SubTask subTask2 = new SubTask("Закончить проект", Status.IN_PROGRESS, "Успеть все сдать", 1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(2, taskManager.getAllSubTasks().size(), "Количество подзадач должно быть равным 2");

        taskManager.deleteSubTask(subTask1.getId());
        assertEquals(1, taskManager.getAllSubTasks().size(), "Количество подзадач должно быть равным 1");
    }

    @Test
    void deleteAllSubTasks() {
        taskManager.createEpic(new Epic(1, "Создать работающий проект", "5 спринт"));

        SubTask subTask1 = new SubTask("Создать работающий проект", Status.DONE, "5 спринт", 1);
        SubTask subTask2 = new SubTask("Закончить проект", Status.NEW, "Успеть все сдать", 1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(2, taskManager.getAllSubTasks().size(), "Количество подзадач должно быть равным 2");

        taskManager.deleteAllSubTasks();
        assertEquals(0, taskManager.getAllSubTasks().size(), "Список подзадач после удаления должен быть пустым");
    }

    @Test
    void updateSubTask() {
        Epic epic = new Epic("Создать работающий проект", "5 спринт");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Название подзадачи", Status.NEW, "Описание задачи", epic.getId());
        taskManager.createSubTask(subTask);
        epic.addSubTask(subTask);


        SubTask updatedSubTask = new SubTask("Измененная подзадача", Status.IN_PROGRESS, "Доработанное описание задачи", 1);
        taskManager.updateSubTask(updatedSubTask, subTask.getId());

        assertEquals(updatedSubTask, taskManager.getSubTask(subTask.getId()), "Задача должна быть изменена");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Создать работающий проект", "5 спринт");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков должен быть пустым");
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getAllEpics().size(), "Количество эпиков должно быть равным 1");

        assertEquals(epic, taskManager.getEpic(epic.getId()), "Эпики не совпадают");

        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков после удаления должен быть пустым");
    }

    @Test
    void deleteEpic() {
        Epic epic1 = new Epic("Создать работающий проект", "5 спринт");
        Epic epic2 = new Epic("Начать подготовку к 6 спринту", "6 спринт");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        assertEquals(2, taskManager.getAllEpics().size(), "Количество эпиков должно быть равным 2");

        taskManager.deleteEpic(epic1.getId());
        assertEquals(1, taskManager.getAllEpics().size(), "Количество эпиков должно быть равным 1");
    }

    @Test
    void deleteAllEpics() {
        Epic epic = new Epic("Создать работающий проект", "5 спринт");
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getAllEpics().size(), "Количество эпиков должно быть равным 1");

        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков после удаления должен быть пустым");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Создать работающий проект", "5 спринт");
        taskManager.createEpic(epic);

        Epic updatedEpic = new Epic(epic.getId(), "Подготовить проект", "6 спринт");

        taskManager.updateEpic(updatedEpic);

        assertEquals(updatedEpic, taskManager.getEpic(epic.getId()));
    }

    @Test
    void getPrioritizedTasks() {
        LocalDateTime startDate = LocalDateTime.parse("2024-01-03T10:00:30");
        Task task1 = new Task("Задача 1", Status.NEW, "Описание задачи 1", Duration.ofMinutes(30), startDate.plusHours(1));
        Task task2 = new Task("Задача 2", Status.IN_PROGRESS, "Описание задачи 2", Duration.ofMinutes(10), startDate);
        Task task3 = new Task("Задача 3", Status.DONE, "Описание задачи 3", Duration.ofMinutes(60), startDate.minusHours(1));
        Task task4 = new Task("Задача без времени", Status.NEW, "Описание задачи 4");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.size(), "Неверное количество приоритетных задач");

        assertArrayEquals(prioritizedTasks.toArray(), new Task[]{task3, task2, task1}, "Неправильный порядок задач");

        taskManager.deleteTask(task1.getId());
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Неверное количество приоритетных задач");
    }

    @Test
    void checkEpicDuration() {
        Epic epic = new Epic("Создать работающий проект", "8 спринт");
        taskManager.createEpic(epic);

        LocalDateTime startDate = LocalDateTime.parse("2024-01-03T10:00:02");

        SubTask subTask2 = new SubTask("Задача 1", Status.IN_PROGRESS, "8 спринт", epic.getId(), Duration.ofMinutes(10), startDate.plusMinutes(30));
        SubTask subTask1 = new SubTask("Задача 2", Status.DONE, "8 спринт", epic.getId(), Duration.ofMinutes(30), startDate);
        SubTask subTask3 = new SubTask("Задача 3", Status.NEW, "8 спринт", epic.getId(), Duration.ofMinutes(10), startDate.plusMinutes(70));

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        assertEquals(50, epic.getDuration().toMinutes(), "Длительность эпика не равна сумме подзадач");
        assertEquals(startDate, epic.getStartTime(), "Дата начала эпика неправильная");
        assertEquals(startDate.plusMinutes(80), epic.getEndTime(), "Дата окончания эпика неправильная");

        taskManager.deleteSubTask(subTask1.getId());
        assertEquals(20, epic.getDuration().toMinutes(), "Длительность эпика не равна сумме подзадач");
        assertEquals(startDate.plusMinutes(30), epic.getStartTime(), "Дата начала эпика неправильная");
        assertEquals(startDate.plusMinutes(80), epic.getEndTime(), "Дата окончания эпика неправильная");

        taskManager.deleteAllSubTasks();
        assertEquals(0, epic.getDuration().toMinutes(), "Длительность эпика не равна сумме подзадач");
        assertNull(epic.getStartTime(), "Дата начала эпика неправильная");
        assertNull(epic.getEndTime(), "Дата окончания эпика неправильная");
    }

    @Test
    void checkTaskCollision() {
        LocalDateTime startDate = LocalDateTime.parse("2024-01-03T10:00:00");

        boolean collision = taskManager.isTaskCollideByDate(
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate),
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate)
        );

        assertTrue(collision, "Задачи не могут пересекаться по дате");

        collision = taskManager.isTaskCollideByDate(
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate),
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate.plusMinutes(5))
        );

        assertTrue(collision, "Задачи не могут пересекаться по дате");

        collision = taskManager.isTaskCollideByDate(
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate.plusMinutes(5)),
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate)
        );

        assertTrue(collision, "Задачи не могут пересекаться по дате");

        collision = taskManager.isTaskCollideByDate(
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate),
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate.plusMinutes(10))
        );

        assertFalse(collision, "Задачи не пересекаются по дате");

        collision = taskManager.isTaskCollideByDate(
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate.plusMinutes(10)),
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate)
        );

        assertFalse(collision, "Задачи не пересекаются по дате");

        collision = taskManager.isTaskCollideByDate(
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate),
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate.plusMinutes(20))
        );

        assertFalse(collision, "Задачи не пересекаются по дате");

        collision = taskManager.isTaskCollideByDate(
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate.plusMinutes(20)),
                new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate)
        );

        assertFalse(collision, "Задачи не пересекаются по дате");
    }

    @Test
    void checkTaskCollisionError() {
        Epic epic = new Epic("Создать работающий проект", "8 спринт");
        taskManager.createEpic(epic);

        LocalDateTime startDate = LocalDateTime.now();
        Task task1 = new Task("Задача 1", Status.IN_PROGRESS, "Описание задачи 1", Duration.ofMinutes(10), startDate);
        Task task2 = new Task("Задача 2", Status.IN_PROGRESS, "Описание задачи 2", Duration.ofMinutes(10), startDate.minusMinutes(5));
        SubTask subTask1 = new SubTask("Задача 2", Status.DONE, "8 спринт", epic.getId(), Duration.ofMinutes(30), startDate.minusHours(1));
        SubTask subTask2 = new SubTask("Задача 2", Status.DONE, "8 спринт", epic.getId(), Duration.ofMinutes(30), startDate.minusHours(1));

        taskManager.createTask(task1);
        taskManager.createSubTask(subTask1);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask(task2);
        }, "Задача не может пересекаться по дате");

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubTask(subTask2);
        }, "Задача не может пересекаться по дате");
    }
}