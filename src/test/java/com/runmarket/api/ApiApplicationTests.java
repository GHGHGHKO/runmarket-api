package com.runmarket.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.runmarket.api.adapter.out.email.EmailAdapter;

@SpringBootTest
@ActiveProfiles("test")
class ApiApplicationTests {

    @MockitoBean EmailAdapter emailAdapter;

	@Test
	void contextLoads() {
	}

}
