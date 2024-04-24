package model;
public class SubTask extends Task {
    private final int idEpic;

    public SubTask(String name, Status status, String description,int idEpic) { // для создания подзадачи
        super(name, status, description);
        this.idEpic = idEpic;
    }

    public int getEpic() {
        return idEpic;
    }
}