package vn.iotstar.services;

import java.util.List;

import vn.iotstar.entity.Address;

public interface AddressService {

	List<Address> getAddressesByUserId(String userId);

	Address getDefaultAddress(String userId);

	Address saveAddress(Address address);

	Address getDefaultAddressByUserId(String userId);

	void deleteAddress(String id);

	Address getAddressById(String id);

}
