package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.iam.application.IdentityAccessService;
import com.andesheavytech.maquicontrol.platform.iam.domain.RoleName;
import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1")
public class AuthenticationController {
    private final IdentityAccessService identity;
    public AuthenticationController(IdentityAccessService identity) { this.identity=identity; }
    @PostMapping("/authentication/sign-up") @ResponseStatus(HttpStatus.CREATED) public AuthenticationResource signUp(@Valid @RequestBody SignUpRequest request) { return resource(identity.register(new IdentityAccessService.SignUpData(request.email(),request.password(),request.firstName(),request.lastName(),request.phone(),request.address(),request.role()))); }
    @PostMapping("/authentication/sign-in") public AuthenticationResource signIn(@Valid @RequestBody SignInRequest request) { return resource(identity.authenticate(request.email(),request.password())); }
    @GetMapping("/roles") public List<RoleName> roles() { return identity.roles(); }
    private static AuthenticationResource resource(IdentityAccessService.AuthenticationResult result) { return new AuthenticationResource(result.token(),result.accountId(),result.email(),result.roles()); }
    public record SignUpRequest(@NotBlank @Email @Size(max=160) String email,@NotBlank @Size(min=8,max=72) String password,@NotBlank @Size(max=80) String firstName,@NotBlank @Size(max=80) String lastName,@NotBlank @Pattern(regexp="\\+?[0-9]{7,15}") String phone,@NotBlank @Size(max=200) String address,RoleName role) { }
    public record SignInRequest(@NotBlank @Email String email,@NotBlank String password) { }
    public record AuthenticationResource(String token,Long accountId,String email,List<RoleName> roles) { }
}
