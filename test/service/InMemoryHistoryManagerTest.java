package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }
    @Test
    public void addToHistory() {
        assertEquals(0, historyManager.getHistory().size(), "Список истории должен быть пустым");

        Task task1 = new Task(0, "Название задачи 1", Status.NEW, "Описание задачи");
        Task task2 = new Task(1, "Название задачи 2", Status.IN_PROGRESS, "Описание задачи");
        Task task3 = new Task(2, "Название задачи 3", Status.DONE, "Описание задачи");

        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 1");

        historyManager.add(task2);
        assertEquals(2, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 2");

        historyManager.add(task3);
        assertEquals(3, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 3");

        Task[] expectedHistory = new Task[]{ task1, task2, task3};
        assertArrayEquals(historyManager.getHistory().toArray(), expectedHistory, "Список исторри должен быть в правильном порядке");
    }

    @Test
    public void tryAddDuplicateTaskToHistory() {
        Task task1 = new Task(0, "Название задачи 1", Status.NEW, "Описание задачи");
        Task task2 = new Task(1, "Название задачи 2", Status.IN_PROGRESS, "Описание задачи");
        Task task3 = new Task(2, "Название задачи 3", Status.DONE, "Описание задачи");

        // Создаем список истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Добавляем дубликат задачи
        historyManager.add(task1);

        // Проверяем, что список истории выводится в правильном порядке
        Task[] expectedHistory = new Task[]{ task2, task3, task1 };
        assertArrayEquals(historyManager.getHistory().toArray(), expectedHistory, "Список исторри должен быть в правильном порядке");
    }

    @Test
    public void tryDeleteTaskFromHistory() {
        Task task1 = new Task(0, "Название задачи 1", Status.NEW, "Описание задачи");
        Task task2 = new Task(1, "Название задачи 2", Status.IN_PROGRESS, "Описание задачи");
        Task task3 = new Task(2, "Название задачи 3", Status.DONE, "Описание задачи");
        Task task4 = new Task(3, "Название задачи 4", Status.IN_PROGRESS, "Описание задачи");

        // Создаем список истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        // Удаляем задачу из середины списка
        historyManager.remove(task2.getId());
        assertEquals(3, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 3");

        // Проверяем, что список истории выводится в правильном порядке
        Task[] expectedHistory = new Task[]{ task1, task3, task4};
        assertArrayEquals(historyManager.getHistory().toArray(), expectedHistory, "Список исторри должен быть в правильном порядке");

        // Удаляем последнюю задачу из списка
        historyManager.remove(task4.getId());
        assertEquals(2, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 2");

        // Проверяем, что список истории выводится в правильном порядке
        expectedHistory = new Task[]{ task1, task3};
        assertArrayEquals(historyManager.getHistory().toArray(), expectedHistory, "Список исторри должен быть в правильном порядке");

        // Удаляем первую задачу из списка
        historyManager.remove(task1.getId());
        assertEquals(1, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 1");

        // Проверяем, что список истории выводится в правильном порядке
        expectedHistory = new Task[]{ task3 };
        assertArrayEquals(historyManager.getHistory().toArray(), expectedHistory, "Список исторри должен быть в правильном порядке");

        // Удаляем оставшуюся задачу из списка
        historyManager.remove(task3.getId());
        assertEquals(0, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 0");
    }

    @Test
    public void tryAddNullToHistory() {
        historyManager.add(null);

        assertEquals(0, historyManager.getHistory().size(), "Список истории должен быть пустым");
    }
}