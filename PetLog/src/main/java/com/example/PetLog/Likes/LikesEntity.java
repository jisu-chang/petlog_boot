package com.example.PetLog.Likes;

import com.example.PetLog.Community.CommunityEntity;
import com.example.PetLog.Snack.SnackEntity;
import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Likes")
@SequenceGenerator(
        name = "likes",
        sequenceName = "likesnum_seq",
        allocationSize = 1,
        initialValue = 1
)
public class LikesEntity {
    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "likes")
    Long likeId;
    @Column(name = "user_id")
    int userId;
    @Column(name = "post_id")
    int postId;
    @Column(name = "user_login_id")
    String userLoginId;
    @Column(name = "snack_id")
    int snackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;  // 유저와 연결 (좋아요를 누른 유저)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", insertable = false, updatable = false)
    CommunityEntity community;  // 게시글과 연결 (좋아요가 눌린 게시글)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snack_id", referencedColumnName = "snack_id", insertable = false, updatable = false)
    SnackEntity snack;  // 스낵과 연결 (좋아요가 눌린 스낵)
}
