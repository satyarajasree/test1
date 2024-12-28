package com.rajasreeit.backend.entities.crmEmployeeEntities;


import com.rajasreeit.backend.entities.CrmEmployee;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RemainderDates {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;

    private String message;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private CrmEmployee crmEmployee;

}
