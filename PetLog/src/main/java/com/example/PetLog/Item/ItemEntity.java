package com.example.PetLog.Item;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "ITEMS")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_seq")
    @SequenceGenerator(
            name = "items_seq",
            sequenceName = "items_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Column(name = "item_id")
    Long itemId;

    @Column(name = "item_name")
    String itemName;

    @Column(name = "item_cost")
    Long itemCost;

    @Column(name = "item_category")
    String itemCategory;

    @Column(name = "item_image")
    String itemImage;

    @Column(name = "item_status")
    String itemStatus;

}
