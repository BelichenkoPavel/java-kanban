package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    public void taskEquals() {
        SubTask task1 = new SubTask("Создать работающий проект", Status.DONE, "5 спринт", 1);
        task1.setId(1);
        SubTask task2 = new SubTask("Создать работающий проект", Status.DONE,  "5 спринт", 1);
        task2.setId(1);

        assertEquals(task1, task2);
    }
}