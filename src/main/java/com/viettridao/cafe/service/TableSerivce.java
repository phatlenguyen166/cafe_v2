package com.viettridao.cafe.service;

import java.util.List;

import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.table.SwitchTableRequest;
import com.viettridao.cafe.dto.response.table.TableResponse;

public interface TableSerivce {
    List<TableResponse> getAllTables();

    TableResponse switchTable(SwitchTableRequest request);

    List<TableResponse> getTableByStatus(TableStatus status);
}
