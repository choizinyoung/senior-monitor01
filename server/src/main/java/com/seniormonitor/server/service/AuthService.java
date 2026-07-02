package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.LoginRequest;
import com.seniormonitor.server.dto.LoginResponse;
import com.seniormonitor.server.dto.ManagerResponse;
import com.seniormonitor.server.dto.SignupRequest;
import com.seniormonitor.server.entity.Manager;
import com.seniormonitor.server.entity.RevokedToken;
import com.seniormonitor.server.exception.BadRequestException;
import com.seniormonitor.server.exception.ConflictException;
import com.seniormonitor.server.exception.ForbiddenException;
import com.seniormonitor.server.exception.UnauthorizedException;
import com.seniormonitor.server.repository.ManagerRepository;
import com.seniormonitor.server.repository.RevokedTokenRepository;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final ManagerRepository managerRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(ManagerRepository managerRepository,
                        RevokedTokenRepository revokedTokenRepository,
                        PasswordEncoder passwordEncoder,
                        JwtProvider jwtProvider) {
        this.managerRepository = managerRepository;
        this.revokedTokenRepository = revokedTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public ManagerResponse signup(SignupRequest req) {
        if (req.getName() == null || req.getUsername() == null || req.getPassword() == null
                || req.getPhone() == null || req.getEmail() == null) {
            throw new BadRequestException("ERR_MISSING_FIELD", "name, username, password, phone, email은 필수 항목입니다.");
        }
        if (managerRepository.existsByUsername(req.getUsername())) {
            throw new ConflictException("ERR_DUPLICATE_USERNAME", "이미 사용 중인 아이디입니다.");
        }
        if (managerRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("ERR_DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.");
        }

        Manager manager = new Manager();
        manager.setName(req.getName());
        manager.setUsername(req.getUsername());
        manager.setPassword(passwordEncoder.encode(req.getPassword()));
        manager.setPhone(req.getPhone());
        manager.setEmail(req.getEmail());
        manager.setRole("MANAGER");
        manager.setStatus("PENDING");

        return new ManagerResponse(managerRepository.save(manager));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        if (req.getUsername() == null || req.getPassword() == null) {
            throw new BadRequestException("ERR_MISSING_FIELD", "username, password는 필수 항목입니다.");
        }

        Manager manager = managerRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new UnauthorizedException("ERR_INVALID_CREDENTIALS", "아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), manager.getPassword())) {
            throw new UnauthorizedException("ERR_INVALID_CREDENTIALS", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (!"APPROVED".equals(manager.getStatus())) {
            throw new ForbiddenException("ERR_NOT_APPROVED", "아직 승인되지 않은 계정입니다.");
        }

        String token = jwtProvider.generateToken(manager);
        return new LoginResponse(token, new ManagerResponse(manager));
    }

    public void logout(CurrentManager currentManager) {
        RevokedToken revoked = new RevokedToken();
        revoked.setJti(currentManager.jti());
        revoked.setExpiresAt(currentManager.expiresAt());
        revokedTokenRepository.save(revoked);
    }
}
