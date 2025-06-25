package com.viettridao.cafe.service;

import java.util.List;

import com.viettridao.cafe.dto.request.equipment.CreateEquipmentRequest;
import com.viettridao.cafe.dto.response.equipment.EquipmentResponse;
import com.viettridao.cafe.model.EquipmentEntity;

public interface EquipmentService {
    List<EquipmentResponse> getAllEquipments();

    EquipmentEntity createEquipment(CreateEquipmentRequest request);
}
