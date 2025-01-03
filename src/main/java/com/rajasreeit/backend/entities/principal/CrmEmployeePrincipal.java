package com.rajasreeit.backend.entities.principal;

import com.rajasreeit.backend.entities.CrmAdmin;
import com.rajasreeit.backend.entities.CrmEmployee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CrmEmployeePrincipal implements UserDetails {

    private CrmEmployee crmEmployee;

    public CrmEmployeePrincipal(CrmEmployee crmEmployee){
        this.crmEmployee = crmEmployee;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return crmEmployee.getMobile();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
