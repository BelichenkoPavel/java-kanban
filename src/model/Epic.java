package model;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private LocalDateTime endTime;

    private final ArrayList<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, Status.NEW, description);
    }

    // Конструктор для загрузки из файла
    public Epic(int id, String name, Status status, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
    }

    public Epic(int id, String name, String description) {
//        Конструктор эпика не должен принимать на вход параметр со статусом, так как по ТЗ он задается автоматически на основе его подзадач.
        super(id, name, Status.NEW, description);
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        duration = duration.plus(subTask.getDuration());
        subTasks.add(subTask.getId());

        if (subTask.getStartTime() == null) {
            return;
        }

        if (startTime == null || subTask.getStartTime().isBefore(startTime)) {
            startTime = subTask.getStartTime();
        }

        if (endTime == null || subTask.getEndTime().isAfter(endTime)) {
            endTime = subTask.getEndTime();
        }
    }

    public void updateSubTask(SubTask subTask) {
        for (Integer id : subTasks) {
            if (id.equals(subTask.getId())) {
                subTasks.remove(id);
            }
        }
        subTasks.add(subTask.getId());
    }

    public void deleteSubTask(SubTask subTask) {
        subTasks.remove(Integer.valueOf(subTask.getId()));
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public  void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}