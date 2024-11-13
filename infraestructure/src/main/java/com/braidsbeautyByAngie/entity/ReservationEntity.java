package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_Id", nullable = false)
    private Long reservationId;
    @Column(name = "reservation_state", nullable = false)
    private String reservationState;
    @Column(name = "shopping_cart_item_Id", nullable = false)
    private Long shoppingCartItemId;
    @Column(name = "order_line_Id", nullable = false)
    private Long orderLineId;

    @OneToMany(mappedBy = "reservationEntity", cascade = CascadeType.ALL)
    private List<WorkServiceEntity> workServiceEntities = new ArrayList<>();

    @Column(name = "state", nullable = false)
    private Boolean state;

    @Column(name = "modified_by_user", nullable = false, length = 15)
    private String modifiedByUser;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

}
