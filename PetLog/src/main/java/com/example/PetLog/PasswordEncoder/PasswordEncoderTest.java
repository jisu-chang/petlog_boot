package com.example.PetLog.PasswordEncoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

        public static void main(String[] args) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = "qqqq1111"; // 원하는 비밀번호
            String encodedPassword = encoder.encode(rawPassword);
            System.out.println("암호화 된 비밀번호: " + encodedPassword);
        }


/*
오라클 복붙용 데이터

관리자 비밀번호 admin1234
INSERT INTO user1 (user_id, user_login_id, password, user_role, name, phone, email, profileimg, rank, grape_count)
VALUES ( user_id_seq.NEXTVAL, 'admin01',  '$2a$10$zZrrQxQQ5P3UN9TP2Ezb7erqwew/iM/ifFBxV2rN6B/JZwtXZDzt6', 'ADMIN', '관리자', '010-1111-1111', 'petlog.team@gmail.com', 'default.png','관리자','0');

commit;
*/

}