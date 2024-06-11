package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<SubTask> getAllSubTasks();

    ArrayList<SubTask> getSubTasksOfEpic(Epic epic);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void createTask(Task task);

    Epic createEpic(Epic epic);

    void createSubTask(SubTask subtask);

    void updateTask(int id, Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subtask, int id);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    ArrayList<Task> getHistory();
}
