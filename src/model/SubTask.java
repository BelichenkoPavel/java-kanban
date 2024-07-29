package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int idEpic;

    // Конструктор для загрузки из файла
    public SubTask(int id, String name, Status status, String description, int idEpic, Duration duration, LocalDateTime startTime) { // для создания подзадачи
        super(id, name, status, description, duration, startTime);
        this.idEpic = idEpic;
    }

    public SubTask(String name, Status status, String description,int idEpic) { // для создания подзадачи
        super(name, status, description);
        this.idEpic = idEpic;
    }

    public SubTask(String name, Status status, String description,int idEpic, Duration duration, LocalDateTime startTime) { // для создания подзадачи
        super(name, status, description, duration, startTime);
        this.idEpic = idEpic;
    }

    public int getEpic() {
        return idEpic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }
}