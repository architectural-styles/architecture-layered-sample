package com.architecture.layered.integration.mvc;

import com.architecture.layered.domain.User;
import com.architecture.layered.presentation.common.dto.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/test-schema.sql", "/test-data.sql"})
class MvcQueryIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    void shouldFindById() throws Exception {
        mvc.perform(get("/mvc/users/search/id").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("result/user-details"))
                .andExpect(model().attribute("response", Mapper.toResponse(
                        User.create("1", "Alice", LocalDate.of(1990, 1, 1))
                )))
                .andExpect(content().string(containsString("Alice")))
                .andExpect(content().string(containsString("1990-01-01")))
                .andExpect(content().string(containsString("/mvc/users/1/edit")))
                .andExpect(content().string(containsString("/mvc/users/1/delete")));
    }

    @Test
    void shouldShowErrorWhenUserNotFound() throws Exception {
        mvc.perform(get("/mvc/users/search/id").param("id", "99"))
                .andExpect(view().name("form/main-page-search"))
                .andExpect(model().attribute("error", "User not found: 99"))
                .andExpect(content().string(containsString("User not found: 99")));
    }

    @Test
    void shouldFindByName() throws Exception {
        mvc.perform(get("/mvc/users/search/name").param("name", "A"))
                .andExpect(status().isOk())
                .andExpect(view().name("result/list"))
                .andExpect(model().attribute("searchTerm", "A"))
                .andExpect(model().attribute("userViews", List.of(
                        Mapper.toResponse(User.create("1", "Alice", LocalDate.of(1990, 1, 1))),
                        Mapper.toResponse(User.create("3", "Anna",  LocalDate.of(2000, 3, 15)))
                )))
                .andExpect(content().string(containsString("Alice")))
                .andExpect(content().string(containsString("Anna")))
                .andExpect(content().string(containsString("/mvc/users/search/id?id=1")))
                .andExpect(content().string(containsString("/mvc/users/search/id?id=3")))
                .andExpect(content().string(containsString("/mvc/users/1/edit")))
                .andExpect(content().string(containsString("/mvc/users/3/edit")))
                .andExpect(content().string(containsString("/mvc/users/1/delete")))
                .andExpect(content().string(containsString("/mvc/users/3/delete")));
    }

    @Test
    void shouldReturnEmptyListWhenNoPrefixMatch() throws Exception {
        mvc.perform(get("/mvc/users/search/name").param("name", "ZZZ"))
                .andExpect(status().isOk())
                .andExpect(view().name("result/list"))
                .andExpect(model().attribute("searchTerm", "ZZZ"))
                .andExpect(model().attribute("userViews", List.of()))
                .andExpect(content().string(containsString("No results found.")));
    }

}
