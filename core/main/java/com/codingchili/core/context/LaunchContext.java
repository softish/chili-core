package com.codingchili.core.context;

import com.codingchili.core.configuration.Environment;
import com.codingchili.core.configuration.exception.BlockNotConfiguredException;
import com.codingchili.core.configuration.exception.NoServicesConfiguredForBlock;
import com.codingchili.core.configuration.exception.RemoteBlockNotConfiguredException;
import com.codingchili.core.configuration.system.LauncherSettings;
import com.codingchili.core.files.Configurations;
import com.codingchili.core.logging.ConsoleLogger;
import com.codingchili.core.logging.Logger;

import java.util.List;
import java.util.Optional;

import static com.codingchili.core.configuration.CoreStrings.ID_DEFAULT;

/**
 * @author Robin Duda
 * <p>
 * Provides context for the Launcher system.
 */
public class LaunchContext {
    private static final String BLOCK_DEFAULT = "default";
    private String[] args = new String[]{};
    private Logger console = new ConsoleLogger();

    /**
     * @param args process arguments to create a launcher for.
     */
    public LaunchContext(String... args) {
        this.args = args;
    }

    public LauncherSettings settings() {
        return Configurations.launcher();
    }

    private boolean isRemoteBlock(String remote) {
        return settings().getHosts().containsKey(remote);
    }

    private boolean isBlockConfigured(String block) {
        return settings().getBlocks().containsKey(block);
    }

    private List<String> getBlockForRemote(String remote) throws RemoteBlockNotConfiguredException,
            BlockNotConfiguredException {
        String block = settings().getHosts().get(remote);

        if (isBlockConfigured(block)) {
            return getBlock(block);
        } else {
            throw new RemoteBlockNotConfiguredException(remote, block);
        }
    }

    private List<String> getBlock(String block) throws BlockNotConfiguredException {
        if (isBlockConfigured(block)) {
            return settings().getBlocks().get(block);
        } else if (block == null) {
            if (isBlockConfigured(ID_DEFAULT)) {
                return settings().getBlocks().get(ID_DEFAULT);
            } else {
                throw new BlockNotConfiguredException(ID_DEFAULT);
            }
        } else {
            throw new BlockNotConfiguredException(block);
        }
    }

    /**
     * Get the configured services for the given block or remote identifier.
     *
     * @param block the handler of the configured block or the hostname.
     * @return a list of configured services for the given block or host.
     * @throws RemoteBlockNotConfiguredException when no block is configured for given host.
     * @throws BlockNotConfiguredException       when no block is configured for given block-handler.
     */
    protected List<String> block(String block) throws CoreException {
        List<String> blocks;

        if (isRemoteBlock(block)) {
            blocks = getBlockForRemote(block);
        } else {
            blocks = getBlock(block);
        }

        if (blocks.size() == 0) {
            throw new NoServicesConfiguredForBlock(block);
        }

        return blocks;
    }

    public String[] args() {
        return args;
    }

    public List<String> block(String[] args) throws CoreException {
        return block((args.length == 0) ? findBlockByEnvironment() : args[0]);
    }

    /**
     * Attempts to identify a remote block to use by looking at the hostname and the
     * ip addresses available on the network interfaces.
     *
     * @return a chosen remote or local block.
     */
    private String findBlockByEnvironment() {
        Optional<String> hostname = Environment.hostname();

        if (hostname.isPresent() && isRemoteBlock(hostname.get())) {
            return hostname.get();
        } else {
            for (String address : Environment.addresses()) {
                if (isRemoteBlock(address)) {
                    return address;
                }
            }
        }
        return BLOCK_DEFAULT;
    }

    /**
     * @return the command that was passed to the launcher if existing.
     */
    public String command() {
        if (args.length > 0) {
            return args[0];
        } else {
            return BLOCK_DEFAULT;
        }
    }

    public Logger logger() {
        return console;
    }
}