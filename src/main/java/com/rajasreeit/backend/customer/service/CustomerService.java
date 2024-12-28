package com.rajasreeit.backend.customer.service;


import com.rajasreeit.backend.customer.entities.Customer;
import com.rajasreeit.backend.customer.entities.Employee;
import com.rajasreeit.backend.customer.repo.CustomerRepo;
import com.rajasreeit.backend.customer.repo.EmployeeRepo;
import com.rajasreeit.backend.imagesS3.S3FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;


@Service
public class CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private S3FileUploadService s3FileUploadService;


    public String registerCustomer(
            String customerName,
            String fatherName,
            String dateOfBirth,
            int age,
            String aadharNumber,
            String mobileNumber,
            String email,
            String city,
            int pincode,
            String groupName,
            String panNumber,
            String primaryAddress,
            String nomineeName,
            String occupation,
            String employeeId,
            MultipartFile profileImage) {

        // Check if Aadhaar, mobile, or email already exists
        if (customerRepo.existsByAadharNumber(aadharNumber)) {
            return "Aadhaar number already exists";
        }
        if (customerRepo.existsByMobileNumber(mobileNumber)) {
            return "Mobile number already exists";
        }
        if (customerRepo.existsByEmail(email)) {
            return "Email already exists";
        }

        try {
            Customer customer = new Customer();
            customer.setCustomerName(customerName);
            customer.setFatherName(fatherName);
            customer.setDateOfBirth(LocalDate.parse(dateOfBirth));
            customer.setAge(age);
            customer.setAadharNumber(aadharNumber);
            customer.setMobileNumber(mobileNumber);
            customer.setEmail(email);
            customer.setCity(city);
            customer.setPincode(pincode);
            customer.setGroupName(groupName);
            customer.setPanNumber(panNumber);
            customer.setPrimaryAddress(primaryAddress);
            customer.setNomineeName(nomineeName);
            customer.setOccupation(occupation);

            // Handle profile image upload
            if (profileImage != null && !profileImage.isEmpty()) {
                String profileImagePath = s3FileUploadService.uploadFile(
                        "customer-profiles", profileImage, "profile_" + mobileNumber);
                customer.setProfileImagePath(profileImagePath);
            }

            // Retrieve and set employee reference
            Optional<Employee> employeeOpt = employeeRepo.findByEmployeeReferenceId(employeeId);
            if (employeeOpt.isPresent()) {
                customer.setEmployee(employeeOpt.get());
            } else {
                return "Employee not found";
            }

            customerRepo.save(customer);
            return "Customer registered successfully";

        } catch (IOException e) {
            return "Failed to upload profile image";
        } catch (Exception e) {
            return "Failed to register customer";
        }
    }



    public String updateCustomerProfile(
            int customerId,
            String customerName,
            String email,
            String mobileNumber,
            String dateOfBirth,
            String primaryAddress,
            MultipartFile profileImage,
            String nomineeName) {

        Optional<Customer> customerOpt = customerRepo.findById(customerId);

        if (customerOpt.isEmpty()) {
            return "Customer not found";
        }

        Customer customer = customerOpt.get();


        // Update fields only if non-null values are provided
        if (customerName != null) customer.setCustomerName(customerName);
        if (email != null) customer.setEmail(email);
        if (mobileNumber != null) customer.setMobileNumber(mobileNumber);
        if (dateOfBirth != null) customer.setDateOfBirth(LocalDate.parse(dateOfBirth));
        if (primaryAddress != null) customer.setPrimaryAddress(primaryAddress);
        if (nomineeName != null) customer.setNomineeName(nomineeName);

        try {
            // Handle profile image upload if provided
            if (profileImage != null && !profileImage.isEmpty()) {
                String profileImagePath = s3FileUploadService.uploadFile(
                        "customer-profiles", profileImage, "profile_" + customer.getMobileNumber());
                customer.setProfileImagePath(profileImagePath);
            }
            customerRepo.save(customer);
            return "Customer profile updated successfully";
        } catch (IOException e) {
            return "Failed to update profile image";
        } catch (Exception e) {
            return "Failed to update customer profile";
        }
    }


}
