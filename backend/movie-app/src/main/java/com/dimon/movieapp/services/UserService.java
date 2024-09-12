package com.dimon.movieapp.services;

import com.dimon.movieapp.dto.LoginBody;
import com.dimon.movieapp.dto.PasswordResetBody;
import com.dimon.movieapp.dto.RegistrationBody;
import com.dimon.movieapp.exceptions.EmailFailureException;
import com.dimon.movieapp.exceptions.EmailNotFoundException;
import com.dimon.movieapp.exceptions.UserAlreadyExistsException;
import com.dimon.movieapp.exceptions.UserNotVerifiedException;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.models.VerificationToken;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.repositories.VerificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private LocalUserRepository userRepository;
    private VerificationRepository verificationRepository;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;

    @Autowired
    public UserService(LocalUserRepository userRepository, VerificationRepository verificationRepository, EncryptionService encryptionService, JWTService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
        if (userRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || userRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setUsername(registrationBody.getUsername());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        return userRepository.save(user);
    }

    private VerificationToken createVerificationToken(LocalUser user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public boolean checkAuthentication(String token) {
        String username = jwtService.getUsername(token);
        Optional<LocalUser> opUser = userRepository.findByUsernameIgnoreCase(username);
        if(opUser.isPresent()) {
            return opUser.get().isEmailVerified();
        }
        return false;
    }


    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = userRepository.findByEmailIgnoreCase(loginBody.getEmail());
        if(opUser.isPresent()) {
            LocalUser user = opUser.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                if(user.isEmailVerified()) {
                    return jwtService.generateJWT(user);
                } else {
                    List<VerificationToken> verificationTokenList = user.getVerificationTokens();
                    boolean resend = verificationTokenList.size() == 0 ||
                            verificationTokenList.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));

                    if(resend) {
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationRepository.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token) {
        Optional<VerificationToken> opToken = verificationRepository.findByToken(token);
        if(opToken.isPresent()) {
            System.out.println("Present");
            VerificationToken verificationToken = opToken.get();
            System.out.println(verificationToken);
            LocalUser user = verificationToken.getUser();
            System.out.println(user);
            if(!user.isEmailVerified()) {
                user.setEmailVerified(true);
                userRepository.save(user);
                verificationRepository.deleteByUser(user);
                return true;
            }
        }
        return false;
    }

    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
        Optional<LocalUser> opUser = userRepository.findByEmailIgnoreCase(email);
        if(opUser.isPresent()) {
            LocalUser user = opUser.get();
            String token = jwtService.generatePasswordResetJWT(user);
            emailService.sendPasswordResetEmail(user, token);
        } else {
            throw new EmailNotFoundException();
        }
    }

    public void resetPassword(PasswordResetBody body) {
        String email = jwtService.getResetPasswordEmail(body.getToken());
        Optional<LocalUser> opUser = userRepository.findByEmailIgnoreCase(email);
        if(opUser.isPresent()) {
            LocalUser user = opUser.get();
            user.setPassword(encryptionService.encryptPassword(body.getPassword()));
            userRepository.save(user);
        }
    }

    public boolean userHasPermissionToUser(LocalUser user, Long id) {
        return user.getId() == id;
    }

}
