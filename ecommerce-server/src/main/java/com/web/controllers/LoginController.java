package com.web.controllers;

import com.ecommerce.data.dtos.AuthRequest;
import com.ecommerce.data.dtos.UserDto;
import com.ecommerce.data.entities.User;
import com.ecommerce.data.exceptions.ApiException;
import com.ecommerce.data.services.UserService;
import com.web.config.JwtTokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import static ch.qos.logback.core.util.OptionHelper.isEmpty;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    public LoginController(AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<UserDto> login(@RequestBody AuthRequest request) throws LoginException {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getLogin(), request.getPassword()
                            )
                    );

            User user = (User) authenticate.getPrincipal();

            UserDto userDto = new UserDto(user.getLogin());
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            jwtTokenUtil.generateAccessToken(user)
                    )
                    .body(userDto);
        } catch (BadCredentialsException ex) {
           throw new LoginException("Invalid login or password");
        }
    }

    @GetMapping("/me")
    public UserDto me(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            return null;
        }

        final String token = header.split(" ")[1].trim();
        if (!jwtTokenUtil.validate(token)) {
            return null;
        }

        User userDetails = userService.findUserByLogin(jwtTokenUtil.getUsername(token));

        UserDto user = new UserDto();
        user.setLogin(userDetails.getLogin());

        return user;
    }
}
