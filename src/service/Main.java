package service;

import model.Epic;
import model.SubTask;
import model.Task;

import static model.Status.*;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = new Managers().getDefault();
        Task task1 = inMemoryTaskManager.createTask(new Task("Заказать еду из СберМаркета", NEW, "1)Хлеб, 2)Соль."));
        Task task2 = inMemoryTaskManager.createTask(new Task("Посетить театр", NEW, "Балет «Анна Каренина»"));

        System.out.println("Получить список задач");
        System.out.println(inMemoryTaskManager.getTask(task1.getId()));
        System.out.println(inMemoryTaskManager.getTask(task2.getId()));
        System.out.println("Получить историю задач");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println("______________");

        Epic travel = inMemoryTaskManager.createEpic(new Epic("Поездка в Англию", "в Лондон"));
        inMemoryTaskManager.createSubTask(new SubTask("Купить билеты", DONE, "На Январь 2025", travel.getId()));
        inMemoryTaskManager.createSubTask(new SubTask("Собрать чемодан", DONE, "задача выполнена 5 месяцев назад", travel.getId()));

        Epic project = inMemoryTaskManager.createEpic(new Epic("Создать работающий проект", "5 спринт"));
        inMemoryTaskManager.createSubTask(new SubTask("Сдать проект на первую проверку", NEW, "ожидать правки", project.getId()));

        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks());
        System.out.println();

        inMemoryTaskManager.updateEpic(new Epic(3, "Поездка в Беларусь>",  "в Минск"));
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks());
        System.out.println(inMemoryTaskManager.getSubTasksOfEpic(project));
        System.out.println();

        inMemoryTaskManager.deleteTask(1);
        inMemoryTaskManager.deleteSubTask(4);
        inMemoryTaskManager.deleteSubTask(5);
        inMemoryTaskManager.deleteEpic(3);

        Epic travel2 = inMemoryTaskManager.createEpic(new Epic("Подольск", "в ПОДОЛЬСК"));
        inMemoryTaskManager.createSubTask(new SubTask("Купить деньги", IN_PROGRESS, "На Январь 2025", travel2.getId()));
        inMemoryTaskManager.createSubTask(new SubTask("Собрать сборы", DONE, "задача выполнена 5 месяцев назад", travel2.getId()));
        System.out.println("Tasks");
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println("epics");
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println("subtasks");
        System.out.println(inMemoryTaskManager.getAllSubTasks());
        System.out.println("______________");
        inMemoryTaskManager.updateSubTask(new SubTask("341234 1234", DONE, "1234 2134 5 месяцев назад", travel2.getId()), 9);
        System.out.println(inMemoryTaskManager.getSubTasksOfEpic(travel2));

        System.out.println("______________");
        inMemoryTaskManager.updateTask(2, new Task("Посетить театр", DONE, "Балет «Анна Каренина»"));
        System.out.println(inMemoryTaskManager.getTask(2));
    }
}