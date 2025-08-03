package com.app.flashcard.user.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for user registration
 */
public class UserRegistrationDTO {
    
    @NotBlank(message = "Tài khoản không được để trống")
    @Size(min = 3, max = 50, message = "Tài khoản phải từ 3-50 ký tự")
    private String loginID;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 4, max = 100, message = "Mật khẩu phải từ 4-100 ký tự")
    private String password;
    
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải từ 2-100 ký tự")
    private String name;
    
    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 13, message = "Tuổi phải từ 13 trở lên")
    @Max(value = 120, message = "Tuổi không được quá 120")
    private Integer age;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được quá 100 ký tự")
    private String email;

    public UserRegistrationDTO() {}

    public UserRegistrationDTO(String loginID, String password, String name, Integer age, String email) {
        this.loginID = loginID;
        this.password = password;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}