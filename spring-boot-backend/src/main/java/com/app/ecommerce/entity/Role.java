package com.app.ecommerce.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
