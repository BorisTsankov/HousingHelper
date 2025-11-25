package nl.fontys.s3.back_end;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class BackEndApplicationTests {

    @MockBean
    private JavaMailSender javaMailSender;

	@Test
	void contextLoads() {
	}

}
