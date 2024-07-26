package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import static model.Status.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subtasks;
    private int numb = 0;
    private HistoryManager history = new Managers().getDefaultHistory();

    private TreeSet<Task> prioritizedTasks = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime().isEqual(o2.getStartTime())) {
                return 0;
            }

            return o1.getStartTime().isBefore(o2.getStartTime()) ? -1 : 1;
        }
    });

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
        epic.getSubTasks().stream().forEach(subtask -> {
            subtasksList.add(subtasks.get(subtask));
        });
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
            epic.getSubTasks().forEach(id -> {
                SubTask subtask = subtasks.get(id);
                if (subtask.getStartTime() != null) {
                    prioritizedTasks.remove(subtask);
                }
            });
            epic.getSubTasks().clear();
//            Ещё не забыть пересчитать статусы у всех Epic.
            calculateStatus(epic);
            calculateDate(epic);
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
        checkCollideByDate(task);

        task.setId(generateId());
        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epic.setStatus(NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }


    public SubTask createSubTask(SubTask subtask) {
        checkCollideByDate(subtask);

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpic());
        epic.addSubTask(subtask);
        calculateStatus(epic);
        calculateDate(epic);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        return subtask;
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            checkCollideByDate(task);

            task.setId(id);
            tasks.put(id, task);
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
                prioritizedTasks.add(task);
            }
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
                checkCollideByDate(subtask);

                subtask.setId(id);
                subtasks.put(id,subtask);
                epic.updateSubTask(subtask);
                calculateStatus(epic);

                if (subtask.getStartTime() != null) {
                    prioritizedTasks.remove(subtask);
                    prioritizedTasks.add(subtask);
                }
            }
        }
    }

    public void deleteTask(int id) {
        Task task = tasks.get(id);
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(tasks.get(id));
        }

        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubTasks()) {
            subtasks.remove(subtaskId);
            SubTask subtask = subtasks.get(subtaskId);

            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        }
        epics.remove(id);
    }

    public void deleteSubTask(int id) {
        SubTask subtask = getSubTask(id);
        Epic epic = epics.get(subtask.getEpic());
        subtasks.remove(id);
        epic.deleteSubTask(subtask);
        calculateStatus(epic);
        calculateDate(epic);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.remove(subtask);
        }
    }

    protected void calculateDate(Epic epic) {
        if (epic.getSubTasks().isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ofSeconds(0));
            return;
        }

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = Duration.ofSeconds(0);

        for (Integer subtaskId : epic.getSubTasks()) {
            SubTask subtask = subtasks.get(subtaskId);
            if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }

            duration = duration.plus(subtask.getDuration());
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
    }

    protected void calculateStatus(Epic epic) {
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

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public void checkCollideByDate(Task task) {
        tasks.forEach((id, existTask) -> {
            if (isTaskCollideByDate(existTask, task)) {
                throw new IllegalArgumentException("Нельзя добавлять задачу с пересечением по времени");
            }
        });

        subtasks.forEach((id, subTask) -> {
            if (isTaskCollideByDate(subTask, task)) {
                throw new IllegalArgumentException("Нельзя добавлять задачу с пересечением по времени");
            }
        });
    }

    public boolean isTaskCollideByDate(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }


        if (task1.getEndTime().isAfter(task2.getStartTime())
            && task1.getStartTime().isBefore(task2.getEndTime())) {
            return true;
        }

        if (task2.getStartTime().isBefore(task1.getEndTime())
            && task2.getEndTime().isAfter(task1.getStartTime())) {
            return true;
        }

        return false;
    }
}