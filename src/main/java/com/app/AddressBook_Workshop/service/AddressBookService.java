package com.app.AddressBook_Workshop.service;

import com.app.AddressBook_Workshop.model.AddressBookEntry;
import com.app.AddressBook_Workshop.repository.AddressBookRepository;
import com.app.AddressBook_Workshop.dto.AddressBookDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressBookService implements IAddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<AddressBookDTO> getAllContacts() {
        return addressBookRepository.findAll().stream()
                .map(contact -> modelMapper.map(contact, AddressBookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AddressBookDTO> getContactById(Long id) {
        return addressBookRepository.findById(id)
                .map(contact -> modelMapper.map(contact, AddressBookDTO.class));
    }

    @Override
    public AddressBookDTO addContact(AddressBookDTO contact) {
        AddressBookEntry newContact = modelMapper.map(contact, AddressBookEntry.class);
        AddressBookEntry savedContact = addressBookRepository.save(newContact);
        return modelMapper.map(savedContact, AddressBookDTO.class);
    }

    @Override
    public AddressBookDTO updateContact(Long id, AddressBookDTO contactDTO) {
        return addressBookRepository.findById(id)
                .map(existingContact -> {
                    modelMapper.map(contactDTO, existingContact);
                    AddressBookEntry updated = addressBookRepository.save(existingContact);
                    return modelMapper.map(updated, AddressBookDTO.class);
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