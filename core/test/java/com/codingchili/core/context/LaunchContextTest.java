package com.codingchili.core.context;

import com.codingchili.core.configuration.Environment;
import com.codingchili.core.configuration.exception.BlockNotConfiguredException;
import com.codingchili.core.configuration.exception.NoServicesConfiguredForBlock;
import com.codingchili.core.configuration.exception.RemoteBlockNotConfiguredException;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.codingchili.core.configuration.CoreStrings.getNoServicesConfiguredForBlock;
import static com.codingchili.core.context.LaunchContextMock.*;

/**
 * @author Robin Duda
 * <p>
 * Tests for the launcher context.
 */
@RunWith(VertxUnitRunner.class)
public class LaunchContextTest {
    private LaunchContext context;

    @Before
    public void setUp() {
        this.context = new LaunchContextMock(new String[]{});
    }

    @Test
    public void testGetBlock() throws CoreException {
        Assert.assertTrue(context.block(BLOCK_1).contains(SERVICE_1));
        Assert.assertTrue(context.block(BLOCK_2).contains(SERVICE_2));
    }

    @Test
    public void testGetHostBlock() throws CoreException {
        Assert.assertTrue(context.block(HOST_1).contains(SERVICE_1));
        Assert.assertTrue(context.block(HOST_2).contains(SERVICE_1));
    }

    @Test
    public void testGetMissingBlock() throws CoreException {
        try {
            Assert.assertTrue(context.block(BLOCK_NULL).isEmpty());
        } catch (BlockNotConfiguredException e) {
            Assert.assertTrue(e.getMessage().contains(BLOCK_NULL));
        }
    }

    @Test
    public void testGetMissingHost() throws CoreException {
        try {
            context.block(HOST_3).contains(SERVICE_1);
        } catch (RemoteBlockNotConfiguredException e) {
            Assert.assertTrue(e.getMessage().contains(HOST_3));
            Assert.assertTrue(e.getMessage().contains(BLOCK_NULL));
        }
    }

    @Test
    public void testGetEmptyBlockThrows(TestContext test) throws CoreException {
        try {
            context.block(BLOCK_EMPTY);
            test.fail("Should throw exception when block is empty.");
        } catch (NoServicesConfiguredForBlock e) {
            test.assertEquals(getNoServicesConfiguredForBlock(BLOCK_EMPTY), e.getMessage());
        }
    }

    @Test
    public void testGetDefaultBlockWhenNoArgs(TestContext test) throws CoreException {
        try {
            context.block(new String[]{});
        } catch (NoServicesConfiguredForBlock e) {
            test.assertTrue(e.getMessage().contains(BLOCK_DEFAULT));
        }
    }

    @Test
    public void testGetRemoteBlockByHostname(TestContext test) throws CoreException {
        if (Environment.hostname().isPresent()) {
            test.assertTrue(context.block(Environment.hostname().get()).contains(SERVICE_2));
        }
    }

    @Test
    public void testGetRemoteBlockByIP(TestContext test) throws CoreException {
        if (Environment.addresses().size() > 0) {
            test.assertTrue(context.block(Environment.addresses().get(0)).contains(SERVICE_2));
        }
    }
}
