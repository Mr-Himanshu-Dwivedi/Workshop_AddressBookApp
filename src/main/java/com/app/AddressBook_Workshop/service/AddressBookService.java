package com.app.AddressBook_Workshop.service;

import com.app.AddressBook_Workshop.dto.AddressBookDTO;
import com.app.AddressBook_Workshop.model.AddressBookEntry;
import com.app.AddressBook_Workshop.repository.AddressBookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AddressBookService implements IAddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "contacts";

    @Override
    @Cacheable(value = "contacts")
    public List<AddressBookDTO> getAllContacts() {
        // Try fetching from Redis cache first
        List<AddressBookDTO> cachedContacts = (List<AddressBookDTO>) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cachedContacts != null) {
            return cachedContacts;
        }

        // Fetch from database
        List<AddressBookDTO> contacts = addressBookRepository.findAll().stream()
                .map(contact -> modelMapper.map(contact, AddressBookDTO.class))
                .collect(Collectors.toList());

        // Store in Redis Cache (Expire in 10 minutes)
        redisTemplate.opsForValue().set(CACHE_KEY, contacts, 10, TimeUnit.MINUTES);

        return contacts;
    }

    @Override
    public Optional<AddressBookDTO> getContactById(Long id) {
        return addressBookRepository.findById(id)
                .map(contact -> modelMapper.map(contact, AddressBookDTO.class));
    }

    @Override
    @CacheEvict(value = "contacts", allEntries = true)
    public AddressBookDTO addContact(AddressBookDTO contactDTO) {
        AddressBookEntry newContact = modelMapper.map(contactDTO, AddressBookEntry.class);
        AddressBookEntry savedContact = addressBookRepository.save(newContact);

        // Remove Cache so new data can be fetched
        redisTemplate.delete(CACHE_KEY);

        return modelMapper.map(savedContact, AddressBookDTO.class);
    }

    @Override
    @CacheEvict(value = "contacts", allEntries = true)
    public AddressBookDTO updateContact(Long id, AddressBookDTO contactDTO) {
        return addressBookRepository.findById(id)
                .map(existingContact -> {
                    modelMapper.map(contactDTO, existingContact);
                    AddressBookEntry updated = addressBookRepository.save(existingContact);

                    // Remove Cache
                    redisTemplate.delete(CACHE_KEY);

                    return modelMapper.map(updated, AddressBookDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + id));
    }

    @Override
    @CacheEvict(value = "contacts", allEntries = true)
    public void deleteContact(Long id) {
        if (!addressBookRepository.existsById(id)) {
            throw new RuntimeException("Contact not found with ID: " + id);
        }
        addressBookRepository.deleteById(id);
        redisTemplate.delete(CACHE_KEY);
    }
}