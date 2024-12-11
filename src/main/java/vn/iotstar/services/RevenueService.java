package vn.iotstar.services;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenueService 
{
	List<Object[]> getRevenueByMonth(int year);
	
	List<Object[]> getRevenueByQuarter(int year);
	
	List<Object[]> getRevenueByYear();
	
	List<Object[]> getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
