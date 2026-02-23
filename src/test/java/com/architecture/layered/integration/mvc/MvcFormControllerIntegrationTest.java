package com.architecture.layered.integration.mvc;

import com.architecture.layered.domain.User;
import com.architecture.layered.presentation.common.dto.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/test-schema.sql", "/test-data.sql"})
class MvcFormControllerIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    void shouldShowSearchForm() throws Exception {
        mvc.perform(get("/mvc/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("form/main-page-search"))
                .andExpect(content().string(containsString("Search User by Name")))
                .andExpect(content().string(containsString("Create New User")));
    }

    @Test
    void shouldShowCreateForm() throws Exception {
        mvc.perform(get("/mvc/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("form/create-form"))
                .andExpect(model().attributeExists("request"))
                .andExpect(content().string(containsString("Create New User")))
                .andExpect(content().string(containsString("action=\"/mvc/users\"")));
    }

    @Test
    void shouldShowEditForm() throws Exception {
        mvc.perform(get("/mvc/users/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("form/edit-form"))
                .andExpect(model().attribute("response", Mapper.toResponse(
                        User.create("1", "Alice", LocalDate.of(1990, 1, 1))
                )))
                .andExpect(content().string(containsString("Alice")))
                .andExpect(content().string(containsString("1990-01-01")))
                .andExpect(content().string(containsString("action=\"/mvc/users/1\"")));
    }

    @Test
    void shouldShowDeleteConfirmForm() throws Exception {
        mvc.perform(get("/mvc/users/1/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("form/delete-confirm-form"))
                .andExpect(model().attribute("response", Mapper.toResponse(
                        User.create("1", "Alice", LocalDate.of(1990, 1, 1))
                )))
                .andExpect(content().string(containsString("Alice")))
                .andExpect(content().string(containsString("action=\"/mvc/users/1/delete\"")));
    }

    @Test
    void shouldShowErrorWhenEditingNonExistentUser() throws Exception {
        mvc.perform(get("/mvc/users/99/edit"))
                .andExpect(view().name("form/main-page-search"))
                .andExpect(model().attribute("error", "User not found: 99"))
                .andExpect(content().string(containsString("User not found: 99")));
    }

}
