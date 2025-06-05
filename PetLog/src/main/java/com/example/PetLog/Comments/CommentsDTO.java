package com.example.PetLog.Comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentsDTO {
    Long com_id;
    int user_id;
    int post_id;
    String com_com;
    int parent_id;
    int depth;
    int snack_id;
}
