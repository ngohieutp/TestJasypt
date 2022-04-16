package com.mt.validation;

import com.mt.constraints.UserGroups;
import com.mt.data.enums.Status;
import com.mt.request.user.UserRequest;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

public class UserGroupSequenceProvider implements DefaultGroupSequenceProvider<UserRequest> {

    @Override
    public List<Class<?>> getValidationGroups(UserRequest request) {
        List<Class<?>> sequence = new ArrayList<>();
        sequence.add(UserRequest.class);

        if (request != null) {
            if (request.getStatus() == Status.ACTIVE) {
                sequence.add(UserGroups.UserStatus.Active.class);
            } else {
                sequence.add(UserGroups.UserStatus.Inactive.class);
            }
        }

        return sequence;
    }
}
