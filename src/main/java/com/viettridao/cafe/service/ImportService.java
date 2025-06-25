package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.import_request.ImportRequest;
import com.viettridao.cafe.model.ImportEntity;

public interface ImportService {

    ImportEntity createImportEquipment(ImportRequest request, Integer employeeId);
}
