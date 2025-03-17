package com.app.AddressBook_Workshop.service;

import com.app.AddressBook_Workshop.model.AddressBookEntry;

import java.util.List;
import java.util.Optional;

public interface IAddressBookService {
    List<AddressBookEntry> getAllContacts();
    Optional<AddressBookEntry> getContactById(Long id);
    AddressBookEntry addContact(AddressBookEntry contact);
    AddressBookEntry updateContact(Long id, AddressBookEntry updatedContact);
    void deleteContact(Long id);
}