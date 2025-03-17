package com.app.AddressBook_Workshop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address_book")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AddressBookEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phoneNumber;
    private String address;
}