package com.example.bankcards.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Table(name = "user_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique=true)
    @NotNull
    @NotEmpty
    private String login;
    @NotNull
    @NotEmpty
    @Length(min = 4,message = "Длина пароля должна быть более 4 символов")
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

}


