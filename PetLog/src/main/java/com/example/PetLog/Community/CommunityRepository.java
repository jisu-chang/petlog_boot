package com.example.PetLog.Community;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = "update community set post_readcnt=post_readcnt+1 where post_id=:post_id", nativeQuery = true)
    void readup(@Param("post_id") Long num);
}
