package net.youssfi.authservice.security.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import net.youssfi.authservice.security.dtos.*;
import net.youssfi.authservice.security.entities.AppRole;
import net.youssfi.authservice.security.entities.AppUser;
import net.youssfi.authservice.security.exceptions.EmailNotFoundException;
import net.youssfi.authservice.security.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@Slf4j
public class SecurityController {
    private AuthService authService;
    private AuthenticationManager authenticationManager;
    private JwtDecoder jwtDecoder;
    @Value("${user.photos.path}")
    private String profilePath;



    public SecurityController(AuthService authService, AuthenticationManager authenticationManager, JwtDecoder jwtDecoder) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtDecoder = jwtDecoder;
    }
    @PostMapping(value = "/public/auth")
    public ResponseEntity<Map<String,String>> authentication(AuthRequestDTO authRequestDTO, HttpServletRequest request){
            String subject=authRequestDTO.username();
            String grantType = authRequestDTO.grantType();
            if (grantType == null)
                return new ResponseEntity<>(Map.of("errorMessage", "grantType is required"), HttpStatus.UNAUTHORIZED);
            if (grantType.equals("password")) {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(subject, authRequestDTO.password())
                );
                subject=authentication.getName();
            } else if (grantType.equals("refreshToken")) {
                Jwt decodedRefreshToken = null;
                decodedRefreshToken = jwtDecoder.decode(authRequestDTO.refreshToken());
                subject = decodedRefreshToken.getClaim("username");

            } else {
                return new ResponseEntity<>(Map.of("errorMessage", String.format("GrantType %s not supported", grantType)), HttpStatus.UNAUTHORIZED);
            }
            Map<String, String> idToken = authService.generateToken(subject, authRequestDTO.withRefreshToken());
            return ResponseEntity.ok(idToken);


    }
    @PutMapping(path = "/user/profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "Bearer Authentication")
    public AppUser updatePhotoProfile(
            @RequestParam MultipartFile photoFile,
            Principal principal,
            HttpServletRequest request) throws IOException {
        return this.authService.updatePhotoProfile(photoFile,principal.getName(),request.getRequestURL().toString());
    }
    @GetMapping(value = "/user/profile/photo", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @SecurityRequirement(name = "Bearer Authentication")
    public byte[] getUserPhoto(Principal principal) throws IOException {
        AppUser appUser=authService.findUserByUserId(principal.getName());
        Path path=Paths.get(profilePath,appUser.getPhotoFileName());
        return Files.readAllBytes(path);
    }

    @GetMapping(value = "/public/profile/photo/{userId}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @SecurityRequirement(name = "Bearer Authentication")
    public byte[] getPublicPhoto(@PathVariable String userId) throws IOException {
        AppUser appUser=authService.findUserByUserId(userId);
        if(appUser.getPhotoFileName()==null){
            ClassPathResource classPathResource = new ClassPathResource("static/images/up.jpeg");
            return Files.readAllBytes(classPathResource.getFile().toPath());
        } else {
            Path path=Paths.get(profilePath,appUser.getPhotoFileName());
            return Files.readAllBytes(path);
        }
    }

    @GetMapping(path = "/user/profile")
    @SecurityRequirement(name = "Bearer Authentication")
    public AppUser getUserProfile(Principal principal)  {
        return this.authService.findUserByUserId(principal.getName());
    }
    @GetMapping("/public/isUsernameAvailable")
    public boolean isUsernameAvailable(String username) {
        return authService.isUsernameAvailable(username);
    }
    @GetMapping("/public/isEmailAvailable")
    public boolean isEmailAvailable(String email) {
        log.info(email);
        return authService.isEmailAvailable(email);
    }
    @PostMapping(path = "/public/register")
    public AppUser register(@RequestBody RegistrationRequestDTO requestDTO){
        return  this.authService.register(requestDTO,false);
    }
    @GetMapping(path = "/public/emailActivation")
    public String emailActivation(String token) {
        return this.authService.emailActivation(token);
    }

    @PostMapping(path="/public/forgotPassword")
    public ResponseEntity<Map<String, String>> forgotPassword(String email){
        try {
            this.authService.sendActivationCode(email);
            return ResponseEntity.ok(Map.of("message","The activation code has been sent to "+email));
        } catch (EmailNotFoundException e) {
            return new ResponseEntity<>(Map.of("errorMessage",e.getMessage()),HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            return new ResponseEntity<>(Map.of("errorMessage","Internal Error"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping(path = "/public/requestForPasswordInit")
    public ResponseEntity<Map<String, String>>  authorizePasswordInitialization(String authorizationCode, String email){
        try {
            this.authService.authorizePasswordInitialization(authorizationCode, email);
            return ResponseEntity.ok(Map.of("message","Your account has been activated successfully"));
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("errorMessage",e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/public/passwordInitialization")
    public void passwordInitialization(PasswordInitializationRequestDTO request){
        this.authService.passwordInitialization(request);
    }
    @GetMapping(path = "/admin/roles")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<AppRole> rolesList(){
        return authService.getAllRoles();
    }
    @PostMapping(path = "/admin/roles")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public AppRole addNewRole(@RequestBody AppRole appRole){
        return authService.addRole(appRole.getRoleName());
    }
    @DeleteMapping(path = "/admin/roles/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void removeRole(@PathVariable Long id){
         authService.deleteRole(id);
    }
    @GetMapping(path = "/admin/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<AppUser> usersList(){
        return authService.getAllUsers();
    }
    @GetMapping(path = "/admin/searchUsers")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<AppUser> searchUsers(String keyWord){
        return authService.searchUsers(keyWord);
    }
    @PostMapping(path = "/admin/addRoleToUser")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public AppRole addRoleToUser(@RequestBody AddRoleToUserDTO request){
         return this.authService.addRoleToUser(request.username(),request.roleName(), request.deleteRequestRole());
    }

    @PostMapping(path = "/admin/removeRoleFromUser")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public AppRole removeRoleFromUser(@RequestBody RemoveRoleFromUserDTO request){
        return this.authService.removeRoleFromUser(request.username(),request.roleName());
    }

    @PostMapping(path = "/admin/requestForRoleAttribution")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public AppRole requestForRoleAttribution(@RequestBody RequestForRoleToUserDTO request){
        return this.authService.requestForRoleToUserAttribution(request.username(),request.roleName());
    }

    @PutMapping(path = "/admin/updateUserDetails")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    public AppUser updateUser(@RequestBody UserDetailsRequestDTO requestDTO){
        return this.authService.updateUserDetails(requestDTO);
    }
    @PutMapping("/admin/changePassword")
    public void changerPassword(@RequestBody ChangePasswordRequestDTO request, Principal principal){
        this.authService.changePassword(request, principal.getName());
    }
    @PutMapping("/admin/verifyEmail")
    public void verifyEmail(Principal principal){
        this.authService.verifyEmail(principal.getName());
    }
    @PutMapping("/admin/activateAccount")
    public AppUser activateAccount(@RequestBody ActivateAccountRequestDTO request){
        return this.authService.activateAccount(request.value(), request.userId());
    }
}
