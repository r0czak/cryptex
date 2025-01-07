package org.atonic.cryptexsimple.model.entity.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "auth0UserId"),
        @UniqueConstraint(columnNames = "email")
    })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String auth0UserId;

    private String email;
    private boolean deleted;
}
