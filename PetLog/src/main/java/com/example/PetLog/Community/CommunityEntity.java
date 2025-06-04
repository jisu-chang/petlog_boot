package com.example.PetLog.Community;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "community")
@SequenceGenerator(
        name = "community",
        sequenceName = "postnum_seq",
        allocationSize = 1,
        initialValue = 1)
public class CommunityEntity {
    @Id
    @Column
            @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "community")
    Long post_id;
    @Column
    int user_id;
    @Column
    String post_title;
    @Column
    String post_content;
    @Column
    String post_image;
    @Column
    int post_readcnt;
    @Column
    String post_date;
//    @Column
//     String user_login_id;
//    @Column
//    int comment_count;
//    @Column
//    int like_count;
//    @Column
//    String profileimg;
    @Column
    String post_type;
}

