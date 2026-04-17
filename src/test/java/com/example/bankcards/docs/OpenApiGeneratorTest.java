package com.example.bankcards.docs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class OpenApiGeneratorTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void generateOpenApiYaml() throws Exception {
        byte[] content = mockMvc.perform(get("/v3/api-docs.yaml"))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        Files.write(
                Path.of("docs/openapi.yaml"),
                content
        );
    }
}
