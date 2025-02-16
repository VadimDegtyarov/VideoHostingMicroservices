package ru.website.micro.authservice.userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.website.micro.authservice.userservice.Exception.ResourceNotFoundException;
import ru.website.micro.authservice.userservice.dto.AddInfoUserDTO;
import ru.website.micro.authservice.userservice.model.User;
import ru.website.micro.authservice.userservice.model.UserAuthInfo;
import ru.website.micro.authservice.userservice.repository.UserAuthInfoRepository;
import ru.website.micro.authservice.userservice.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserAuthInfoRepository userAuthInfoRepository;

    private User getUserByLogin(String login) {
        if (login.contains("@")) {
            return userAuthInfoRepository.findByEmail(login)
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь с почтой: %s не найден".formatted(login)))
                    .getUser();
        } else if (login.contains("+")) {
            return userAuthInfoRepository.findByPhoneNumber(login)
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь с номером телефона: %s не найден".formatted(login)))
                    .getUser();
        } else {
            return userRepository.findByUsername(login)
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ником: %s не найден".formatted(login)));
        }
    }

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        Optional<UserAuthInfo> userAuth = Optional.ofNullable(userAuthInfoRepository.findByEmail(email).
                orElseThrow(() ->
                        new ResourceNotFoundException("User with email %s not found".formatted(email))));
        return userAuth.get().getUser();
    }

    public User getUserByUsername(String username) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username).
                orElseThrow(() ->
                        new ResourceNotFoundException("User with username: %s not found".formatted(username))));
        return user.get();
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        Optional<UserAuthInfo> userAuth = Optional.ofNullable(userAuthInfoRepository.findByPhoneNumber(phoneNumber).
                orElseThrow(() ->
                        new ResourceNotFoundException("User with phone: %s not found".formatted(phoneNumber))));
        return userAuth.get().getUser();
    }

    public HttpStatus createUser(User user) {
        try {
            userRepository.save(user);
            return HttpStatus.CREATED;
        } catch (DataAccessException ex) {
            log.error("Ошибка при сохранении пользователя: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
    public User getUserById(UUID id)
    {
        return userRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("User with id %s not found".formatted(id)));
    }
    @Transactional
    public ResponseEntity<Void> updateUser(UUID id,AddInfoUserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пользователь с данным id:%s не найден".formatted(id)));

        Optional.ofNullable(userDTO.getUsername()).filter(StringUtils::hasText).ifPresent(user::setUsername);
        Optional.ofNullable(userDTO.getBirthDate()).ifPresent(user::setBirthDate);
        Optional.ofNullable(userDTO.getFirstName()).filter(StringUtils::hasText).ifPresent(user::setFirstName);
        Optional.ofNullable(userDTO.getLastName()).filter(StringUtils::hasText).ifPresent(user::setLastName);
        Optional.ofNullable(userDTO.getAvatarURL()).ifPresent(user::setAvatarURL);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }


    public HttpStatus deleteUser(String email) {
        userAuthInfoRepository.findByEmail(email).ifPresent(userAuthInfoRepository::delete);
        return HttpStatus.OK;
    }

    @Transactional
    public HttpStatus subscribeUser(String loginCurrentUser, String loginTargetUser) {
        User targetUser = getUserByLogin(loginTargetUser);
        targetUser.getSubscribers().add(getUserByLogin(loginCurrentUser));
        User user = getUserByLogin(loginCurrentUser);
        user.getSubscription().add(targetUser);
        user.setNumOfSubs(user.getNumOfSubs() + 1);
        userRepository.save(user);
        userRepository.save(targetUser);
        return HttpStatus.OK;
    }

    @Transactional
    public HttpStatus unsubscribeUser(String loginCurrentUser, String loginTargetUser) {
        User targetUser = getUserByLogin(loginTargetUser);
        targetUser.getSubscribers().remove(getUserByLogin(loginCurrentUser));
        User user = getUserByLogin(loginCurrentUser);
        user.getSubscription().remove(targetUser);
        if(user.getNumOfSubs() > 0) {
            user.setNumOfSubs(user.getNumOfSubs() - 1);
        }
        userRepository.save(user);
        userRepository.save(targetUser);
        return HttpStatus.OK;
    }

    public HttpStatus setBirth(String loginCurrentUser, Instant birthDate) {
        User user = getUserByLogin(loginCurrentUser);
        user.setBirthDate(birthDate);
        userRepository.save(user);
        return HttpStatus.OK;
    }

    public HttpStatus setFirstName(String loginCurrentUser, String newFirstName) {
        User user = getUserByLogin(loginCurrentUser);
        user.setFirstName(newFirstName);
        userRepository.save(user);
        return HttpStatus.OK;
    }

    public HttpStatus setLastName(String loginCurrentUser, String newLastName) {
        User user = getUserByLogin(loginCurrentUser);
        user.setLastName(newLastName);
        userRepository.save(user);
        return HttpStatus.OK;
    }

    public HttpStatus setAvatarURL(String loginCurrentUser, String avatarURL) {
        User user = getUserByLogin(loginCurrentUser);
        user.setAvatarURL(avatarURL);
        userRepository.save(user);
        return HttpStatus.OK;
    }
}
