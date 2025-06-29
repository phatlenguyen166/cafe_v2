package com.viettridao.cafe.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import com.viettridao.cafe.dto.response.table.TableResponse;
import com.viettridao.cafe.mapper.TableMapper;
import com.viettridao.cafe.repository.TableRepository;
import com.viettridao.cafe.service.TableSerivce;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TableSerivceImpl implements TableSerivce {

    private final TableRepository tableRepository;
    private final TableMapper tableMapper;

    @Override
    public List<TableResponse> getAllTables() {
        return tableRepository.findAllByIsDeletedFalse()
                .stream()
                .map(tableMapper::convertToDto)
                .toList();
    }

}
