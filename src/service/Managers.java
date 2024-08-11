package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.http.adapters.DurationAdapter;
import service.http.adapters.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {
    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
