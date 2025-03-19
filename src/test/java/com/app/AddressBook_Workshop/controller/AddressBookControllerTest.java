package com.app.AddressBook_Workshop.controller;

import com.app.AddressBook_Workshop.dto.AddressBookDTO;
import com.app.AddressBook_Workshop.service.IAddressBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressBookControllerTest {

    @InjectMocks
    private AddressBookController addressBookController;

    @Mock
    private IAddressBookService addressBookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllContacts_Success() {
        AddressBookDTO contact = new AddressBookDTO("John Doe", "1234567890", "john@example.com");
        when(addressBookService.getAllContacts()).thenReturn(List.of(contact));

        ResponseEntity<List<AddressBookDTO>> response = addressBookController.getAllContacts();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getName());
        assertEquals("1234567890", response.getBody().get(0).getPhone());
        assertEquals("john@example.com", response.getBody().get(0).getEmail());

        verify(addressBookService, times(1)).getAllContacts();
    }

    @Test
    void getAllContacts_EmptyList() {
        when(addressBookService.getAllContacts()).thenReturn(List.of());

        ResponseEntity<List<AddressBookDTO>> response = addressBookController.getAllContacts();

        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(addressBookService, times(1)).getAllContacts();
    }

    @Test
    void getContactById_Success() {
        AddressBookDTO mockContact = new AddressBookDTO("Alice Brown", "9876543210", "alice@example.com");
        when(addressBookService.getContactById(1L)).thenReturn(Optional.of(mockContact));

        ResponseEntity<AddressBookDTO> response = addressBookController.getContactById(1L);

        assertNotNull(response.getBody());
        assertEquals("Alice Brown", response.getBody().getName());
        assertEquals("9876543210", response.getBody().getPhone());
        assertEquals("alice@example.com", response.getBody().getEmail());

        verify(addressBookService, times(1)).getContactById(1L);
    }

    @Test
    void getContactById_NotFound() {
        when(addressBookService.getContactById(999L)).thenReturn(Optional.empty());

        ResponseEntity<AddressBookDTO> response = addressBookController.getContactById(999L);

        assertNull(response.getBody());
        verify(addressBookService, times(1)).getContactById(999L);
    }

    @Test
    void addContact_Success() {
        AddressBookDTO mockContact = new AddressBookDTO("Bob Smith", "1112223333", "bob@example.com");
        when(addressBookService.addContact(any(AddressBookDTO.class))).thenReturn(mockContact);

        ResponseEntity<AddressBookDTO> response = addressBookController.addContact(mockContact);

        assertNotNull(response.getBody());
        assertEquals("Bob Smith", response.getBody().getName());
        assertEquals("1112223333", response.getBody().getPhone());
        assertEquals("bob@example.com", response.getBody().getEmail());

        verify(addressBookService, times(1)).addContact(any(AddressBookDTO.class));
    }

    @Test
    void updateContact_Success() {
        AddressBookDTO mockContact = new AddressBookDTO("Updated Name", "9998887777", "updated@example.com");
        when(addressBookService.updateContact(eq(1L), any(AddressBookDTO.class))).thenReturn(mockContact);

        ResponseEntity<AddressBookDTO> response = addressBookController.updateContact(1L, mockContact);

        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("9998887777", response.getBody().getPhone());
        assertEquals("updated@example.com", response.getBody().getEmail());

        verify(addressBookService, times(1)).updateContact(eq(1L), any(AddressBookDTO.class));
    }

    @Test
    void updateContact_Failure_NotFound() {
        AddressBookDTO mockContact = new AddressBookDTO("New Name", "1231231234", "new@example.com");
        when(addressBookService.updateContact(eq(999L), any(AddressBookDTO.class)))
                .thenThrow(new RuntimeException("Contact not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            addressBookController.updateContact(999L, mockContact);
        });

        assertEquals("Contact not found", exception.getMessage());
        verify(addressBookService, times(1)).updateContact(eq(999L), any(AddressBookDTO.class));
    }

    @Test
    void deleteContact_Success() {
        doNothing().when(addressBookService).deleteContact(1L);

        ResponseEntity<String> response = addressBookController.deleteContact(1L);

        assertEquals("Contact deleted successfully.", response.getBody());
        verify(addressBookService, times(1)).deleteContact(1L);
    }

    @Test
    void deleteContact_Failure_NotFound() {
        doThrow(new RuntimeException("Contact not found")).when(addressBookService).deleteContact(999L);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            addressBookController.deleteContact(999L);
        });

        assertEquals("Contact not found", exception.getMessage());
        verify(addressBookService, times(1)).deleteContact(999L);
    }
}