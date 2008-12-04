/**
 * Logback: the generic, reliable, fast and flexible logging framework.
 * 
 * Copyright (C) 2000-2008, QOS.ch
 * 
 * This library is free software, you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation.
 */

package ch.qos.logback.classic.multiJVM;

import org.slf4j.Logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.testUtil.RandomUtil;

public class FileAppenderPerf {
  static String msgLong = "ABCDEGHIJKLMNOPQRSTUVWXYZabcdeghijklmnopqrstuvwxyz1234567890";

  static long LEN = 100 * 1000;
  static int DIFF = RandomUtil.getPositiveInt() % 1000;
  static String FILENAME;

  static LoggerContext buildLoggerContext(String filename, boolean safetyMode) {
    LoggerContext loggerContext = new LoggerContext();

    FileAppender<LoggingEvent> fa = new FileAppender<LoggingEvent>();

    PatternLayout patternLayout = new PatternLayout();
    patternLayout.setPattern("%5p %c - %m%n");
    patternLayout.setContext(loggerContext);
    patternLayout.start();

    fa.setLayout(patternLayout);
    fa.setFile(filename);
    fa.setAppend(false);
    fa.setImmediateFlush(true);
    fa.setBufferedIO(false);
    fa.setPrudent(safetyMode);
    fa.setContext(loggerContext);
    fa.start();

    ch.qos.logback.classic.Logger root = loggerContext
        .getLogger(LoggerContext.ROOT_NAME);
    root.addAppender(fa);

    return loggerContext;
  }

  static void usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java " + FileAppenderPerf.class.getName()
        + " filename");

    System.exit(1);
  }

  public static void main(String[] argv) throws Exception {
    if (argv.length > 1) {
      usage("Wrong number of arguments.");
    }

    if (argv.length == 0) {
      FILENAME = DIFF+"";
    } else {
      FILENAME = argv[0];
    }

    perfCase(false);
    perfCase(true);
  }

  static void perfCase(boolean safetyMode) throws Exception {
    LoggerContext lc = buildLoggerContext(FILENAME + "-" + safetyMode + ".log",
        safetyMode);
    Logger logger = lc.getLogger(FileAppenderPerf.class);

    long start = System.nanoTime();
    for (int i = 0; i < LEN; i++) {
      logger.debug(msgLong + " " + i);
    }
    // in microseconds
    double durationPerLog = (System.nanoTime() - start) / (LEN * 1000.0);

    lc.stop();

    System.out.println("Average duration of " + (durationPerLog)
        + " microseconds per log. Prudent mode=" + safetyMode);
    System.out.println("------------------------------------------------");
  }

}