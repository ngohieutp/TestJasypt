package com.mt.request.user;

import com.mt.constraints.UserGroups;
import com.mt.data.enums.Status;
import com.mt.validation.UserGroupSequenceProvider;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@GroupSequenceProvider(UserGroupSequenceProvider.class)
public class UserRequest {

    @NotBlank(message = "{username_cannot_blank}")
    @Size(max = 100, message = "{username_max_length}")
    private String username;
    @Size(max = 100, message = "{full_name_max_length}")
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;
    @NotBlank(message = "Email không được để trống", groups = UserGroups.UserStatus.Active.class)
    private String email;
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;
    @NotNull(message = "Trạng thái bắt buộc phải nhập thuộc active, deactive")
    private Status status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
