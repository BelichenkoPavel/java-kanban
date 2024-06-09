package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    public void addToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        assertEquals(0, historyManager.getHistory().size(), "Список истории должен быть пустым");

        Task task = new Task("Название задачи", Status.NEW, "Описание задачи");
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Количество элементов в списке истории должно быть равным 1");
    }

    @Test
    public void max10ElementsInHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

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