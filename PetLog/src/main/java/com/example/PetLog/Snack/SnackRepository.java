package com.example.PetLog.Snack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnackRepository extends JpaRepository<SnackEntity, Long> {

    List<SnackEntity> findAllByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);

    List<SnackEntity> findBySnackTitleContaining(String keyword);

    List<SnackEntity> findBySnackRecipeContaining(String keyword);

    @Query(value =
            "SELECT * FROM ( " +
                    "  SELECT a.*, ROWNUM rnum FROM ( " +
                    "    SELECT * FROM snack ORDER BY snack_date DESC " +
                    "  ) a WHERE ROWNUM <= :offset + :limit " +
                    ") WHERE rnum > :offset",
            nativeQuery = true)
    List<SnackEntity> findSnacksPaged(@Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM snack", nativeQuery = true)
    int countAllSnacks();

    int countByUserId(Long userId);

    List<SnackEntity> findByUser_UserId(Long userId);
}
