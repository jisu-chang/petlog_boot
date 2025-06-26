package com.example.PetLog.Comments;

import com.example.PetLog.Community.CommunityEntity;
import com.example.PetLog.Community.CommunityRepository;
import com.example.PetLog.Snack.SnackEntity;
import com.example.PetLog.Snack.SnackRepository;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentsServiceImp implements CommentsService{

    @Autowired
    CommentsRepository commentsRepository;
    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SnackRepository snackRepository;

    //회원탈퇴
    @Override
    public List<CommentsEntity> findByUserId(Long userId) {
        return commentsRepository.findAllByUser_UserId(userId);
    }

    //회원탈퇴
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        commentsRepository.deleteByUser_UserId(userId);
    }

    //댓글 저장
    @Override
    public void saveComment(CommentsDTO commentsDTO) {
        CommentsEntity entity = toEntity(commentsDTO);

        //게시글 아이디로 DB에서 찾아온 Entity를 넣어줘야함
        // 유저 설정
        UserEntity user = userRepository.findById(commentsDTO.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        entity.setUser(user);

        // 게시판 분기 설정 (하나만 연결)
        if (commentsDTO.getPost_id() != null) { //커뮤니티 댓글인 경우
            CommunityEntity community = communityRepository.findById(commentsDTO.getPost_id())
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
            entity.setCommunity(community); //커뮤니티 연결
            entity.setSnack(null); //  snack은 null
        } else if (commentsDTO.getSnack_id() != null) { //스낵 댓글인 경우
            SnackEntity snack = snackRepository.findById(commentsDTO.getSnack_id())
                    .orElseThrow(() -> new IllegalArgumentException("해당 간식레시피가 존재하지 않습니다."));
            entity.setSnack(snack); //스낵 연결
            entity.setCommunity(null); // community는 null
        }

        commentsRepository.save(entity);
    }
    // DTO -> Entity로 변환
    CommentsEntity toEntity(CommentsDTO dto) {
        CommentsEntity entity = new CommentsEntity();
        entity.setComCom(dto.getCom_com());
        entity.setParentId(dto.getParent_id());
        entity.setDepth(dto.getDepth());
        return entity;
    }

    //특정 게시글의 댓글 목록 조회
    @Override
    public List<CommentsDTO> getCommentsByPostId(Long postId) {
        List<CommentsEntity> comments = commentsRepository.findCommentsByPostId(postId);
        return comments.stream()
                .filter(Objects::nonNull) // comments 리스트 자체에 null CommentsEntity가 있다면 필터링
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //Entity -> DTO로 변환
    CommentsDTO toDTO(CommentsEntity comment) {
        CommentsDTO dto = new CommentsDTO();
        dto.setCom_id(comment.getComId());
        dto.setCom_com(comment.getComCom());
        dto.setParent_id(comment.getParentId());
        dto.setDepth(comment.getDepth());

        dto.setUser(comment.getUser());
        dto.setUserLoginId(comment.getUser().getUserLoginId());

        // Snack이 null인 경우를 처리
        if (comment.getSnack() != null) {
            dto.setSnack_id(comment.getSnack().getSnackId()); // getSnackId 호출 전에 null 체크
        }
        //작성자 로그인 아이디 담기
        if (comment.getUser() != null) {
            dto.setUser(comment.getUser()); // UserEntity 객체 자체를 DTO에 담기
        }
        return dto;
    }

    @Override
    public List<CommentsEntity> getSnackComments(Long snackId) {
        return commentsRepository.findBySnack_SnackId(snackId);
    }

}
