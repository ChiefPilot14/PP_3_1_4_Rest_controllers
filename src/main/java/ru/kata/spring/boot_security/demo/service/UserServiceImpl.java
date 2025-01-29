package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
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
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserDao userDao, RoleDao roleDao, RoleService roleService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDao = userDao;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void createUser(User user) {
        if (user.getUsername().equals(userDao.findByUsername(user.getUsername()))) {
            throw new EntityNotFoundException("Пользователь с username=" + user.getUsername() + " уже существует");
        }
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, User updatedUser, Long[] rolesIds) {
        User existingUser = userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        if (!StringUtils.isEmpty(updatedUser.getPassword())) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
        } else {
            updatedUser.setPassword(existingUser.getPassword()); // Сохраняем существующий пароль, если новый не передан
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
        existingUser.setRoles(updatedRoles);

        userDao.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userDao.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }


    @Override
    public User getUserOrCreateIfNotExists(long id) {
        return userDao.findById(id)
                .orElse(new User(id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userDao.findByUsername(username);
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

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userDao.findById(id);
    }
}