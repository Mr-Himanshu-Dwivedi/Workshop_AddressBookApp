package com.app.AddressBook_Workshop.controller;

import com.app.AddressBook_Workshop.model.AddressBookEntry;
import com.app.AddressBook_Workshop.service.IAddressBookService;
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
    public ResponseEntity<List<AddressBookEntry>> getAllContacts() {
        List<AddressBookEntry> contacts = addressBookService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressBookEntry> getContactById(@PathVariable Long id) {
        return addressBookService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("new")
    public ResponseEntity<AddressBookEntry> addContact(@RequestBody AddressBookEntry contact) {
        AddressBookEntry savedContact = addressBookService.addContact(contact);
        return ResponseEntity.ok(savedContact);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<AddressBookEntry> updateContact(@PathVariable Long id, @RequestBody AddressBookEntry updatedContact) {
        AddressBookEntry contact = addressBookService.updateContact(id, updatedContact);
        return ResponseEntity.ok(contact);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        addressBookService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}