package com.viettridao.cafe.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viettridao.cafe.dto.response.report.ReportResponse;
import com.viettridao.cafe.model.InvoiceEntity;

@Repository
public interface ReportRepository extends JpaRepository<InvoiceEntity, Integer> {

    @Query(value = """
            SELECT
                d.Ngay AS dateReport,
                IFNULL((
                    SELECT SUM(total_amount)
                    FROM invoices
                    WHERE status = 'PAID' AND DATE(created_at) = d.Ngay
                ), 0) AS revenue,
                (
                    IFNULL((SELECT SUM(total_amount) FROM imports WHERE import_date = d.Ngay), 0) +
                    IFNULL((SELECT SUM(amount) FROM expenses WHERE expense_date = d.Ngay), 0) +
                    IFNULL((SELECT SUM(purchase_price) FROM equipment WHERE purchase_date = d.Ngay), 0)
                ) AS expense
            FROM (
                SELECT DATE(created_at) AS Ngay FROM invoices WHERE status = 'PAID'
                UNION ALL
                SELECT expense_date FROM expenses
                UNION ALL
                SELECT import_date FROM imports
                UNION ALL
                SELECT purchase_date FROM equipment
            ) AS d
            WHERE d.Ngay BETWEEN :startDate AND :endDate
            ORDER BY d.Ngay;
            """, nativeQuery = true)
    List<ReportResponse> getRevenueExpenseBetween(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
