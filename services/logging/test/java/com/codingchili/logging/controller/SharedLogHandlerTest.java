package com.codingchili.logging.controller;

import com.codingchili.common.Strings;
import com.codingchili.core.context.SystemContext;
import com.codingchili.core.files.Configurations;
import com.codingchili.core.logging.DefaultLogger;
import com.codingchili.core.logging.Level;
import com.codingchili.core.protocol.Serializer;
import com.codingchili.core.security.Token;
import com.codingchili.core.security.TokenFactory;
import com.codingchili.core.testing.RequestMock;
import com.codingchili.core.testing.ResponseListener;
import com.codingchili.logging.configuration.LogContext;
import com.codingchili.logging.configuration.LogServerSettings;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.codingchili.core.configuration.CoreStrings.ID_TOKEN;

/**
 * @author Robin Duda
 * <p>
 * Base test cases for log handlers.
 */
@Ignore("Extend this class to run the test cases")
@RunWith(VertxUnitRunner.class)
public class SharedLogHandlerTest {
    private static final int MESSAGE_COUNT = 50;
    @Rule
    public Timeout timeout = new Timeout(50, TimeUnit.SECONDS);
    AbstractLogHandler handler;
    LogContext context;
    private TokenFactory factory;

    public SharedLogHandlerTest() {
        LogServerSettings settings = new LogServerSettings();
        Configurations.put(settings);
        SystemContext system = new SystemContext(Vertx.vertx());
        settings.setSecret(new byte[]{0x0});
        context = new LogContext(system);
        factory = new TokenFactory(settings.getSecret());
    }

    @Before
    public void setUp() {
        context.storage().clear(done -> {
        });
    }

    @After
    public void tearDown(TestContext test) {
        context.vertx().close(test.asyncAssertSuccess());
    }

    @Test
    public void logMessage(TestContext test) {
        Async async = test.async();

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            handle(Strings.PROTOCOL_LOGGING, (response, status) -> {
            }, messageWithToken());
        }

        context.periodic(() -> 20, "", event -> {
            context.storage().size(size -> {
                if (size.result() == MESSAGE_COUNT) {
                    context.cancel(event);
                    async.complete();
                }
            });
        });
    }

    void handle(String action, ResponseListener listener, JsonObject data) {
        handler.handle(RequestMock.get(action, listener, data));
    }

    private JsonObject messageWithToken() {
        return getLogMessage().put(ID_TOKEN, Serializer.json(new Token(factory, "domain")));
    }

    JsonObject getLogMessage() {
        return new ClientLogHandlerTest.LogMessageGenerator()
                .event("test-event", Level.WARNING).put("unique", UUID.randomUUID().toString());
    }

    class LogMessageGenerator extends DefaultLogger {
    }
}
