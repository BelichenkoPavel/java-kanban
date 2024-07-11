package service;

import model.Task;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private Node first;

    private Node last;

    private Map<Integer, Node> history = new HashMap<>();

    public void add(Task task) {
        if (task == null) {
            return;
        }

        remove(task.getId());

        Node node = new Node(task, last);
        history.put(task.getId(), node);

        if (first == null) {
            first = node;
        }

        linkLast(node);
    }

    private void linkLast(Node node) {
        last = node;
    }

    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node current = first;

        while (current != null) {
            tasks.add(current.value);
            current = current.next;
        }

        return tasks;
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);

        if (node == null) {
            return;
        }

        removeNode(node);
    }

    private void removeNode(Node node) {
        if (node.prev == null) {
            first = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            last = node.prev;
        } else {
            node.next.prev = node.prev;
        }
    }

    static class Node {
        Task value;

        Node prev;

        Node next;

        Node(Task value, Node prev) {
            this.value = value;
            this.next = null;

            if (prev != null) {
                this.prev = prev;
                prev.next = this;
            }

        }
    }
}
