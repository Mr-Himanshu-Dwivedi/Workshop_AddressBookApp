package com.app.AddressBook_Workshop.controller;

import com.app.AddressBook_Workshop.dto.AddressBookDTO;
import com.app.AddressBook_Workshop.model.AddressBookEntry;
import com.app.AddressBook_Workshop.service.IAddressBookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressBookController {
    @Autowired
    private IAddressBookService addressBookService;

    @GetMapping("/all")
    public ResponseEntity<List<AddressBookDTO>> getAllContacts() {
        List<AddressBookDTO> contacts = addressBookService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AddressBookDTO> getContactById(@PathVariable Long id) {
        return addressBookService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/new")
    public ResponseEntity<AddressBookDTO> addContact(@Valid @RequestBody AddressBookDTO contactDTO) {
        AddressBookDTO savedContact = addressBookService.addContact(contactDTO);
        return ResponseEntity.ok(savedContact);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<AddressBookDTO> updateContact(@PathVariable Long id,@Valid @RequestBody AddressBookDTO updatedContact) {
        return ResponseEntity.ok(addressBookService.updateContact(id, updatedContact));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        addressBookService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}