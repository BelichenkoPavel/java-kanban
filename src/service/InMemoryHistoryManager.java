package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history = new ArrayList<Task>();

    public void add(Task task) {
        if (history.size() >= 10) {
            history.removeFirst();
        }

        history.add(task);
    }

    public ArrayList<Task> getHistory() {
       return history;
    }
}
