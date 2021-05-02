package com.web.controller;

import com.ecommerce.data.dtos.PartnerRegisterDto;
import com.ecommerce.data.dtos.UserDto;
import com.ecommerce.data.exceptions.ApiException;
import com.web.services.ExchangeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequestMapping(value = "/partner")
@Controller
public class PartnerProgramController extends BasicController{

    private final String commonUrl;
    private final RestTemplate restTemplate;
    private final String partnerLogin;


    @Autowired
    public PartnerProgramController(@Value("${app.service.common}") String commonUrl, @Value("${app.service.partner}") String partnerLogin, RestTemplate restTemplate){
        this.commonUrl = commonUrl;
        this.restTemplate = restTemplate;
        this.partnerLogin = partnerLogin;
    }

    @RequestMapping()
    public String getPartnerPage(@CookieValue(value = "token", defaultValue = "")String token, ModelMap map){
        if(!StringUtils.isEmpty(token)){
            return partnerDashboard(map, token);
        }

        return "partner-register";
    }

    @RequestMapping("/register")
    public String register(@ModelAttribute PartnerRegisterDto partnerRegisterDto, HttpServletResponse httpServletResponse, ModelMap map) {
        log.info("Registering a new partner");
        ExchangeUtils.postData(commonUrl, "partner/register", PartnerRegisterDto.class, restTemplate, null, partnerRegisterDto, null);

        UserDto userDto = new UserDto();
        userDto.setLogin(partnerRegisterDto.getEmail());
        userDto.setPassword(partnerRegisterDto.getPassword());
        ResponseEntity<UserDto> response = restTemplate.postForEntity(loginUrl, userDto, UserDto.class);
        HttpHeaders headers = response.getHeaders();

        Cookie cookie = new Cookie("token", headers.get("Authorization").get(0));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);

        return partnerDashboard(map, headers.get("Authorization").get(0));
    }

    @RequestMapping("/login")
    public String partnerLogin(){
        return "partner-login";
    }

    @RequestMapping("/loginSubmit")
    public String partnerLogin(@ModelAttribute UserDto userDto, HttpServletResponse httpServletResponse, ModelMap map){
        if(userDto != null && userDto.getLogin() != null) {
            try {

                ResponseEntity<UserDto> response = restTemplate.postForEntity(loginUrl, userDto, UserDto.class);
                HttpHeaders headers = response.getHeaders();

                Cookie cookie = new Cookie("token", headers.get("Authorization").get(0));
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                //cookie.setSecure(true); to enable on live env
                httpServletResponse.addCookie(cookie);

                return partnerDashboard(map, headers.get("Authorization").get(0));

            }catch(ApiException exc){
                map.put("error", "Nieprawidłowy login lub hasło.");
                return "partner-login";
            }

        }
        return "partner-login";
    }

    @RequestMapping("/partner-main")
    public String publicDashboard(@CookieValue(value = "token", defaultValue = "")String token, ModelMap map){
        return partnerDashboard(map, token);
    }


    public String partnerDashboard(ModelMap map, String token){
        UserDto user = getCurrentUser(token);

        if(!user.getRole().equals("PARTNER") || !user.isEnabled()){
            map.put("error", "Nie masz dostępu do tej strony.");
        }else{
            map.put("login", user.getLogin());
            map.put("points", user.getPoints() == null ? "0" : user.getPoints());
            map.put("link", user.getLink());
            map.put("sold", user.getSoldItems());
            map.put("name", user.getName());
            map.put("surname", user.getSurname());
        }
        return "partner-main";
    }



}
