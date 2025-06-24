package com.example.PetLog.Comments;

import com.example.PetLog.Community.CommunityEntity;
import com.example.PetLog.Snack.SnackEntity;
import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "comments")
@SequenceGenerator(
        name = "comments",
        sequenceName = "comment_seq",
        allocationSize = 1,
        initialValue = 1
)
public class CommentsEntity {
    @Id
    @Column (name = "com_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "comments")
    Long comId;
    @Column (name = "com_com")
    String comCom;
    @Column (name = "parent_id")
    int parentId;
    @Column
    int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snack_id")
    SnackEntity snack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserEntity user;  // 댓글을 작성한 사용자 (유저 테이블과 연결)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    CommunityEntity community;  // 댓글은 게시글에 속함

}
