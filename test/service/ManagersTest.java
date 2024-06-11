package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    Managers manager;

    @BeforeEach
    void setUp() {
        manager = new Managers();
    }

    @Test
    public void shouldTaskManagerExist() {
        TaskManager taskManager = manager.getDefault();

        assertNotNull(taskManager, "taskManager должен быть существовать");
    }
    @Test
    public void shouldHistoryManagerExist() {
        HistoryManager historyManager = manager.getDefaultHistory();

        assertNotNull(historyManager);
        assertNotNull(historyManager, "historyManager должен быть существовать");
    }
}