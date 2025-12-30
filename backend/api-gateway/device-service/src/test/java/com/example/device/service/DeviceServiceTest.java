package com.example.device.service;

import com.example.device.repository.DeviceRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {
    @Mock
    private DeviceRepository deviceRepo;

    @InjectMocks
    private DeviceServiceImpl deviceService;



}