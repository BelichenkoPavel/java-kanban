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

        Task task = new Task("Название задачи", Status.NEW, "Описание задачи");
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 1");
    }

    @Test
    public void tryAddNullToHistory() {
        historyManager.add(null);

        assertEquals(0, historyManager.getHistory().size(), "Список истории должен быть пустым");
    }

    @Test
    public void max10ElementsInHistory() {
        for (int i = 0; i < 10; i++) {
            Task task = new Task("Задача " + i, Status.NEW, "Описание задачи");
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size(), "Количество элементов в списке истории должен быть равным 10");

        Task task = new Task("Задача 11", Status.NEW, "Описание задачи");
        historyManager.add(task);

        assertEquals("Задача 1", historyManager.getHistory().get(0).getName(), "Первая задача должна быть удалена");
        assertEquals(10, historyManager.getHistory().size(), "Количество элементов в списке истории должен быть равным 10");
    }
}