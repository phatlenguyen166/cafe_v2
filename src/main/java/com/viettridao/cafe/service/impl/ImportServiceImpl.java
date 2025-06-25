package com.viettridao.cafe.service.impl;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.viettridao.cafe.dto.request.import_request.ImportRequest;
import com.viettridao.cafe.mapper.ImportMapper;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.EquipmentEntity;
import com.viettridao.cafe.model.ImportEntity;
import com.viettridao.cafe.repository.EmployeeRepository;
import com.viettridao.cafe.repository.EquipmentRepository;
import com.viettridao.cafe.repository.ImportRepository;
import com.viettridao.cafe.service.ImportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final EmployeeRepository employeeRepository;
    private final EquipmentRepository equipmentRepository;
    private final ImportRepository importRepository;
    private final ImportMapper importMapper;

    @Override
    public ImportEntity createImportEquipment(ImportRequest request, Integer employeeId) {

        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        ImportEntity importEntity = importMapper.converToEntity(request, employee, null);

        return importRepository.save(importEntity);
    }

}
