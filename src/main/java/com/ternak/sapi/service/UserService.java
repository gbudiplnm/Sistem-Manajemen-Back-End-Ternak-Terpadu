package com.ternak.sapi.service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ternak.sapi.exception.ResourceNotFoundException;
import com.ternak.sapi.model.Peternak;
import com.ternak.sapi.payload.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ternak.sapi.exception.BadRequestException;
import com.ternak.sapi.model.RumpunHewan;
import com.ternak.sapi.model.User;
import com.ternak.sapi.repository.UserRepository;
import com.ternak.sapi.util.AppConstants;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserRepository userRepository = new UserRepository();

    // private static final Logger logger =
    // LoggerFactory.getLogger(UserService.class);

    ZoneId zoneId = ZoneId.of("Asia/Jakarta");
    ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
    Instant instant = zonedDateTime.toInstant();

    public PagedResponse<User> getAllUser(int page, int size) throws IOException {
        validatePageNumberAndSize(page, size);

        // Retrieve Polls
        List<User> userResponse = userRepository.findAll(size);

        return new PagedResponse<>(userResponse, userResponse.size(), "Successfully get data", 200);
    }


    @Transactional
    public User createUser(UserRequest userRequest) throws IOException {
        // Validasi jika email sudah ada
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email sudah terdaftar!");
        }

        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username sudah terdaftar!");
        }
        if (userRepository.existsByNik(userRequest.getNik())) {
            throw new IllegalArgumentException("Nik sudah terdaftar!");
        }

        User user = new User();
        user.setId(userRequest.getId());
        user.setName(userRequest.getName());
        user.setNik(userRequest.getNik());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setAlamat(userRequest.getAlamat());
        user.setRole(userRequest.getRole());
        user.setCreatedAt(userRequest.getCreatedAt() != null ? userRequest.getCreatedAt() : instant);
        return userRepository.saveForm(user);
    }

    public User getUserById(String userId) throws IOException {
        return userRepository.findByUserIdAll(userId);
    }

    @Transactional
    public User update(String userId, UserRequest userRequest)throws  IOException{
        User user = new User();
        User userResponse = userRepository.findByUserId(userId);
        if(userResponse != null) {
            user.setNik(userRequest.getNik());
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setUsername(userRequest.getUsername());
            user.setAlamat(userRequest.getAlamat());
            user.setRole(userRequest.getRole());
            user.setCreatedAt(userRequest.getCreatedAt() != null ? userRequest.getCreatedAt() : instant);
            if (userRequest.getNewPassword() == null){
                user.setPassword(userResponse.getPassword());
            }else{
                user.setPassword(passwordEncoder.encode(userRequest.getNewPassword()));
            }
        }
        return userRepository.update(userId,user);
    }

    @Transactional
    public void createBulkUser(List<UserRequest> userRequest) throws IOException {
        System.out.println("Memulai proses penyimpanan data user secara bulk...");
        List<String> nikList = userRequest.stream().map(UserRequest::getNik).collect(Collectors.toList());
        List<String> emailList = userRequest.stream().map(UserRequest::getEmail).collect(Collectors.toList());
        List<String> usernameList = userRequest.stream().map(UserRequest::getUsername).collect(Collectors.toList());

        // Check which NIK, Email, and NoTelp already exist
        System.out.println("Memeriksa NIK, Email, dan Username yang sudah terdaftar...");
        Set<String> existingNikSet = new HashSet<>(userRepository.findExistingNik(nikList));
        Set<String> existingEmailSet = new HashSet<>(userRepository.findExistingEmail(emailList));
        Set<String> existingUsernameSet = new HashSet<>(userRepository.findExistingUsername(usernameList));
        List<User> userList = new ArrayList<>();
        int skippedIncomplete = 0;
        int skippedExisting = 0;

        for (UserRequest request : userRequest) {
            try {
                if (existingNikSet.contains(request.getNik())) {
                    System.out.println("NIK sudah terdaftar, melewati NIK: " + request.getNik());
                    skippedExisting++;
                    continue;
                }
                if (existingEmailSet.contains(request.getEmail())) {
                    System.out.println("Email sudah digunakan, melewati Email: " + request.getEmail());
                    skippedExisting++;
                    continue;
                }
                if (existingUsernameSet.contains(request.getUsername())) {
                    System.out.println("Username sudah terdaftar, melewati Username: " + request.getUsername());
                    skippedExisting++;
                    continue;
                }
                String password = passwordEncoder.encode(request.getPassword().toString());

                User user = new User();
                user.setId(request.getId() != null ? request.getId() : "-");
                user.setName(request.getName() != null ? request.getName() : "-");
                user.setNik(request.getNik() != null ? request.getNik() : "-");
                user.setUsername(request.getUsername() != null ? request.getUsername() : "-");
                user.setEmail(request.getEmail() != null ? request.getEmail() : "-");
                user.setPassword(password);
                user.setAlamat(request.getAlamat() != null ? request.getAlamat() : "-");
                user.setRole(request.getRole() != null ? request.getRole() : "-");
                user.setCreatedAt(instant);

                userList.add(user);
                System.out.println("Menambahkan user ke dalam daftar: " + user.getId());
            } catch (Exception e) {
                System.err.println("Terjadi kesalahan saat memproses data: " + request);
                e.printStackTrace();
            }
        }

        if (!userList.isEmpty()) {
            System.out.println("Menyimpan data user yang valid...");
            userRepository.saveBulk(userList);
            System.out.println("Data User berhasil disimpan. Total: " + userList.size());
        } else {
            System.out.println("Tidak ada data User baru yang valid untuk disimpan.");
        }

        System.out.println("Proses selesai. Data tidak lengkap: " + skippedIncomplete);
    }

    public void deleteUserById(String userId) throws IOException {
        User userResponse = userRepository.findById(userId);
        if (userResponse.isValid()) {
            userRepository.deleteById(userId);
        } else {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }


    public PagedResponse<User> getUserNotUsedAccount(int page, int size) throws IOException {
        validatePageNumberAndSize(page, size);

        // Retrieve Polls
        List<User> userResponse = userRepository.findUsersNotUsedInLectures(size);

        return new PagedResponse<>(userResponse, userResponse.size(), "Successfully get data", 200);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
