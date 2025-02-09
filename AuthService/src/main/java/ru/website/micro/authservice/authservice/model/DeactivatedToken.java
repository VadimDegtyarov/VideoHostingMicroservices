package ru.website.micro.authservice.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "deactivated_token")
@NoArgsConstructor
@AllArgsConstructor
public class DeactivatedToken {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private UUID id;
    @Column(name = "keep_until")
    private Date keepUntil;

}
