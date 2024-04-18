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
    private final HashMap<Integer, SubTask> subtasks;
    private int numb = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    private int generateId() {
        return ++numb;
    }

    //a
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }


    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicsList.add(epic);
        }
        return epicsList;
    }

    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subtasksList = new ArrayList<>();
        for (SubTask subtask : subtasks.values()) {
            subtasksList.add(subtask);
        }
        return subtasksList;
    }

    public ArrayList<SubTask> getSubTasksOfEpic(Epic epic) {
        ArrayList<SubTask> subtasksList = new ArrayList<>();
        for (Integer subtask : epic.getSubTasks()) {
            subtasksList.add(subtasks.get(subtask));
        }
        return subtasksList;
    }

    //b
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
        }
        subtasks.clear();
    }


    // c
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {
        return subtasks.get(id);
    }

    // d
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
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

    //e
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);

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

    public void updateSubTask(SubTask subtask) {
        SubTask saved = getSubTask(subtask.getId());
        saved.setName(subtask.getName());
        saved.setStatus(subtask.getStatus());
        Epic epic = epics.get(subtask.getEpic());
        epic.addSubTask(subtask.getId());
        calculateStatus(epic);
    }

    //f
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
        int statusNew = 0;
        int statusDone = 0;
        if (epic.getSubTasks() == null) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (Integer subtaskId : epic.getSubTasks()) {
            SubTask subTask = subtasks.get(subtaskId);
            Status status = subTask.getStatus();
            if (status.equals(Status.NEW)) {
                statusNew += 1;
            }
            if (status.equals(Status.DONE)) {
                statusDone += 1;
            }
        }
        if (statusNew == epic.getSubTasks().size()) {
            epic.setStatus(Status.NEW);

        } else if (statusDone == epic.getSubTasks().size()) {
            epic.setStatus(Status.DONE);

        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}