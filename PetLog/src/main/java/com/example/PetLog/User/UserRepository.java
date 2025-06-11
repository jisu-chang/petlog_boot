package com.example.PetLog.User;

import aj.org.objectweb.asm.commons.Remapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUserLoginId(String userLoginId);

    Optional<UserEntity> findOptionalByEmail(String email);

    Optional<UserEntity> findByNameAndUserLoginIdAndEmailAndPhone(String name, String userLoginId, String email, String phone);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM user1 WHERE user_login_id = :userLoginId", nativeQuery = true)
    int existsByUserLoginIdNative(@Param("userLoginId") String userLoginId);

    Optional<UserEntity> findByNameAndEmailAndPhone(String name, String email, String phone);

    boolean existsByUserLoginId(String userLoginId);
}
