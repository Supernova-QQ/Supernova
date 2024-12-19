package com.hanshin.supernova.user.application;

import com.hanshin.supernova.community.application.CommunityService;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.auth.UserAuthManagementInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.exception.user.UserRegisterInvalidException;
import com.hanshin.supernova.news.application.NewsService;
import com.hanshin.supernova.news.domain.Type;
import com.hanshin.supernova.news.dto.request.NewsRequest;
import com.hanshin.supernova.security.application.JwtService;
import com.hanshin.supernova.user.domain.Activity;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.dto.response.ChangePasswordResponse;
import com.hanshin.supernova.user.dto.response.ResetPasswordResponse;
import com.hanshin.supernova.user.dto.response.UserRegisterResponse;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CommunityService communityService;
    private final NewsService newsService;


    @Transactional
    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserRegisterInvalidException(ErrorType.EMAIL_DUPLICATE_ERROR);
        }

        // 닉네임 중복 검증
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserRegisterInvalidException(ErrorType.NICKNAME_DUPLICATE_ERROR);
        }

        validatePassword(request.getPassword());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UserRegisterInvalidException(ErrorType.PASSWORD_NOT);
        }

        User savedUser = buildAndSaveUser(request);

        // 가입과 동시에 일반 게시판 회원 등록
        communityService.joinGeneralCommunity(savedUser);

        // 가입 알림 전송
        NewsRequest newsRequest = new NewsRequest("축하합니다! 가입이 완료되었습니다.",
                "회원님은 현재 일반 게시판을 자유롭게 이용 가능합니다.", Type.COMMUNITY, false, null, savedUser.getId());
        newsService.createNews(newsRequest);

        return UserRegisterResponse.toResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getNickname(),
                savedUser.getEmail(),
                savedUser.getPassword(),
                savedUser.getAuthority());
    }

    private User buildAndSaveUser(UserRegisterRequest request) {
        Activity newActivity = new Activity();

        // request.getAuthority()가 null인 경우 Authority.USER로 설정
        Authority authority = request.getAuthority() != null ? request.getAuthority() : Authority.USER;

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .nickname(request.getNickname())
                .authority(authority)
                .activity(newActivity)
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    @Override
    public User getByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Override
    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,}$");
    }

    @Transactional
    @Override
    public ChangePasswordResponse changePassword(HttpServletRequest request, String currentPassword, String newPassword, String confirmNewPassword) {
        // 토큰을 통해 이메일 추출
        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            throw new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }

        User user = getByEmail(email);
        if (user == null) {
            throw new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new AuthInvalidException(ErrorType.WRONG_PASSWORD_ERROR);
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new ChangePasswordResponse("Password changed successfully");
    }

    @Transactional
    @Override
    public ResetPasswordResponse resetPassword(String email, String username, String newPassword, String confirmNewPassword) {
        User user = userRepository.findByEmailAndUsername(email, username)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));

        if (!newPassword.equals(confirmNewPassword)) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new ResetPasswordResponse("Password reset successfully");
    }

    @Transactional
    @Override
    public void deleteUser(Long userId, String password) {
        User user = getById(userId);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }
        userRepository.delete(user);
    }

//// userId만으로 회원 삭제(외래키의 영향을 받는다면 검사를 비활성화하여 삭제되도록 함
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Transactional
//    @Override
//    public void deleteUser(Long userId) {
//        User user = getById(userId);
//        // 외래 키 검사 비활성화
//        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
//
//        // 유저 삭제
//        userRepository.deleteById(userId);
//
//        // 외래 키 검사 활성화
//        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
//
//    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserFromClaims(HttpServletRequest request) {

        // JwtFilter에서 저장한 Claims 객체를 HttpServletRequest에서 가져옴
        Claims claims = (Claims) request.getAttribute("claims");

        if (claims == null) {
            throw new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }
        // Claims에서 사용자 이메일 추출
        String email = claims.getSubject();
        if (email == null) {
            throw new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }

        // 이메일을 통해 User 엔티티 조회하여 반환
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR));
    }

    public String getUsernameById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    public String getNicknameById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getNickname) // User 객체의 getNickname 호출
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)); // 유저가 없으면 예외 발생
    }
}
