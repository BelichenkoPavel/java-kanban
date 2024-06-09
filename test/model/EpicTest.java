package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void epicEquals() {
        Epic epic1 = new Epic(1, "Покрыть проект тестами", "5 спринт");
        Epic epic2 = new Epic(1, "Покрыть проект тестами", "5 спринт");

        assertEquals(epic1, epic2);
    }
}