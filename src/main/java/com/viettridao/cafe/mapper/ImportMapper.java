package com.viettridao.cafe.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.viettridao.cafe.dto.request.import_request.ImportRequest;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.ImportEntity;
import com.viettridao.cafe.model.ProductEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImportMapper {
    private final ModelMapper modelMapper;

    // public ImportEntity converToEntity(ImportRequest request, EmployeeEntity employee,
    //         ProductEntity product) {
    //     ImportEntity importEntity = modelMapper.map(request, ImportEntity.class);

    //     importEntity.setImportDate(request.getImportDate());
    //     importEntity.setQuantity(request.getQuantity());
    //     importEntity.setIsDeleted(request.getIsDeleted());
    //     importEntity.setEmployee(employee);
    //     importEntity.setProduct(product); // Assuming product is not used in this context
    //     return importEntity;
    // }
}
