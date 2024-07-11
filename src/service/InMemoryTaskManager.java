package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

import static model.Status.*;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;
    private int numb = 0;
    private HistoryManager history = new Managers().getDefaultHistory();

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    private int generateId() {
        return ++numb;
    }


    //        Здесь и дальше аналогичные методы. Можно проще:
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }


    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<SubTask> getSubTasksOfEpic(Epic epic) {
        ArrayList<SubTask> subtasksList = new ArrayList<>();
        for (Integer subtask : epic.getSubTasks()) {
            subtasksList.add(subtasks.get(subtask));
        }
        return subtasksList;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }


    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }


    public void deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
//            Ещё не забыть пересчитать статусы у всех Epic.
            calculateStatus(epic);
        }
        subtasks.clear();
    }


    public Task getTask(int id) {
        Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        history.add(epic);
        return epic;
    }

    public SubTask getSubTask(int id) {
        SubTask subTask = subtasks.get(id);
        history.add(subTask);
        return subTask;
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);

        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epic.setStatus(NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }


    public void createSubTask(SubTask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpic());
        epic.addSubTask(subtask.getId());
        calculateStatus(epic);
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            task.setId(id);
            tasks.put(id, task);
        }
    }

    public void updateEpic(Epic epic) {
        Epic saved = getEpic(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        epics.put(saved.getId(), saved);
    }

    public void updateSubTask(SubTask subtask, int id) {
        for (Epic epic : epics.values()) {
            if (epic.getSubTasks().contains(id)) {
                subtask.setId(id);
                subtasks.put(id,subtask);
                epic.updateSubTask(subtask);
                calculateStatus(epic);
            }
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubTasks()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubTask(int id) {
        SubTask subtask = getSubTask(id);
        Epic epic = epics.get(subtask.getEpic());
        epic.removeSubTask(id);
        subtasks.remove(id);
        calculateStatus(epic);
    }

    private void calculateStatus(Epic epic) {
        if (epic.getSubTasks().isEmpty()) {
            epic.setStatus(NEW);
        }
        int count = 0;
        for (Integer subtaskId : epic.getSubTasks()) {
            if (subtasks.get(subtaskId).getStatus() == IN_PROGRESS) {
                epic.setStatus(IN_PROGRESS);
                break;
            }
            if (subtasks.get(subtaskId).getStatus() == DONE) {
                count++;
            }
        }
        if (count == epic.getSubTasks().size()) {
            epic.setStatus(DONE);
        }
    }

    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }
}