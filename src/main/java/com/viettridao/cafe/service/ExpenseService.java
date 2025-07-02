package com.viettridao.cafe.service;

import java.util.List;

import com.viettridao.cafe.dto.request.expense.ExpenseRequest;
import com.viettridao.cafe.dto.response.expense.ExpenseResponse;
import com.viettridao.cafe.model.ExpenseEntity;

public interface ExpenseService {

    List<ExpenseResponse> getAllExpenses();

    ExpenseResponse createExpense(ExpenseRequest request, Integer accountId);

    ExpenseEntity save(ExpenseEntity expenseEntity);
}
