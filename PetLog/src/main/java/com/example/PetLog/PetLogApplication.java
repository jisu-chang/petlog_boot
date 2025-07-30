package com.example.PetLog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetLogApplication {

	public static void main(String[] args) {
		// .env 읽고 시스템 속성으로 등록
//		Dotenv dotenv = Dotenv.configure().load();
		Dotenv dotenv = Dotenv.configure().directory("PetLog").load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		System.out.println("SMTP_USER: " + System.getProperty("SMTP_USER"));

		SpringApplication.run(PetLogApplication.class, args);
	}
}

/*
오라클 복붙용 데이터

관리자 비밀번호 admin1234
INSERT INTO user1 (user_id, user_login_id, password, user_role, name, phone, email, profileimg, rank, grape_count)
VALUES ( user_id_seq.NEXTVAL, 'admin01',  '$2a$10$zZrrQxQQ5P3UN9TP2Ezb7erqwew/iM/ifFBxV2rN6B/JZwtXZDzt6', 'ADMIN', '관리자', '010-1111-1111', 'petlog.team@gmail.com', 'default.png','관리자','0');

commit;
*/
