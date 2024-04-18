package service;

import model.Epic;
import model.SubTask;
import model.Task;

import static model.Status.NEW;
// import static model.Status.IN_PROGRESS;
import static model.Status.DONE;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.createTask(new Task("Заказать еду из СберМаркета", NEW, "1)Хлеб, 2)Соль."));
        taskManager.createTask(new Task("Посетить театр", NEW, "Балет «Анна Каренина»"));

        Epic Travel = taskManager.createEpic(new Epic("Поездка в Англию", "в Лондон"));
        taskManager.createSubTask(new SubTask("Купить билеты", NEW, "На Январь 2025", Travel.getId()));
        taskManager.createSubTask(new SubTask("Собрать чемодан", DONE, "задача выполнена 5 месяцев назад", Travel.getId()));

        Epic Project = taskManager.createEpic(new Epic("Создать работающий проект", "5 спринт"));
        taskManager.createSubTask(new SubTask("Сдать проект на первую проверку", NEW, "ожидать правки", Project.getId()));

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();

        taskManager.updateEpic(new Epic(3, "Поездка в Беларусь>", NEW, "в Минск"));
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getSubTasksOfEpic(Project));
        System.out.println();

        taskManager.deleteTask(1);
        taskManager.deleteSubTask(4);
        taskManager.deleteSubTask(5);
        taskManager.deleteEpic(3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();
    }
}
