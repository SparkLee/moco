package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;
import com.github.dreamhead.moco.runner.Runner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Function;

public final class WatcherFactory {
    private static Logger logger = LoggerFactory.getLogger(WatcherFactory.class);

    private FileWatcherFactory factory = new DefaultWatcherFactory();

    public ShutdownMocoRunnerWatcher createShutdownWatcher(final Runner runner,
                                                           final Integer shutdownPort,
                                                           final String shutdownKey) {
        return new ShutdownMocoRunnerWatcher(shutdownPort, shutdownKey, runner::stop);
    }

    public Watcher createConfigurationWatcher(final Iterable<File> files, final FileRunner fileRunner) {
        return factory.createWatcher(listener(fileRunner), Iterators.toArray(files.iterator(), File.class));
    }

    public Watcher createSettingWatcher(final File settingsFile,
                                        final Iterable<File> configurationFiles,
                                        final FileRunner fileRunner) {
        ImmutableList<File> files = ImmutableList.<File>builder().add(settingsFile).addAll(configurationFiles).build();
        return factory.createWatcher(listener(fileRunner), files.toArray(new File[0]));
    }

    private Function<File, Void> listener(final FileRunner fileRunner) {
        return file -> {
            logger.info("{} change detected.", file.getName());
            fileRunner.restart();
            return null;
        };
    }
}
