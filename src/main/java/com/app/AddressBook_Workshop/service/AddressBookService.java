package com.app.AddressBook_Workshop.service;

import com.app.AddressBook_Workshop.model.AddressBookEntry;
import com.app.AddressBook_Workshop.repository.AddressBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressBookService implements IAddressBookService  {
    @Autowired
    private AddressBookRepository addressBookRepository;

    public List<AddressBookEntry> getAllContacts() {
        return addressBookRepository.findAll();
    }
    @Override
    public Optional<AddressBookEntry> getContactById(Long id) {
        return addressBookRepository.findById(id);
    }

    @Override
    public AddressBookEntry addContact(AddressBookEntry contact) {
        return addressBookRepository.save(contact);
    }

    @Override
    public AddressBookEntry updateContact(Long id, AddressBookEntry updatedContact) {
        return addressBookRepository.findById(id)
                .map(existingContact -> {
                    existingContact.setName(updatedContact.getName());
                    existingContact.setEmail(updatedContact.getEmail());
                    existingContact.setPhoneNumber(updatedContact.getPhoneNumber());
                    existingContact.setAddress(updatedContact.getAddress());
                    return addressBookRepository.save(existingContact);
                })
                .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + id));
    }

    @Override
    public void deleteContact(Long id) {
        if (!addressBookRepository.existsById(id)) {
            throw new RuntimeException("Contact not found with ID: " + id);
        }
        addressBookRepository.deleteById(id);
    }
}