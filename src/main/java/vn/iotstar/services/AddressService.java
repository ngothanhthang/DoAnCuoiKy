package vn.iotstar.services;

import java.util.List;

import vn.iotstar.entity.Address;

public interface AddressService {

	List<Address> getAddressesByUserId(Long userId);

	Address getDefaultAddress(Long userId);

	Address saveAddress(Address address);

}
