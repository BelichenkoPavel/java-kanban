package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private int ID;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
    }

    private int generateId() {
        return ++ID;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        HashMap<Integer, SubTask> subTasksList = new HashMap<>();
        for (Epic epic : epics.values()) {
            for (SubTask subTask : epic.getSubTasks().values()) {
                subTasksList.put(subTask.getId(), subTask);
            }
        }
        return subTasksList;
    }

    public HashMap<Integer, SubTask> getSubTasksOfEpic(int epicId) {
        HashMap<Integer, SubTask> subTasksList = new HashMap<>();
        for (SubTask subTask : epics.get(epicId).getSubTasks().values()) {
            subTasksList.put(subTask.getId(), subTask);
        }
        return subTasksList;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }


    public void deleteAllEpics() {
        epics.clear();
    }


    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int epicId, int subTuskId) {
        return epics.get(epicId).getSubTasks().get(subTuskId);
    }

    public void createTask(String name, Status status, String description) {
        Task task = new Task();
        int id = generateId();
        task.setId(id);
        task.setName(name);
        task.setStatus(status);
        task.setDescription(description);
        tasks.put(id, task);
    }

    public void createEpic(String name, String description) {
        int id = generateId();
        Epic epic = new Epic();
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epic.setName(name);
        epic.setDescription(description);
        epics.put(id, epic);
    }

    public void createSubTask(int epicId, String name, Status status, String description) {
        int id = generateId();
        SubTask subTask = new SubTask();
        subTask.setId(id);
        subTask.setName(name);
        subTask.setStatus(status);
        subTask.setDescription(description);
        epics.get(epicId).addSubTask(subTask);
        calculateStatus(epics.get(epicId));
    }

    public void updateTask(int id, String name, String description, Status status) {
        tasks.get(id).setName(name);
        tasks.get(id).setDescription(description);
        tasks.get(id).setStatus(status);
    }

    public void updateEpic(int id, String name, String description) {
        epics.get(id).setName(name);
        epics.get(id).setDescription(description);
    }

    public void updateSubTask(int SubTuskId, String name, Status status, String description) {
        getAllSubTasks().get(SubTuskId).setName(name);
        getAllSubTasks().get(SubTuskId).setStatus(status);
        getAllSubTasks().get(SubTuskId).setDescription(description);
        for (Epic epic : epics.values()) {
            if (epic.getSubTasks().containsKey(SubTuskId)) {
                calculateStatus(epic);
            }
        }
    }

    //f
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        epics.remove(id);
    }

    public void deleteSubTask(int id) {
        for (Epic epic : epics.values()) {
            if (epic.getSubTasks().containsKey(id)) {
                epic.getSubTasks().remove(id);
                calculateStatus(epic);
            }
        }
    }

    private void calculateStatus(Epic epic) {
        if (epic.getSubTasks().size() == 0) {
            epic.setStatus(Status.NEW);
        } else {
            int count = 0;
            for (SubTask subTask : epic.getSubTasks().values()) {
                if (subTask.getStatus() == Status.DONE)
                    count++;
            }
            if (count == epic.getSubTasks().size())
                epic.setStatus(Status.DONE);
            else
                epic.setStatus(Status.IN_PROGRESS);
        }
    }
}