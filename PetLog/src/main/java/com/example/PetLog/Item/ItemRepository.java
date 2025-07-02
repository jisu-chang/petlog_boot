package com.example.PetLog.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity,Long> {

    List<ItemEntity> findByItemStatus(String itemStatus);

    //page
    @Query(value =
            "SELECT * FROM ( " +
                    "    SELECT inner_table.*, ROWNUM AS rn FROM ( " +
                    "        SELECT * FROM ITEMS WHERE ITEM_STATUS = :status ORDER BY ITEM_ID DESC " +
                    "    ) inner_table WHERE ROWNUM <= :end " +
                    ") WHERE rn >= :start",
            nativeQuery = true)
    List<ItemEntity> findByItemStatusWithPaging(@Param("status") String status,
                                                @Param("start") int start,
                                                @Param("end") int end);

    @Query(value = "SELECT COUNT(*) FROM ITEMS WHERE ITEM_STATUS = :status", nativeQuery = true)
    int countByItemStatus(@Param("status") String status);
}
