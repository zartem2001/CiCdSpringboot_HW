package ru.netology.cicdspringboothw;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Task {
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 1, max = 200, message = "Длина описания от 1 до 200 символов")
    private String description;

    private boolean completed;
    public Task() {
    }

    public Task(Long id, String description, boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
