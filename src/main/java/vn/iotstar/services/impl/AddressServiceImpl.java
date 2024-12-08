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
    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserUserId(userId);
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        return addressRepository.findByUserUserIdAndIsDefaultTrue(userId);
    }

    @Override
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }
    
}
