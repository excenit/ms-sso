package com.excenit.sso.service;

import com.excenit.sso.config.SsoConfig;
import com.excenit.sso.entity.SsoUser;
import com.google.gson.Gson;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service(MicrosoftRegistrationService.NAME)
public class MicrosoftRegistrationServiceBean implements MicrosoftRegistrationService {
    @Inject
    private Metadata metadata;

    @Inject
    private Persistence persistence;

    @Inject
    private SsoConfig systemConfig;


    @Override
    @Transactional
    public SsoUser findOrRegisterUser(String microsoftId, String email, String name) {
        EntityManager em = persistence.getEntityManager();

        // Find existing user
        TypedQuery<SsoUser> query = em.createQuery(
                "select u from sso_SsoUser u where u.microsoftId = :microsoftId",
                SsoUser.class);
        query.setParameter("microsoftId", microsoftId);
        query.setViewName(View.LOCAL);

        SsoUser existingUser = query.getFirstResult();
        if (existingUser != null) {
            return existingUser;
        }

        TypedQuery<Role> roleQuery = em.createQuery("select r from sec$Role r where r.name = :roleName",
                Role.class);
        roleQuery.setParameter("roleName",systemConfig.getDefaultRoleName());

        roleQuery.setViewName(View.LOCAL);
        Role defaultRole = roleQuery.getFirstResult();


        Group defaultGroup = em.find(Group.class, systemConfig.getDefaultGroupId(), View.MINIMAL);

        // Register new user
        SsoUser user = metadata.create(SsoUser.class);
        User systemUser = metadata.create(User.class);

        UserRole userRole = metadata.create(UserRole.class);
        userRole.setRole(defaultRole);
        userRole.setUser(systemUser);

        em.persist(userRole);

        user.setMicrosoftId(microsoftId);
        user.setEmail(email);
        user.setName(name);
        user.setGroup(defaultGroup);
        user.setActive(true);
        user.setLogin(email);

        em.persist(user);

        return user;
    }

    @Override
    @Transactional
    public User findOrRegisterUser(String userJson) {
        EntityManager em = persistence.getEntityManager();
        Gson gson = new Gson();

        Map<String,Object> responseMap = gson.fromJson(userJson,Map.class);

        String microsoftId = responseMap.get("id").toString();
        String email = responseMap.get("mail").toString();
        String fullName = responseMap.get("displayName").toString();
        String firstName = responseMap.get("givenName").toString();
        String lastName = responseMap.get("surname").toString();
        List<String> phones = (List)responseMap.get("businessPhones");

        TypedQuery<SsoUser> query = em.createQuery("select u from sso_SsoUser u where u.microsoftId = :microsoftId",
                SsoUser.class);
        query.setParameter("microsoftId", microsoftId);
        query.setViewName(View.LOCAL);

        SsoUser existingUser = query.getFirstResult();
        if (existingUser != null) {
            return existingUser;
        }

        TypedQuery<Role> roleQuery = em.createQuery("select r from sec$Role r where r.defaultRole = true",
                Role.class);
        //roleQuery.setParameter("roleName",systemConfig.getDefaultRoleName());

        roleQuery.setViewName(View.LOCAL);
        Role defaultRole = roleQuery.getFirstResult();


        Group defaultGroup = em.find(Group.class, systemConfig.getDefaultGroupId(), View.MINIMAL);

        // Register new user
        SsoUser user = metadata.create(SsoUser.class);

        UserRole userRole = metadata.create(UserRole.class);
        userRole.setRole(defaultRole);
        userRole.setUser(user);


        em.persist(userRole);

        //user.setBrokersureRole(Arrays.asList(defaultRole));
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user.setMicrosoftId(microsoftId);
        user.setEmail(email);
        user.setName(fullName);
        user.setGroup(defaultGroup);
        user.setActive(true);
        user.setLogin(email);

        em.persist(user);

        return user;
    }
}