package com.dimon.movieapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "verification_token")
@Getter
@Setter
public class VerificationToken {

    /** The unique id for the record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    /** The token that was sent to the user. */
    @Column(name = "token", nullable = false, unique = true)
    private String token;
    /** The timestamp of when the token was created. */
    @Column(name = "created_timestamp", nullable = false)
    private Timestamp createdTimestamp;
    /** The user this verification token is for. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

}
