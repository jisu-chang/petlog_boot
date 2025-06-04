package com.example.PetLog.Community;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    @Column (name = "post_id")
            @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "community")
    Long postId;
    @Column(name = "user_id", nullable = false)
    int userId;
    @Column(name = "post_title", nullable = false)
    String postTitle;
    @Lob
    @Column(name = "post_content", nullable = false)
    String postContent;
    @Column(name = "post_image")
    String postImage;
    @Column(name = "post_readcnt")
    int postReadcnt;
    @Column(name = "post_date")
    LocalDate postDate;
//    @Column
//     String user_login_id;
//    @Column
//    int comment_count;
//    @Column
//    int like_count;
//    @Column
//    String profileimg;
    @Column(name = "post_type")
    String postType;
}

