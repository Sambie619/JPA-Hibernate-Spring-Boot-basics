package com.sabin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabin.entity.Task;
import com.sabin.entity.User;
import com.sabin.repository.TaskRepository;
import com.sabin.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskManagerJwtIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Start each test with a clean in-memory database.
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerCreatesUserWithEncodedPassword() throws Exception {
        String requestBody = """
                {
                  "username": "sabin",
                  "email": "sabin@example.com",
                  "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByUsername("sabin").orElseThrow();
        org.junit.jupiter.api.Assertions.assertNotEquals("secret123", savedUser.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("secret123", savedUser.getPassword()));
        org.junit.jupiter.api.Assertions.assertEquals("USER", savedUser.getRole());
    }

    @Test
    void registerRejectsDuplicateUsername() throws Exception {
        saveUser("sabin", "first@example.com", "secret123", "USER");

        String requestBody = """
                {
                  "username": "sabin",
                  "email": "second@example.com",
                  "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void loginReturnsJwtTokenForValidCredentials() throws Exception {
        saveUser("sabin", "sabin@example.com", "secret123", "USER");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("sabin", "secret123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginRejectsInvalidPassword() throws Exception {
        saveUser("sabin", "sabin@example.com", "secret123", "USER");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("sabin", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tasksRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidTokenIsRejectedWithoutCrashingTheRequest() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer definitely-not-a-real-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanCreateReadUpdateAndDeleteOwnTask() throws Exception {
        saveUser("sabin", "sabin@example.com", "secret123", "USER");
        String token = loginAndGetToken("sabin", "secret123");

        String createBody = """
                {
                  "title": "Finish API",
                  "description": "Write task endpoints",
                  "status": "PENDING"
                }
                """;

        String createResponse = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Finish API"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Finish API"));

        String updateBody = """
                {
                  "title": "Finish API v2",
                  "description": "Add tests too",
                  "status": "COMPLETED"
                }
                """;

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Finish API v2"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(delete("/api/tasks/{id}", taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void userCannotUpdateAnotherUsersTask() throws Exception {
        User owner = saveUser("owner", "owner@example.com", "secret123", "USER");
        saveUser("other", "other@example.com", "secret123", "USER");

        Task task = new Task();
        task.setTitle("Private task");
        task.setDescription("Only owner should edit this");
        task.setStatus(Task.TaskStatus.PENDING);
        task.setUser(owner);
        Task savedTask = taskRepository.save(task);

        String otherUserToken = loginAndGetToken("other", "secret123");
        String updateBody = """
                {
                  "title": "Hacked title",
                  "description": "Should not work",
                  "status": "COMPLETED"
                }
                """;

        mockMvc.perform(put("/api/tasks/{id}", savedTask.getId())
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());
    }

    private User saveUser(String username, String email, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        // Use the real login endpoint so the test covers authentication too.
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("token").asText();
    }

    private String loginBody(String username, String password) throws Exception {
        // Small helper so the request JSON stays consistent across tests.
        return objectMapper.writeValueAsString(new LoginRequestPayload(username, password));
    }

    private record LoginRequestPayload(String username, String password) {
    }
}
