package com.example.PetLog.Snack;

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
@Data
@Builder
@Table(name = "snack")
@SequenceGenerator(
        name = "snack_id",
        sequenceName = "snack_seq",
        allocationSize = 1,
        initialValue = 1
)
public class SnackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "snack_id")
    @Column (name = "snack_id")
    Long snackId;
    @Column (name = "snack_title")
    String snackTitle;
    @Column (name = "snack_recipe")
    String snackRecipe;
    @Column (name = "snack_image")
    String snackImage;
    @Column (name = "snack_date")
    LocalDate snackDate;
    @Column (name = "user_id", nullable = false)
    Long userId;
    @Column (name = "snack_readcnt")
    int snackReadcnt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;

    // 댓글과의 관계 (게시글은 여러 댓글을 가질 수 있음)
    @OneToMany(mappedBy = "snack", fetch = FetchType.LAZY)
    List<CommentsEntity> comments;

    // 좋아요와의 관계 (게시글은 여러 좋아요를 가질 수 있음)
    @OneToMany(mappedBy = "snack", fetch = FetchType.LAZY)
    List<LikesEntity> likes;
}
