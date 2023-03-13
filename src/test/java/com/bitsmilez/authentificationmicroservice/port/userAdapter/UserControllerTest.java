package com.bitsmilez.authentificationmicroservice.port.userAdapter;

import com.bitsmilez.authentificationmicroservice.core.service.interfaces.IGateway;
import com.bitsmilez.authentificationmicroservice.core.service.interfaces.IUserService;
import com.bitsmilez.authentificationmicroservice.port.requests.CreateUserRequest;
import com.bitsmilez.authentificationmicroservice.port.requests.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private  IUserService userService;

    @Mock
    private  IGateway gateway;

    @InjectMocks
    private  UserController userController;
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void register() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("test", "test", "test", "test", "test");

        when(userService.createKeycloakUser(createUserRequest)).thenReturn(201);
        mockMvc.perform(post("/user/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());

        verify(userService).createKeycloakUser(createUserRequest);
    }

    @Test
    void register_knownUser() throws Exception {
        register();
        CreateUserRequest createUserRequest = new CreateUserRequest("test", "test", "test", "test", "test");

        when(userService.createKeycloakUser(createUserRequest)).thenReturn(409);
        mockMvc.perform(post("/user/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isConflict());
        verify(userService,times(2)).createKeycloakUser(createUserRequest);
    }

    @Test
    void login_successful() throws Exception {
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        LoginRequest loginRequest = new LoginRequest("test", "test");
    when(userService.login("test", "test")).thenReturn(accessTokenResponse);

        MvcResult res = mockMvc.perform(post("/user/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()).andReturn();
        String content = res.getResponse().getContentAsString();
        AccessTokenResponse accessTokenResponse1 = objectMapper.readValue(content, AccessTokenResponse.class);
        assertEquals( accessTokenResponse.getToken(), accessTokenResponse1.getToken());

    }
    @Test
    void login_unsuccessful() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test", "test");
        when(userService.login("test", "test")).thenReturn(null);

        mockMvc.perform(post("/user/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());

    }

    @Test
    void add_to_cart_successful() throws Exception {
        when(userService.verifyToken(any(String.class))).thenReturn(200);
        when(gateway.publishAddToCartEvent(any(String.class), any(Integer.class), any(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ObjectNode node = objectMapper.createObjectNode();
        node.put("productID", 1);
        node.put("quantity", 1);
        node.put("cartID", 1);
        mockMvc.perform(post("/user/add-to-cart").contentType("application/json")
                .header("Authorization", "Bearer test").content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());

        verify(userService).verifyToken(any(String.class));
        verify(gateway).publishAddToCartEvent(any(String.class), any(Integer.class), any(String.class));


    }

    @Test
    void add_to_cart_no_authorization() throws Exception{
        ObjectNode node = objectMapper.createObjectNode();
        mockMvc.perform(post("/user/add-to-cart").contentType("application/json")
                        .header("Authorization", "").content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isForbidden());
        verify(userService, never()).verifyToken(any(String.class));
        verify(gateway, never()).publishAddToCartEvent(any(String.class), any(Integer.class), any(String.class));
    }

   @Test
    void add_to_cart_wrong_authorization() throws Exception {
        when(userService.verifyToken(any(String.class))).thenReturn(401);
        ObjectNode node = objectMapper.createObjectNode();
       mockMvc.perform(post("/user/add-to-cart").contentType("application/json")
                       .header("Authorization", "Bearer test").content(objectMapper.writeValueAsString(node)))
               .andExpect(status().isForbidden());
       verify(userService).verifyToken(any(String.class));
       verify(gateway, never()).publishAddToCartEvent(any(String.class), any(Integer.class), any(String.class));
   }

    @Test
    void remove_from_cart_successful () throws Exception{
        when(userService.verifyToken(any(String.class))).thenReturn(200);
        when(gateway.publishRemoveFromCartEvent(any(String.class), any(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ObjectNode node = objectMapper.createObjectNode();
        node.put("productID", 1);
        node.put("cartID", 1);
        mockMvc.perform(post("/user/remove-from-cart").contentType("application/json")
                        .header("Authorization", "Bearer test").content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
        verify(userService).verifyToken(any(String.class));
        verify(gateway).publishRemoveFromCartEvent(any(String.class), any(String.class));

    }
    @Test
    void remove_from_cart_wrong_authorization() throws Exception{
        when(userService.verifyToken(any(String.class))).thenReturn(401);
        ObjectNode node = objectMapper.createObjectNode();
        mockMvc.perform(
                post("/user/remove-from-cart").contentType("application/json").header(
                        "Authorization", "123"
                ).content(objectMapper.writeValueAsString(node))
        ).andExpect(status().isForbidden());
        verify(userService).verifyToken(any(String.class));
        verify(gateway, never()).publishRemoveFromCartEvent(any(String.class), any(String.class));

    }

    @Test
    void update_cart_successful() throws Exception{
        when(userService.verifyToken(any(String.class))).thenReturn(200);
        when(gateway.publishUpdateCartEvent(any(String.class), any(Integer.class), any(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ObjectNode node = objectMapper.createObjectNode();
        node.put("productID", 1);
        node.put("quantity", 1);
        node.put("cartID", 1);
        mockMvc.perform(post("/user/update-cart").contentType("application/json")
                        .header("Authorization", "Bearer test").content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
        verify(userService).verifyToken(any(String.class));
        verify(gateway).publishUpdateCartEvent(any(String.class), any(Integer.class), any(String.class));
    }

    @Test
    void update_cart_wrong_authorization() throws Exception{
        when(userService.verifyToken(any(String.class))).thenReturn(401);

        ObjectNode node = objectMapper.createObjectNode();
        mockMvc.perform(post("/user/update-cart").contentType("application/json")
                        .header("Authorization", "Bearer test").content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isForbidden());

        verify(userService).verifyToken(any(String.class));
        verify(gateway, never()).publishUpdateCartEvent(any(String.class), any(Integer.class), any(String.class));
    }




}