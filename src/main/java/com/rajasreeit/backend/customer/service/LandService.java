package com.rajasreeit.backend.customer.service;


import com.rajasreeit.backend.customer.entities.Customer;
import com.rajasreeit.backend.customer.entities.Lands;
import com.rajasreeit.backend.customer.entities.Properties;
import com.rajasreeit.backend.customer.repo.CustomerRepo;
import com.rajasreeit.backend.customer.repo.LandsRepo;
import com.rajasreeit.backend.customer.repo.PropertyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LandService {

    @Autowired
    private LandsRepo landsRepo;

    @Autowired
    private PropertyRepo propertyRepo;

    @Autowired
    private CustomerRepo customerRepo;

    public Lands addLand(Lands lands) {
        // Check if the property_id in lands is null or not set
        if (lands.getProperties() == null || lands.getProperties().getId() == 0) {
            throw new RuntimeException("Property ID must be provided.");
        }

        // Fetch the Properties object by its id from the property_repo
        Properties property = propertyRepo.findById(lands.getProperties().getId())
                .orElseThrow(() -> new RuntimeException("Property with ID " + lands.getProperties().getId() + " not found"));

        // Set the fetched property to the lands entity
        lands.setProperties(property);
        lands.setPassbookNumber(null);

        // Set other properties to null or default values as needed
        lands.setBookingDate(null);
        lands.setAgreement_document(null);
        lands.setTermAndConditionsDocument(null);

        // Save and return the Land entity
        return landsRepo.save(lands);
    }

    private long generatePassbookNumber() {
        // Your logic to generate the passbook number, e.g., find the max passbook number and increment it.
        Long maxPassbookNumber = landsRepo.getMaxPassbookNumber(); // Assuming you have this query
        return (maxPassbookNumber == null ? 1001 : maxPassbookNumber + 1);
    }


    public List<Lands> getAll(){
        return landsRepo.findAll();
    }

    public Optional<Lands> getLandById(int id){
        return landsRepo.findById(id);
    }

}
