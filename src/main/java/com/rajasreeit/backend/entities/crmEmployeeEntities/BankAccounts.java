package com.rajasreeit.backend.entities.crmEmployeeEntities;

import com.rajasreeit.backend.entities.CrmEmployee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccounts {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private CrmEmployee crmEmployee;

    private String bankName;

    private String branchName;

    private String accountHolderName;

    private String accountNumber;

    private String ifscCode;
}
