package Raven.example.service.impl;

import Raven.example.entity.model.RegisterModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(org.springframework.test.context.junit4.SpringRunner.class)
@Slf4j
class AccountServiceImplTest {

    @org.junit.jupiter.api.Test
    void login() {
    }

    @org.junit.jupiter.api.Test
    void register() {
        RegisterModel registerModel = new RegisterModel();
        registerModel.setEmail("a2506691786@qq.com")
                .setUsername("Raven")
                .setPassword("liujiahui1Y!")
                .setProfile(null);
    }
}