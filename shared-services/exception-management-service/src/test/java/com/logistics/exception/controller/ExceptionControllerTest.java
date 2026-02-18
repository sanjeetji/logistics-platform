package com.logistics.exception.controller;

import com.logistics.exception.model.ExceptionRecord;
import com.logistics.exception.service.ExceptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.jupiter.api.Disabled("Fails on Java 24 due to Mockito/ByteBuddy compatibility")
@WebMvcTest(ExceptionController.class)
class ExceptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExceptionService exceptionService;

    @Test
    void testGetAllExceptions() throws Exception {
        ExceptionRecord record = ExceptionRecord.builder()
                .exceptionId("EX-101")
                .status(ExceptionRecord.ExceptionStatus.OPEN)
                .build();

        given(exceptionService.getAllExceptions()).willReturn(List.of(record));

        mockMvc.perform(get("/api/exceptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exceptionId").value("EX-101"));
    }

    @Test
    void testResolveException() throws Exception {
        ExceptionRecord resolvedRecord = ExceptionRecord.builder()
                .exceptionId("EX-101")
                .status(ExceptionRecord.ExceptionStatus.RESOLVED)
                .resolvedBy("admin")
                .build();

        given(exceptionService.resolveException(anyString(), anyString(), anyString()))
                .willReturn(resolvedRecord);

        mockMvc.perform(put("/api/exceptions/UUID-1/resolve")
                .param("resolvedBy", "admin")
                .param("notes", "Fixed it"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }
}
