package com.example.device.repository;

import com.example.device.model.Device;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceRepository extends ReactiveCrudRepository<Device, Integer>{

}
