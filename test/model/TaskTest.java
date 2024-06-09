package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void taskEquals() {
        Task task1 = new Task(1, "Создать работающий проект", Status.DONE,"5 спринт");
        Task task2 = new Task(1, "Создать работающий проект", Status.DONE, "5 спринт");

        assertEquals(task1, task2);
    }
}