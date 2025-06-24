package com.example.PetLog.Snack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnackServiceImp implements SnackService {

    @Autowired
    SnackRepository snackRepository;

    @Override
    public void save(SnackEntity snackEntity) {
        snackRepository.save(snackEntity);
    }

    @Override
    public List<SnackDTO> out() {
        List<SnackEntity> list = snackRepository.findAll();

        return list.stream().map(entity -> {
            SnackDTO dto = new SnackDTO();
            dto.setSnackId(entity.getSnackId());
            dto.setSnackTitle(entity.getSnackTitle());
            dto.setSnackRecipe(entity.getSnackRecipe());
            dto.setSnackImagename(entity.getSnackImage());
            dto.setSnackDate(entity.getSnackDate());
            dto.setSnackReadcnt(entity.getSnackReadcnt());
            dto.setUserId(entity.getUserId());

            // 💡 user_login_id도 필요하다면 user 객체가 lazy 로딩 되지 않게 처리 필요
            if (entity.getUser() != null) {
                dto.setUserLoginId(entity.getUser().getUserLoginId());
            }

            return dto;
        }).toList();
    }

    @Override
    public SnackDTO detail(long snackId) {
        SnackEntity snackEntity = snackRepository.findById(snackId)
                .orElseThrow(()->new RuntimeException("간식 레시피를 찾을 수 없습니다: " + snackId));

        String userLoginId = (snackEntity.getUser() !=null) ? snackEntity.getUser().getUserLoginId() : "알 수 없음";

        // 3. SnackEntity → SnackDTO 변환
        SnackDTO dto = new SnackDTO();
        dto.setSnackId(snackEntity.getSnackId());
        dto.setSnackTitle(snackEntity.getSnackTitle());
        dto.setSnackRecipe(snackEntity.getSnackRecipe());
        dto.setSnackImagename(snackEntity.getSnackImage());
        dto.setSnackDate(snackEntity.getSnackDate());
        dto.setSnackReadcnt(snackEntity.getSnackReadcnt());
        dto.setUserId(snackEntity.getUserId());
        dto.setUserLoginId(userLoginId);

        return dto;
    }

    @Override
    public SnackEntity getSnack(Long snackId) { //snackId에 해당하는 데이터 가져오기 from DB
        return snackRepository.findById(snackId).orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다: " + snackId));
    }

    @Override
    public void update(SnackEntity entity) {
        snackRepository.save(entity);
    }

    @Override
    public void delete(Long snackId) {
        snackRepository.deleteById(snackId);
    }

    //지수 추가 - 회원탈퇴
    @Override
    public List<SnackEntity> findByUserId(Long userId) {
        return snackRepository.findAllByUser_UserId(userId);
    }
    //지수 추가 - 회원탈퇴
    @Override
    public void deleteByUserId(Long userId) {
        snackRepository.deleteByUser_UserId(userId);
    }

    @Override
    public SnackEntity getSnackById(Long snackId) {
        return snackRepository.findById(snackId).orElse(null);
    }


}
