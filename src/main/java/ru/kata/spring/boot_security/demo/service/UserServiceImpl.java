package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kata.spring.boot_security.demo.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDao userDao;
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserDao userDao, RoleService roleService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDao = userDao;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void createUser(User user) {
        if (findByUsername(user.getUsername()).isPresent()) {
            throw new EntityNotFoundException("Пользователь с username=" + user.getUsername() + " уже существует");
        }

        Set<Role> roles = user.getRoles();

        if (roles != null && !roles.isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            user.setRoles(roles, false);

            userDao.save(user);
        } else {
            throw new IllegalArgumentException("Пользователь должен иметь хотя бы одну роль");
        }
    }

    @Override
    @Transactional
    public void updateUser(Long userId, User updatedUser, Long[] rolesIds) {
        User existingUser = userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        if (!updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
        } else {
            updatedUser.setPassword(existingUser.getPassword());
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setAge(updatedUser.getAge());

        Set<Role> updatedRoles = new HashSet<>();
        if (rolesIds != null) {
            for (Long roleId : rolesIds) {
                Optional<Role> optionalRole = roleService.findById(roleId);
                if (optionalRole.isPresent()) {
                    updatedRoles.add(optionalRole.get());
                } else {
                    throw new EntityNotFoundException("Роль с id=" + roleId + " не найдена");
                }
            }
        }
        existingUser.setRoles(updatedRoles, true);

        userDao.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserOrCreateIfNotExists(long id) {
        return userDao.findById(id)
                .orElse(new User(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(long id) {
        return userDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        User userEntity = optionalUser.get();

        if (userEntity.getPassword() == null || userEntity.getPassword().isBlank()) {
            throw new InternalAuthenticationServiceException("Password is missing for user: " + username);
        }

        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                authorities
        );
    }
    @Transactional(readOnly = true)
    public Optional<User> findUserWithRoles(String username) {
        return userDao.findUserWithRoles(username);
    }
}