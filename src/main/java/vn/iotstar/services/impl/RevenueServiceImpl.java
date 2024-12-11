package vn.iotstar.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.repository.OrderRepository;
import vn.iotstar.services.RevenueService;
@Service
public class RevenueServiceImpl  implements RevenueService{

	 	@Autowired
	    private OrderRepository orderRepository;

	   
		@Override
		public List<Object[]> getRevenueByYear() {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public List<Object[]> getRevenueByMonth(int year) {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public List<Object[]> getRevenueByQuarter(int year) {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public List<Object[]> getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
			// TODO Auto-generated method stub
			return null;
		}
}
