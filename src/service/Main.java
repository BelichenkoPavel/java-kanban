package service;

import model.Epic;
import model.SubTask;
import model.Task;

import static model.Status.*;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask("Посетить театр", NEW, "Балет Анна Каренина");
        taskManager.createTask("Посетить баню", NEW, "Мыло купить не забудем");
        taskManager.createTask("Посетить ДНС", NEW, "Купим новый робот пылесос");

        taskManager.createEpic("Поездка в Англию", "в Лондон");
        taskManager.createEpic("Строим шалаш", "Строим шалаш из камыша, и жаренных гвоздей");

        taskManager.createSubTask(4, "Купить билеты", DONE, "На Январь 2025");
        taskManager.createSubTask(4, "Купить носочки с бигбеном ", NEW, "Первостепенная задача");
        taskManager.createSubTask(4, "Узнать где живет Ричард Хаммонд", NEW, "Второстепенная задача");

        taskManager.createSubTask(5, "Гвозди", NEW, "Жарим на масле");
        taskManager.createSubTask(5, "Камыш", NEW, "Ищем болото");

        System.out.println("Все таски:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Все эпики");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Все сабтаски");
        System.out.println(taskManager.getAllSubTasks());
        System.out.println("Все сабстаски эпика 5");
        System.out.println(taskManager.getSubTasksOfEpic(5));
        System.out.println("___________________________");
        System.out.println("Проверка удаления тасок 2 и 3");
        taskManager.deleteTask(2);
        taskManager.deleteTask(3);
        System.out.println(taskManager.getAllTasks());
        System.out.println("___________________________");
        System.out.println("Проверка удаления эпика 4");
        taskManager.deleteEpic(4);
        System.out.println("все эпики");
        System.out.println(taskManager.getAllEpics());
        System.out.println("___________________________");
        System.out.println("удалление сабтасок эпиков");
        System.out.println(taskManager.getAllSubTasks());
        System.out.println("___________________________");
        System.out.println("Проверка обновления таски");
        taskManager.updateTask(1,"Театр скучно", "Сходили в лазер таг, поиграли", DONE);
        System.out.println(taskManager.getAllTasks());
        System.out.println("___________________________");
        System.out.println("Проверка обновления эпика");
        taskManager.updateEpic(5,"Шалаш строим 2-х этажный", "Докупим клей для шалаша");
        System.out.println(taskManager.getAllEpics());
        System.out.println("___________________________");
        System.out.println("Проверка обновления сабтаски 9 у эпика 5 ");
        taskManager.updateSubTask(9,"Гвозди пожарены", DONE,"Есть неудобно");
        System.out.println("удалление сабтасок по id");
        taskManager.deleteSubTask(10);
        System.out.println("___________________________");
        System.out.println("Проверка обновления статуса у эпика 5 ");
        System.out.println(taskManager.getSubTasksOfEpic(5));
        System.out.println(taskManager.getAllEpics());
    }
}
