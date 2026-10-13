package com.foodcourt.user.infrastructure.output.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.SocketTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.foodcourt.user.domain.exception.RestaurantDirectoryUnavailable;
import com.foodcourt.user.domain.model.RestaurantOwnership;

class PlazaRestaurantDirectoryTest {

    private static final String URL = "http://plaza-service/restaurants/8";

    private MockRestServiceServer server;
    private PlazaRestaurantDirectory directory;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://plaza-service");
        server = MockRestServiceServer.bindTo(builder).build();
        directory = new PlazaRestaurantDirectory(builder.build());
    }

    @Test
    void findByIdReturnsTheOwnerOfTheRestaurant() {
        server.expect(requestTo(URL))
                .andRespond(withSuccess("{\"id\":8,\"ownerId\":15}", MediaType.APPLICATION_JSON));

        assertThat(directory.findById(8L)).contains(new RestaurantOwnership(8L, 15L));
        server.verify();
    }

    @Test
    void findByIdIsEmptyWhenTheRestaurantIsMissing() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(directory.findById(8L)).isEmpty();
        server.verify();
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"UNAUTHORIZED", "FORBIDDEN", "INTERNAL_SERVER_ERROR", "SERVICE_UNAVAILABLE"})
    void findByIdReportsAnUnexpectedStatusAsUnavailable(HttpStatus status) {
        server.expect(requestTo(URL)).andRespond(withStatus(status));

        assertThatThrownBy(() -> directory.findById(8L)).isInstanceOf(RestaurantDirectoryUnavailable.class);
        server.verify();
    }

    @Test
    void findByIdReportsATimeoutAsUnavailable() {
        server.expect(requestTo(URL)).andRespond(withException(new SocketTimeoutException("read timed out")));

        assertThatThrownBy(() -> directory.findById(8L)).isInstanceOf(RestaurantDirectoryUnavailable.class);
        server.verify();
    }

    @Test
    void findByIdReportsAnIncompleteBodyAsUnavailable() {
        server.expect(requestTo(URL))
                .andRespond(withSuccess("{\"id\":8}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> directory.findById(8L)).isInstanceOf(RestaurantDirectoryUnavailable.class);
        server.verify();
    }
}
