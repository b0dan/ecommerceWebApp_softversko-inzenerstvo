package com.fict.eCommerceWebApp.entities;

import com.fict.eCommerceWebApp.entities.audit.Auditable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class UserEntity extends Auditable implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Transient
    private String confirmPassword;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private boolean showUserRealName;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String location;

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double balance;

    @ElementCollection
    private Map<String, Double> balanceHistory;

    @OneToMany(mappedBy = "purchasedBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ListingEntity> purchasedListings;

    public UserEntity() {}

    public UserEntity(String email, String username, String password, String confirmPassword, boolean enabled, String fullName, boolean showUserRealName, String location, double balance, Map<String, Double> balanceHistory) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.enabled = enabled;
        this.fullName = fullName;
        this.showUserRealName = showUserRealName;
        this.location = location;
        this.balance = balance;
        this.balanceHistory = balanceHistory;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(roleModel -> new SimpleGrantedAuthority(roleModel.getName())).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isShowUserRealName() {
        return showUserRealName;
    }
    public void setShowUserRealName(boolean showUserRealName) {
        this.showUserRealName = showUserRealName;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Map<String, Double> getBalanceHistory() {
        return balanceHistory;
    }
    public void setBalanceHistory(Map<String, Double> balanceHistory) {
        this.balanceHistory = balanceHistory;
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
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addRole(RoleEntity roleEntity) {
        roles.add(roleEntity);
    }

    public void addRoles(Set<RoleEntity> roleEntities) {
        roleEntities.forEach(this::addRole);
    }
}