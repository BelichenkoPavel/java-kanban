package model;

import java.util.HashMap;

public class Epic extends Task {

    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public Epic() {
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    public void removeSubTask(int ID) {
        subTasks.remove(ID);
    }
}