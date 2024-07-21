package model;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, Status.NEW, description);
    }

    // Конструктор для загрузки из файла
    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description);
    }

    public Epic(int id, String name, String description) {
//        Конструктор эпика не должен принимать на вход параметр со статусом, так как по ТЗ он задается автоматически на основе его подзадач.
        super(id, name, Status.NEW, description);
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(int id) {
        subTasks.add(id);
    }

    public void updateSubTask(SubTask subTask) {
        for (Integer id : subTasks) {
            if (id.equals(subTask.getId())) {
                subTasks.remove(id);
            }
        }
        subTasks.add(subTask.getId());
    }

    public void removeSubTask(Integer subTask) {
        subTasks.remove(subTask);
    }

    @Override
    public TaskType getType() { return TaskType.EPIC; }
}