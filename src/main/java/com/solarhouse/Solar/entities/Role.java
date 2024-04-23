package com.solarhouse.Solar.entities;

import com.solarhouse.Solar.entities.ERole;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private User idUtilisateur;

    public Role() {

    }

    public Role(ERole name) {
        this.name = name;
    }

    // getters and setters
}