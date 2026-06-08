package ru.bookingsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.EquipmentMapper;
import ru.bookingsystem.repository.EquipmentRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock EquipmentRepository equipmentRepository;
    @Mock EquipmentMapper equipmentMapper;
    @InjectMocks EquipmentService equipmentService;

    @Captor ArgumentCaptor<Equipment> taskCaptor;

    EquipmentRequestDTO mockEquipmentRequest;
    Equipment equipment;

    @BeforeEach
    void setUp() {
        mockEquipmentRequest = mock(EquipmentRequestDTO.class);
        equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("Name");
        equipment.setCreatedAt(Instant.now());
        equipment.setUpdatedAt(Instant.now());
    }

    @Test
    void getAllEquipments() {
        List<Equipment> mockList = Collections.singletonList(equipment);
        when(equipmentRepository.findAll()).thenReturn(mockList);

        List<Equipment> response = equipmentService.getAllEquipments();

        assertNotNull(response);
        assertEquals(1, response.size());

        verify(equipmentRepository, times(1)).findAll();
    }

    @Test
    void getEquipmentById_Success() {
        when(equipmentRepository.findById(anyLong())).thenReturn(Optional.of(equipment));

        Equipment response = equipmentService.getEquipmentById(anyLong());

        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(equipmentRepository, times(1)).findById(anyLong());
    }

    @Test
    void getEquipmentById_ShouldThrow_EntityNotFoundException() {
        when(equipmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> equipmentService.getEquipmentById(anyLong()));

        verify(equipmentRepository, times(1)).findById(anyLong());
    }

    @Test
    void createEquipment() {
        when(equipmentMapper.toEntity(mockEquipmentRequest)).thenReturn(equipment);
        when(equipmentRepository.save(equipment)).thenReturn(equipment);

        Equipment response = equipmentService.createEquipment(mockEquipmentRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(equipmentMapper, times(1)).toEntity(mockEquipmentRequest);
        verify(equipmentRepository, times(1)).save(equipment);
    }

    @Test
    void updateEquipment_Success() {
        EquipmentRequestDTO equipmentRequest = new EquipmentRequestDTO("newName");
        equipment.setName(equipmentRequest.name());
        when(equipmentMapper.toEntity(equipmentRequest)).thenReturn(equipment);
        when(equipmentRepository.findById(anyLong())).thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(equipment)).thenReturn(equipment);

        equipmentService.updateEquipment(equipmentRequest, anyLong());

        verify(equipmentRepository).save(taskCaptor.capture());
        Equipment equipmentFromCaptor = taskCaptor.getValue();

        assertNotNull(equipmentFromCaptor);
        assertEquals(equipmentRequest.name(), equipmentFromCaptor.getName());
        assertEquals(equipment.getId(), equipmentFromCaptor.getId());

        verify(equipmentMapper, times(1)).toEntity(equipmentRequest);
        verify(equipmentRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateEquipment_ShouldThrow_EntityNotFoundException() {
        when(equipmentMapper.toEntity(mockEquipmentRequest)).thenReturn(equipment);
        when(equipmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> equipmentService.updateEquipment(mockEquipmentRequest, anyLong()));

        verify(equipmentRepository, never()).save(any());
    }

    @Test
    void deleteEquipment_Success() {
        when(equipmentRepository.existsById(anyLong())).thenReturn(true);
        equipmentService.deleteEquipment(anyLong());

        verify(equipmentRepository, times(1)).existsById(anyLong());
        verify(equipmentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteEquipment_ShouldThrow_EntityNotFoundException() {
        when(equipmentRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> equipmentService.deleteEquipment(anyLong()));

        verify(equipmentRepository, times(1)).existsById(anyLong());
        verify(equipmentRepository, never()).deleteById(anyLong());
    }
}