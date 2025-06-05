package com.example.PetLog.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity u where u.userId=:userId")
    UserEntity MyPagefindById(@Param("userId") Long userId);


    UserEntity findByUserLoginId(String userLoginId);


}
