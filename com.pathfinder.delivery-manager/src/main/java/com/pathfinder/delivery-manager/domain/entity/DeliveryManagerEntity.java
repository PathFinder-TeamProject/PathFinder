packagecom.pathfinder.delivery-managers.entity;


import com.sparta.pathfinder.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
@Entity
@Table(name = "p_delivery_manager")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryManagerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
