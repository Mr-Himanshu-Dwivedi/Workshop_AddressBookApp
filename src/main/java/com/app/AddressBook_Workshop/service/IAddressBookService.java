package com.app.AddressBook_Workshop.service;

import com.app.AddressBook_Workshop.dto.AddressBookDTO;

import java.util.List;
import java.util.Optional;

public interface IAddressBookService {
    List<AddressBookDTO> getAllContacts();
    Optional<AddressBookDTO> getContactById(Long id);
    AddressBookDTO addContact(AddressBookDTO contactDTO);
    AddressBookDTO updateContact(Long id, AddressBookDTO contactDTO);
    void deleteContact(Long id);
}