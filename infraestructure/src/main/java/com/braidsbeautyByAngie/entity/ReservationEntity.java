package com.braidsbeautyByAngie.entity;

import com.braidsbeautyByAngie.aggregates.types.ReservationStateEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    @Enumerated(EnumType.STRING)
    private ReservationStateEnum reservationState;
    @Column(name = "shopping_cart_item_Id", nullable = true)
    private Long shoppingCartItemId;
    @Column(name = "shop_order_line_Id", nullable = true)
    private Long shopOrderId;
    @Column(name = "reservation_total_price", nullable = true)
    private BigDecimal reservationTotalPrice;
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
