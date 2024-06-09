package service;

import model.Epic;
import model.SubTask;
import model.Task;

import static model.Status.*;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = new Managers().getDefault();
        inMemoryTaskManager.createTask(new Task("Заказать еду из СберМаркета", NEW, "1)Хлеб, 2)Соль."));
        inMemoryTaskManager.createTask(new Task("Посетить театр", NEW, "Балет «Анна Каренина»"));

        Epic Travel = inMemoryTaskManager.createEpic(new Epic("Поездка в Англию", "в Лондон"));
        inMemoryTaskManager.createSubTask(new SubTask("Купить билеты", DONE, "На Январь 2025", Travel.getId()));
        inMemoryTaskManager.createSubTask(new SubTask("Собрать чемодан", DONE, "задача выполнена 5 месяцев назад", Travel.getId()));

        Epic Project = inMemoryTaskManager.createEpic(new Epic("Создать работающий проект", "5 спринт"));
        inMemoryTaskManager.createSubTask(new SubTask("Сдать проект на первую проверку", NEW, "ожидать правки", Project.getId()));

        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks());
        System.out.println();

        inMemoryTaskManager.updateEpic(new Epic(3, "Поездка в Беларусь>",  "в Минск"));
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks());
        System.out.println(inMemoryTaskManager.getSubTasksOfEpic(Project));
        System.out.println();

        inMemoryTaskManager.deleteTask(1);
        inMemoryTaskManager.deleteSubTask(4);
        inMemoryTaskManager.deleteSubTask(5);
        inMemoryTaskManager.deleteEpic(3);

        Epic Travel2 = inMemoryTaskManager.createEpic(new Epic("Подольск", "в ПОДОЛЬСК"));
        inMemoryTaskManager.createSubTask(new SubTask("Купить деньги", IN_PROGRESS, "На Январь 2025", Travel2.getId()));
        inMemoryTaskManager.createSubTask(new SubTask("Собрать сборы", DONE, "задача выполнена 5 месяцев назад", Travel2.getId()));
        System.out.println("Tasks");
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println("epics");
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println("subtasks");
        System.out.println(inMemoryTaskManager.getAllSubTasks());
        System.out.println("______________");
        inMemoryTaskManager.updateSubTask(new SubTask("341234 1234", DONE, "1234 2134 5 месяцев назад", Travel2.getId()), 9);
        System.out.println(inMemoryTaskManager.getSubTasksOfEpic(Travel2));

        System.out.println("______________");
        inMemoryTaskManager.updateTask(2, new Task("Посетить театр", DONE, "Балет «Анна Каренина»"));
        System.out.println(inMemoryTaskManager.getTask(2));
    }
}