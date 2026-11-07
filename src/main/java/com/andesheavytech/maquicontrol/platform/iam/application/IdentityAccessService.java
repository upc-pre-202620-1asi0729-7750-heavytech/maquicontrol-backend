package com.andesheavytech.maquicontrol.platform.iam.application;

import com.andesheavytech.maquicontrol.platform.iam.domain.RoleName;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.*;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.security.JwtService;
import com.andesheavytech.maquicontrol.platform.profiles.domain.ContactInformation;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.ProfileJpaRepository;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.ProfilePersistenceEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service @Transactional
public class IdentityAccessService {
    private final UserAccountJpaRepository accounts; private final RoleJpaRepository roles; private final ProfileJpaRepository profiles; private final PasswordEncoder passwords; private final JwtService jwt;
    public IdentityAccessService(UserAccountJpaRepository accounts, RoleJpaRepository roles, ProfileJpaRepository profiles, PasswordEncoder passwords, JwtService jwt) { this.accounts=accounts;this.roles=roles;this.profiles=profiles;this.passwords=passwords;this.jwt=jwt; }
    public AuthenticationResult register(SignUpData data) {
        var email=email(data.email()); if(accounts.existsByEmailIgnoreCase(email)) throw conflict("An account with this email already exists");
        if(data.password()==null || data.password().length()<8) throw bad("password must contain at least 8 characters");
        var contact=new ContactInformation(data.phone(),data.address()); var role=role(data.role()==null?RoleName.CONTRACTOR:data.role());
        var account=accounts.save(new UserAccountPersistenceEntity(email,passwords.encode(data.password()),role));
        profiles.save(new ProfilePersistenceEntity(account.getId(),required(data.firstName(),"firstName"),required(data.lastName(),"lastName"),contact.phone(),contact.address()));
        return authenticated(account);
    }
    @Transactional(readOnly=true) public AuthenticationResult authenticate(String email, String password) {
        var account=accounts.findByEmailIgnoreCase(email(email)).orElseThrow(() -> unauthorized("Invalid email or password"));
        if(!account.isActive() || !passwords.matches(password==null?"":password,account.getPasswordHash())) throw unauthorized("Invalid email or password");
        return authenticated(account);
    }
    @Transactional(readOnly=true) public List<RoleName> roles() { return Arrays.asList(RoleName.values()); }
    @Transactional(readOnly=true) public UserAccountPersistenceEntity account(Long id) { return accounts.findById(id).orElseThrow(() -> notFound("User account not found")); }
    private AuthenticationResult authenticated(UserAccountPersistenceEntity account) { return new AuthenticationResult(jwt.issue(account),account.getId(),account.getEmail(),account.getRoles().stream().map(r->r.getName()).sorted().toList()); }
    private RolePersistenceEntity role(RoleName name) { return roles.findByName(name).orElseGet(() -> roles.save(new RolePersistenceEntity(name))); }
    private static String email(String value) { if(value==null || !value.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw bad("email is invalid"); return value.trim().toLowerCase(Locale.ROOT); }
    private static String required(String value,String field) { if(value==null||value.isBlank()) throw bad(field+" is required"); return value.trim(); }
    private static ResponseStatusException bad(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST,message); }
    private static ResponseStatusException conflict(String message) { return new ResponseStatusException(HttpStatus.CONFLICT,message); }
    private static ResponseStatusException unauthorized(String message) { return new ResponseStatusException(HttpStatus.UNAUTHORIZED,message); }
    private static ResponseStatusException notFound(String message) { return new ResponseStatusException(HttpStatus.NOT_FOUND,message); }
    public record SignUpData(String email,String password,String firstName,String lastName,String phone,String address,RoleName role) { }
    public record AuthenticationResult(String token,Long accountId,String email,List<RoleName> roles) { }
}
