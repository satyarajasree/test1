package com.rajasreeit.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrmEmployeeProfileDto {

    private String profileImagePath;

    private String idCardPath;
}
