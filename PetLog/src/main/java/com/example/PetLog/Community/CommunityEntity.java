package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Likes.LikesEntity;
import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @Column(name = "user_id", nullable = false) //nullable = false => null 값 허용x
    Long userId;
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
    @Column(name = "post_type")
    String postType;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;  //userEntity 객체 설정하여 컬럼 추가 없이 유저 로그인 아이디도 가져다 쓸 수 있음

    // 댓글과의 관계 (게시글은 여러 댓글을 가질 수 있음)
    @OneToMany(mappedBy = "community", fetch = FetchType.LAZY)
    List<CommentsEntity> comments;

    // 좋아요와의 관계 (게시글은 여러 좋아요를 가질 수 있음)
    @OneToMany(mappedBy = "community", fetch = FetchType.LAZY)
    List<LikesEntity> likes;
}
