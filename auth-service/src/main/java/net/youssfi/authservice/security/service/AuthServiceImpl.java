package net.youssfi.authservice.security.service;
import lombok.extern.slf4j.Slf4j;
import net.youssfi.authservice.security.config.JwtTokenParams;
import net.youssfi.authservice.security.dtos.ChangePasswordRequestDTO;
import net.youssfi.authservice.security.dtos.PasswordInitializationRequestDTO;
import net.youssfi.authservice.security.dtos.RegistrationRequestDTO;
import net.youssfi.authservice.security.dtos.UserDetailsRequestDTO;
import net.youssfi.authservice.security.entities.AppRole;
import net.youssfi.authservice.security.entities.AppUser;
import net.youssfi.authservice.security.enums.AccountStatus;
import net.youssfi.authservice.security.exceptions.EmailNotFoundException;
import net.youssfi.authservice.security.repo.RoleRepository;
import net.youssfi.authservice.security.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;

    private JwtTokenParams jwtTokenParams;
    private MailService mailService;
    @Value("${jwt.issuer}")
    private String issuer;

    private StreamBridge streamBridge;
    @Value("${user.kafka.topic}")
    private String userTopic;
    @Value("${user.photos.path}")
    private String profilePath;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, JwtTokenParams jwtTokenParams, MailService mailService, StreamBridge streamBridge) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.jwtTokenParams = jwtTokenParams;
        this.mailService = mailService;
        this.streamBridge = streamBridge;
    }

    @Override
    public AppUser addUser(String username, String email, String password, String repassword, boolean emailVerified, AccountStatus status) {
        AppUser appUser=userRepository.findByUsername(username);
        if(appUser!=null) throw new RuntimeException(String.format("Username %s already exit",username));
        if(!password.equals(repassword)) throw  new RuntimeException("Passwords not match");
        appUser=AppUser.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .email(email)
                .emailVerified(emailVerified)
                .status(status)
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(appUser);
    }
    @Override
    public AppUser updateUserDetails(UserDetailsRequestDTO request){
        AppUser appUser=userRepository.findById(request.userId()).orElse(null);
        log.info("===========");
        log.info(request.userId());
        log.info(request.firstName());
        log.info(request.lastName());
        log.info(request.email());
        log.info("===========");
        if(appUser==null) throw new RuntimeException("User not found");
        if(request.firstName()!=null) appUser.setFirstName(request.firstName());
        if(request.lastName()!=null) appUser.setLastName(request.lastName());
        if(request.email()!=null) {
            appUser.setEmail(request.email());
            appUser.setEmailVerified(false);
        }
        userRepository.save(appUser);
        streamBridge.send(userTopic,request);
        return appUser;
    }

    @Override
    public AppRole addRole(String roleName) {
        AppRole appRole=roleRepository.findByRoleName(roleName);
        if(appRole!=null) throw new RuntimeException(String.format("Role %s already exit",roleName));
        appRole=AppRole.builder().roleName(roleName).build();
        return roleRepository.save(appRole);
    }

    @Override
    public AppRole addRoleToUser(String username, String roleName, boolean deleteRequestRole) {
        AppUser appUser=userRepository.findByUsername(username);
        if(appUser==null) throw new RuntimeException(String.format("This username %s do not exist",username));
        AppRole appRole=roleRepository.findByRoleName(roleName);
        if(appRole==null) throw new RuntimeException(String.format("This Role %s do not exist",roleName));
        if(appUser.getAppRoles()==null) appUser.setAppRoles(new ArrayList<>());
        appUser.getAppRoles().add(appRole);
        if(deleteRequestRole) appUser.getRequestedRoles().remove(roleName);
        return appRole;
    }

    @Override
    public AppRole removeRoleFromUser(String username, String roleName) {
        AppUser appUser=userRepository.findByUsername(username);
        if(appUser==null) throw new RuntimeException(String.format("This username %s do not exist",username));
        AppRole appRole=roleRepository.findByRoleName(roleName);
        if(appRole==null) throw new RuntimeException(String.format("This Role %s do not exist",roleName));
        appUser.getAppRoles().remove(appRole);
        return appRole;
    }

    @Override
    public AppRole requestForRoleToUserAttribution(String username, String roleName) {
        AppUser appUser=userRepository.findByUsername(username);
        if(appUser==null) throw new RuntimeException(String.format("This username %s do not exist",username));
        AppRole appRole=roleRepository.findByRoleName(roleName);
        if(appRole==null) throw new RuntimeException(String.format("This Role %s do not exist",roleName));
        if(appUser.getAppRoles()==null) appUser.setAppRoles(new ArrayList<>());
        appUser.getRequestedRoles().add(roleName);
        return appRole;
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO request, String userId) {
        AppUser appUser=userRepository.findById(userId).get();
        if (!passwordEncoder.matches(request.currentPassword(),appUser.getPassword()))
            throw new RuntimeException("The current password is incorrect");
        if(!request.newPassword().equals(request.confirmPassword())){
            throw new RuntimeException("Confirmed password not match");
        }
        appUser.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(appUser);
    }

    @Override
    public AppUser findUserByUsername(String username) {
        AppUser appUser=userRepository.findByUsername(username);
        if(appUser==null) throw new RuntimeException(String.format("This username %s do not exist",username));
        return appUser;
    }
    @Override
    public AppUser findUserByUsernameOrEmail(String usernameOrEmail) {
        System.out.println(usernameOrEmail);
        AppUser appUser=userRepository.findByUsernameOrEmail(usernameOrEmail,usernameOrEmail);
        if(appUser==null) throw new RuntimeException("Bad Credentials");
        return appUser;
    }

    @Override
    public boolean isUsernameAvailable(String username){
        AppUser appUser=userRepository.findByUsername(username);
        return appUser==null;
    }
    @Override
    public boolean isEmailAvailable(String email){
        AppUser appUser=userRepository.findByEmail(email);
        return appUser==null;
    }

    @Override
    public Map<String,String> generateToken(String username, boolean generateRefreshToken){
        AppUser appUser=findUserByUsernameOrEmail(username);
        String scope=appUser.getAppRoles().stream().map(r->r.getRoleName()).collect(Collectors.joining(" "));
        Map<String,String> idToken=new HashMap<>();
        Instant instant=Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(appUser.getId())
                .issuedAt(instant)
                .expiresAt(instant.plus(generateRefreshToken?jwtTokenParams.shirtAccessTokenTimeout():jwtTokenParams.longAccessTokenTimeout(), ChronoUnit.MINUTES))
                .issuer(issuer)
                .claim("scope",scope)
                .claim("email",appUser.getEmail())
                .claim("firstName",appUser.getFirstName())
                .claim("lastName",appUser.getLastName())
                .claim("username",appUser.getUsername())
                .build();
        String jwtAccessToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        idToken.put("access-token",jwtAccessToken);
        if(generateRefreshToken){
            JwtClaimsSet jwtRefreshClaimsSet = JwtClaimsSet.builder()
                    .subject(appUser.getId())
                    .issuedAt(instant)
                    .expiresAt(instant.plus(jwtTokenParams.refreshTokenTimeout(), ChronoUnit.MINUTES))
                    .issuer(issuer)
                    .claim("username",appUser.getUsername())
                    .claim("email",appUser.getEmail())
                    .build();
            String jwtRefreshTokenToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtRefreshClaimsSet)).getTokenValue();
            idToken.put("refresh-token",jwtRefreshTokenToken);
        }
        return idToken;
    }

    @Override
    public AppUser findUserByUserId(String userId) {
        AppUser appUser=userRepository.findById(userId).orElse(null);
        if(appUser==null) throw new RuntimeException(String.format("This username %s do not exist",userId));
        return appUser;
    }
    @Override
    public AppUser updatePhotoProfile(MultipartFile photoFile, String userId, String baseURL) throws IOException {
        AppUser appUser=findUserByUserId(userId);
        String photoId= UUID.randomUUID().toString();
        String photoName=userId+"_"+photoId+"_"+photoFile.getOriginalFilename();
        Path photosDirectory= Paths.get(profilePath);
        if(!Files.exists(photosDirectory)){
            Files.createDirectory(photosDirectory);
        }
        Files.write(Path.of(profilePath,photoName),photoFile.getBytes());
        appUser.setPhotoFileName(photoName);
        appUser.setPhotoURL(baseURL+"/photo");
        return userRepository.save(appUser);
    }

    @Override
    public AppUser register(RegistrationRequestDTO requestDTO, boolean activate) {
        AppUser appUser=userRepository.findByUsername(requestDTO.username());
        if(appUser!=null) throw new RuntimeException("This username is not available");
        if(!requestDTO.password().equals(requestDTO.confirmPassword()))
            throw new RuntimeException("Passwords not match");
        appUser=AppUser.builder()
                .username(requestDTO.username())
                .password(passwordEncoder.encode(requestDTO.password()))
                .firstName(requestDTO.firstName())
                .lastName(requestDTO.lastName())
                .id(UUID.randomUUID().toString())
                .email(requestDTO.email())
                .gender(requestDTO.gender())
                .status(AccountStatus.CREATED)
                .status(activate?AccountStatus.ACTIVATED:AccountStatus.CREATED)
                .build();
        AppUser savedAppUser = userRepository.save(appUser);
        addRoleToUser(requestDTO.username(),"USER", false);
        verifyEmail(savedAppUser.getId());
        return appUser;
    }
    @Override
    public void verifyEmail(String userId){
       AppUser appUser=userRepository.findById(userId).get();
        Instant instant=Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(userId)
                .issuedAt(instant)
                .expiresAt(instant.plus(5, ChronoUnit.MINUTES))
                .issuer(issuer)
                .claim("email",appUser.getEmail())
                .build();
        String activationJwtToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        String emailContent=String.format("To activate yous account click this link : http://localhost:8888/auth-service/public/emailActivation?token="+activationJwtToken);
        mailService.sendEmail(appUser.getEmail(),"Email verification",emailContent);
    }

    @Override
    public String emailActivation(String token) {
        try {
            Jwt decode = jwtDecoder.decode(token);
            String subject = decode.getSubject();
            AppUser appUser=userRepository.findById(subject).get();
            appUser.setEmailVerified(true);
            appUser.setStatus(AccountStatus.ACTIVATED);
            userRepository.save(appUser);
            return "Email verification success";
        } catch (JwtException e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public void sendActivationCode(String email) {
        AppUser appUser=userRepository.findByEmail(email);
        if(appUser==null) throw new EmailNotFoundException("This email is not associated with any account");
        Random random=new Random();
        String activationCode="";
        for (int i = 0; i <4 ; i++) {
            activationCode+=random.nextInt(9);
        }
        appUser.setTemporaryActivationCode(activationCode);
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        appUser.setTemporaryActivationCodeTimeStamp(now);
        userRepository.save(appUser);
        mailService.sendEmail(email,"Password Initialization",activationCode);

    }

    @Override
    public void authorizePasswordInitialization(String authorizationCode, String email) {
        AppUser appUser=userRepository.findByEmail(email);
        if(appUser==null) throw new RuntimeException("This email is not associated with any account");
        if(!appUser.getTemporaryActivationCode().equals(authorizationCode))
            throw new RuntimeException("Incorrect authorization code");
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant lastInstant=appUser.getTemporaryActivationCodeTimeStamp();
        Instant lastInstantPlus5=lastInstant.plus(5,ChronoUnit.MINUTES);
        if(!now.isBefore(lastInstantPlus5))
            throw new RuntimeException("This authorization code has been expired");
        appUser.setTemporaryActivationCodeTimeStamp(now);
        userRepository.save(appUser);
    }

    @Override
    public void passwordInitialization(PasswordInitializationRequestDTO request) {
        if(!request.password().equals(request.confirmPassword()))
            throw new RuntimeException("Passwords not match");
        AppUser appUser=userRepository.findByEmail(request.email());
        if(appUser==null) throw new RuntimeException("This email is not associated with any account");
        if(!appUser.getTemporaryActivationCode().equals(request.authorizationCode()))
            throw new RuntimeException("Incorrect authorization code");
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant lastInstant=appUser.getTemporaryActivationCodeTimeStamp();
        Instant lastInstantPlus5=lastInstant.plus(5,ChronoUnit.MINUTES);
        if(!now.isBefore(lastInstantPlus5))
            throw new RuntimeException("This authorization code has been expired");
        appUser.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(appUser);
    }

    @Override
    public List<AppRole> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<AppUser> searchUsers(String keyWord) {
        return userRepository.findByUsernameContains(keyWord);
    }

    @Override
    public void deleteRole(Long id) {
        this.roleRepository.deleteById(id);
    }

    @Override
    public AppUser activateAccount(boolean value, String userId) {
        AccountStatus status=value?AccountStatus.ACTIVATED:AccountStatus.DEACTIVATED;
        AppUser appUser=userRepository.findById(userId).get();
        appUser.setStatus(status);
        return userRepository.save(appUser);
    }
}
