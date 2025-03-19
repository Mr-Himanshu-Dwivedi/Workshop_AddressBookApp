package com.app.AddressBook_Workshop.controller;

import com.app.AddressBook_Workshop.dto.AddressBookDTO;
import com.app.AddressBook_Workshop.service.IAddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Address Book API", description = "Manage Address Book Entries")
@RestController
//@PreAuthorize("hasRole('USER')")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully"),
        @ApiResponse(responseCode = "201", description = "Contact created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Contact not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Requires USER role"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
@RequestMapping("/api")
public class AddressBookController {
    @Autowired
    private IAddressBookService addressBookService;

    @Operation(summary = "Retrieve all contacts", description = "Fetches all address book entries")
    @GetMapping("/all")
    public ResponseEntity<List<AddressBookDTO>> getAllContacts() {
        List<AddressBookDTO> contacts = addressBookService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }

    @Operation(summary = "Get contact by ID", description = "Fetch an address book entry by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<AddressBookDTO> getContactById(@PathVariable Long id) {
        return addressBookService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Add a new contact", description = "Creates a new address book entry")
    @PostMapping("/add")
    public ResponseEntity<AddressBookDTO> addContact(@Valid @RequestBody AddressBookDTO contactDTO) {
        AddressBookDTO createdContact = addressBookService.addContact(contactDTO);
        return ResponseEntity.ok(createdContact);
    }

    @Operation(summary = "Update an existing contact", description = "Updates an address book entry using its ID")
    @PutMapping("/update/{id}")
    public ResponseEntity<AddressBookDTO> updateContact(@PathVariable Long id, @Valid @RequestBody AddressBookDTO contactDTO) {
        AddressBookDTO updatedContact = addressBookService.updateContact(id, contactDTO);
        return ResponseEntity.ok(updatedContact);
    }

    @Operation(summary = "Delete a contact", description = "Deletes an address book entry using its ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable Long id) {
        addressBookService.deleteContact(id);
        return ResponseEntity.ok("Contact deleted successfully.");
    }

}