package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Address;
import vn.iotstar.repository.AddressRepository;
import vn.iotstar.services.AddressService;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> getAddressesByUserId(String userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public Address getDefaultAddress(String userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId);
    }

    @Override
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }
    
    @Override
    public void deleteAddress(String id) {
        addressRepository.deleteById(id);
    }
    
    @Override
    public Address getDefaultAddressByUserId(String userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId);
    }
    
    @Override
    public Address getAddressById(String id) {
        return addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
    }
    
}
