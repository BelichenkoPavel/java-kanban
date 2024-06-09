package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void shouldTaskManagerExist() {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefault();

        assertNotNull(taskManager, "taskManager должен быть существовать");
    }
    @Test
    public void shouldHistoryManagerExist() {
        Managers manager = new Managers();
        HistoryManager historyManager = manager.getDefaultHistory();

        assertNotNull(historyManager);
        assertNotNull(historyManager, "historyManager должен быть существовать");
    }
}