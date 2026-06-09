package ru.netology.cicdspringboothw;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CiCdSpringbootHwApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTask() throws Exception {
        Task newTask = new Task(null, "Купить молоко", false);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("Купить молоко"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void shouldReturnBadRequestOnEmptyDescription() throws Exception {
        Task badTask = new Task(null, "", false);
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        Task task = new Task(null, "Тест", false);
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Тест"));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Task task = new Task(null, "Старое", false);
        var result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andReturn();
        Task created = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        Task updated = new Task(created.getId(), "Новое", true);
        mockMvc.perform(put("/tasks/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Новое"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Task task = new Task(null, "Удаляемая", false);
        var result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andReturn();
        Task created = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);

        mockMvc.perform(delete("/tasks/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturn404ForNonExistingTask() throws Exception {
        mockMvc.perform(put("/tasks/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Task(9999L, "x", false))))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/tasks/9999"))
                .andExpect(status().isNotFound());
    }

}
