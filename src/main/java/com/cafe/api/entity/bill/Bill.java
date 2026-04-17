package com.cafe.api.entity.bill;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.cafe.api.entity.users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uuid;
    private String name;
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;

    private String paymentMethod;
    private Double total;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User staff;
    
    private String createdBy; // Keep this for legacy/string logs if needed, or we can use staff.getEmail()
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PLACED;

    private String tableNumber;
    private String orderSource; // "WALK_IN" or "SCAN_ORDER"

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean isDeleted = false;

    @JsonManagedReference
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BillItem> items=new ArrayList<>();
}