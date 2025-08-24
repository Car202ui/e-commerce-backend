package com.proyecto.backend.e_commerce.service.ImplService;

import com.proyecto.backend.e_commerce.dto.RegisterDto;
import com.proyecto.backend.e_commerce.dto.RoleDto;
import com.proyecto.backend.e_commerce.dto.UserDto;
import com.proyecto.backend.e_commerce.domain.Role;
import com.proyecto.backend.e_commerce.domain.User;
import com.proyecto.backend.e_commerce.dto.UserUpdateRequestDto;
import com.proyecto.backend.e_commerce.exception.ResourceAlreadyExistsException;
import com.proyecto.backend.e_commerce.exception.ResourceNotFoundException;
import com.proyecto.backend.e_commerce.repository.RoleRepository;
import com.proyecto.backend.e_commerce.repository.UserRepository;
import com.proyecto.backend.e_commerce.security.JwtTokenProvider;
import com.proyecto.backend.e_commerce.service.Iservice.IUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User registerUser(RegisterDto registerDto) {
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("El nombre de usuario '" + registerDto.getUsername() + "' ya está en uso.");
        }
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("El email '" + registerDto.getEmail() + "' ya está registrado.");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setFrequent(false);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("El rol 'ROLE_USER' no fue encontrado."));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        return userRepository.save(user);
    }


    @Override
    public String generateToken(Authentication authentication) {
        return jwtTokenProvider.generateToken(authentication);
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
         userDto.setFirstName(user.getFirstName());
         userDto.setLastName(user.getLastName());
        userDto.setFrequent(user.isFrequent());

        if (user.getRoles() != null) {
            userDto.setRoles(user.getRoles().stream()
                    .map(this::convertRoleToDto)
                    .collect(Collectors.toSet()));
        }

        return userDto;
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));
        return convertToDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Usuario no encontrado con id: " + id));

        userRepository.delete(user);
    }



    @Override
    @Transactional
    public UserDto updateUser(Long id, UserUpdateRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (request.getUsername() != null)  user.setUsername(request.getUsername());
        if (request.getEmail() != null)     user.setEmail(request.getEmail());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)  user.setLastName(request.getLastName());
        if (request.getFrequent() != null)  user.setFrequent(request.getFrequent());

        if (request.getRoles() != null) {
            Set<Role> roles = roleRepository.findByNameIn(new HashSet<>(request.getRoles()));
            if (roles.size() != request.getRoles().size()) {

            }
            user.setRoles(roles);
        }

        return convertToDto(userRepository.save(user));
    }


    private RoleDto convertRoleToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        return roleDto;
    }

}
