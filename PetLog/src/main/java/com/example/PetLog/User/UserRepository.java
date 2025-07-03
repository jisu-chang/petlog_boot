package com.example.PetLog.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserLoginId(String userLoginId);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findOptionalByEmail(@Param("email") String email);

    Optional<UserEntity> findByNameAndUserLoginIdAndEmailAndPhone(String name, String userLoginId, String email, String phone);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM user1 WHERE user_login_id = :userLoginId", nativeQuery = true)
    int existsByUserLoginIdNative(@Param("userLoginId") String userLoginId);


    Optional<UserEntity> findByNameAndEmailAndPhone(String name, String email, String phone);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM user1 WHERE user_login_id = :userLoginId", nativeQuery = true)
    int existsByUserLoginId(@Param("userLoginId") String userLoginId);

    //관리자 포도알 관리 다솜
    List<UserEntity> findAllByOrderByGrapeCountDesc();

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);
}
