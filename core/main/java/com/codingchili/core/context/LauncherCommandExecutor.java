package com.codingchili.core.context;

import static com.codingchili.core.configuration.CoreStrings.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.codingchili.core.benchmarking.BenchmarkSuite;
import com.codingchili.core.configuration.CoreStrings;
import com.codingchili.core.files.Configurations;
import com.codingchili.core.logging.ConsoleLogger;
import com.codingchili.core.logging.Level;
import com.codingchili.core.logging.Logger;
import com.codingchili.core.security.AuthenticationGenerator;

/**
 * @author Robin Duda
 *         <p>
 *         Command executor that registers launch commands to a CommandExecutor.
 */
public class LauncherCommandExecutor extends CommandExecutor {

    /**
     * constructs an instance with the default logger
     */
    public LauncherCommandExecutor() {
        this(new ConsoleLogger());
    }

    /**
     * @param logger constructs an instance with specified logger
     */
    public LauncherCommandExecutor(Logger logger) {
        super(logger);
        registerCommands();
    }

    private void registerCommands() {
        AuthenticationGenerator generator = new AuthenticationGenerator(logger);
        BenchmarkSuite suite = new BenchmarkSuite();

        add(Configurations::reset, RECONFIGURE, getReconfigureDescription());
        add(generator::preshare, GENERATE_PRESHARED, getGeneratePresharedDescription());
        add(generator::secrets, GENERATE_SECRETS, getGenerateSecretsDescription());
        add(generator::tokens, GENERATE_TOKENS, getGenerateTokensDescription());
        add(generator::all, GENERATE, getGenerateAllDescription());
        add(suite::execute, BENCHMARK, getBenchmarkDescription());
        add(this::help, HELP, getCommandExecutorHelpDescription());
    }

    private void help() {
        int align = 24;

        for (String line : getCommandExecutorText()) {
            logger.log(line, Level.STARTUP);
        }

        for (Command command : commands.values()) {
            int space = align - command.getName().length();
            logger.log("\t\t" + command.getName() +
                    String.join("", Collections.nCopies(space, " ")) +
                    command.getDescription(), Level.STARTUP);
        }

        List<BlockRow> blocks = new ArrayList<>();
        logger.log("\n\t\t" + CoreStrings.CONFIGURED_BLOCKS + "\t\t" + getRemotesAvailable(), Level.WARNING);
        settings.getBlocks().keySet()
                .forEach(block -> {
                    BlockRow row = new BlockRow(block);
                    settings.getHosts().entrySet().stream()
                            .filter(entry -> entry.getValue().equals(block))
                            .map(Map.Entry::getKey)
                            .forEach(row.remotes::add);
                    blocks.add(row);
                });
        blocks.forEach(block -> {
            logger.log(block.toString(), Level.PURPLE);
        });
    }

    private class BlockRow {
        final List<String> remotes = new ArrayList<>();
        public final String block;

        BlockRow(String block) {
            this.block = block;
        }

        @Override
        public String toString() {
            int align = 36;
            String string = "\t\t" + block + String.join("",
                    Collections.nCopies(align - block.length(), " "));

            for (int i = 0; i < remotes.size(); i++) {
                if (i == 0)
                    string += "[";

                string += remotes.get(i);

                if (i < remotes.size() - 1)
                    string += ", ";
                if (i == remotes.size() - 1)
                    string += "]";
            }

            return string;
        }
    }
}
