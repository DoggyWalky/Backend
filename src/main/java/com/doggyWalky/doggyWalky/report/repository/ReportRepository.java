package com.doggyWalky.doggyWalky.report.repository;

import com.doggyWalky.doggyWalky.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {
}
