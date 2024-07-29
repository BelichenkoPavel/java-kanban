package service;

import exceptions.ManagerSaveException;
import model.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.function.Consumer;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static String fileName = "./data.csv";

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            bufferedWriter.write("id,type,name,status,description,epic,duration,startTime\n");

            Consumer<Task> taskConsumer = task -> {
                try {
                    bufferedWriter.write(toString(task));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            getAllTasks().stream().forEach(taskConsumer);
            getAllEpics().stream().forEach(taskConsumer);
            getAllSubTasks().stream().forEach(taskConsumer);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время сохранения");
        }
    }

    public static FileBackedTaskManager loadFromFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            while (br.ready()) {
                String data = br.readLine();
                Task task = manager.fromString(data);

                if (task.getType() == TaskType.SUB_TASK) {
                    manager.addSubTask((SubTask) task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.addEpic((Epic) task);
                } else {
                    manager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время сохранения задач");
        }

        return manager;
    }

    private String toString(Task task) {
        TaskType type = TaskType.TASK;
        String epicId = "";

        if (task instanceof SubTask) {
            epicId = String.valueOf(((SubTask) task).getEpic());
            type = TaskType.SUB_TASK;
        } else if (task instanceof Epic) {
            type = TaskType.EPIC;
        }

        return task.getId()
                + "," + type
                + "," + task.getName()
                + "," + task.getStatus()
                + "," + task.getDescription()
                + "," + epicId
                + "," + task.getDuration()
                + "," + task.getStartTime()
                + "\n";
    }

    private Task fromString(String value) {
        String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String name = values[2];
        Status status = Status.valueOf(values[3]);
        String description = values[4];
        Duration duration = Duration.parse(values[6]);
        LocalDateTime startTime = null;

        if (!values[7].equals("null")) {
            startTime = LocalDateTime.parse(values[7]);
        }


        if (type == TaskType.SUB_TASK) {
            int epicId = Integer.parseInt(values[5]);
            return new SubTask(id, name, status, description, epicId, duration, startTime);
        }

        if (type == TaskType.EPIC) {
            return new Epic(id, name, status, description, duration, startTime);
        }

        return new Task(id, name, status, description, duration, startTime);
    }

    private Task addTask(Task task) {
        tasks.put(task.getId(), task);

        return task;
    }

    private Task addEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        return epic;
    }

    private Task addSubTask(SubTask subTask) {
        subtasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpic());
        epic.addSubTask(subTask);
        calculateStatus(epic);

        return subTask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();

        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();

        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subtask) {
        SubTask subTask = super.createSubTask(subtask);
        save();

        return subTask;
    }

    @Override
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask, int id) {
        super.updateSubTask(subtask, id);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager();
        manager.createTask(new Task("Проверить сохранение зачач в файл", Status.DONE, "Задачи должны сохраняться в файл"));
        manager.createTask(new Task("Проверить загрузку зачач из файла", Status.IN_PROGRESS, "Задачи должны быть загружены из файла"));
        Task taskWithTime = new Task(
                "Проверить сохранение задачи с временем",
                Status.DONE,
                "Задано время начала и продолжительность",
                Duration.ofSeconds(12345678),
                LocalDateTime.of(2024, Month.JULY, 25, 17, 22, 0));
        manager.createTask(taskWithTime);

        Epic epic = new Epic("Написать тесты", "Проверить новый фнкционал");
        manager.createEpic(epic);

        manager.createSubTask(new SubTask("Подзадача 1", Status.NEW, "Загрука пустого файла", epic.getId()));
        manager.createSubTask(new SubTask("Подзадача 2", Status.DONE, "Загрузка файла с задачами", epic.getId()));

        manager.save();

        manager = FileBackedTaskManager.loadFromFile();

        System.out.println("Задачи:");
        System.out.println(manager.getAllTasks());

        System.out.println("Эпики:");
        System.out.println(manager.getAllEpics());

        System.out.println("Подазадачи:");
        System.out.println(manager.getAllSubTasks());
    }
}
