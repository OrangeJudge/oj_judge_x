package oj.judge.runner;

import oj.judge.common.Conf;
import oj.judge.common.Result;
import oj.judge.common.Solution;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Executor {
    private static final String label = "Executor::";

    private int language;
    private int timeLimit;
    private int memoryLimit;
    private Path exe;
    private Path stdin;
    private Path stdout;
    private Path stderr;
    private Path metricsFile;

    public Result.Verdict quickVerdict;

    public Executor(int a, int b, int c, Path d, Path e, Path f, Path g, Path h) {

        language = a;
        timeLimit = b;
        memoryLimit = c;
        exe = d;
        stdin = e;
        stdout = f;
        stderr = g;
        metricsFile = h;

        quickVerdict = Result.Verdict.NONE;

    }

    public Result.Verdict execute() {
        ProcessBuilder pb = getProcessBuilder(language);
        assert pb != null;

        if (Conf.debug()) System.out.print(label + "ProcessBuilder... ");
        if (Conf.debug()) for (String s : pb.command()) System.out.print(s + " ");
        if (Conf.debug()) System.out.println();

        Process p;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.Verdict.JE;
        }

        try {
            final Thread handle = Thread.currentThread();
            Thread watcher = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(timeLimit + Conf.maxExtraTime());
                        handle.interrupt();
                    } catch (InterruptedException ignored) {

                    }
                }
            };
            watcher.start();
            p.waitFor();
            watcher.interrupt();

        } catch (InterruptedException e) {
            if (Conf.debug()) System.out.println(label + "Runner interrupted due to timeout.");
            p.destroyForcibly();
            try {

                if (System.getProperty("os.name").contains("Windows")) {
                    Runtime.getRuntime().exec("taskkill /F /IM solution.exe");
                }
                else {

                }

            } catch (IOException e1) {
                e1.printStackTrace();
                return Result.Verdict.JE;
            }
            return Result.Verdict.TL;
        }

        return Result.Verdict.NONE;
    }

    private ProcessBuilder getProcessBuilder(int language) {
        String scriptPath = Conf.runScript().toAbsolutePath().toString();
        String suffixScript;
        String suffixExe;

        if (Conf.debug()) System.out.println(System.getProperty("os.name"));

        if (System.getProperty("os.name").contains("Windows")) {
            suffixScript = ".bat";
            suffixExe = ".exe";
        }
        else {
            suffixScript = ".sh";
            suffixExe = "";
        }

        switch (language) {
            case Solution.CPP11:
                exe = Paths.get(exe.toAbsolutePath().toString() + suffixExe);
                scriptPath = scriptPath + "/CPP" + suffixScript;
                break;
            case Solution.JAVA:
                exe = exe.getParent();
                memoryLimit += 20 * 1024;
                scriptPath = scriptPath + "/JAVA" + suffixScript;
                break;
            default:
                return null;
        }

        return new ProcessBuilder(
                scriptPath,
                exe.toString(),
                stdin.toAbsolutePath().toString(),
                stdout.toAbsolutePath().toString(),
                stderr.toAbsolutePath().toString(),
                metricsFile.toAbsolutePath().toString(),
                "" + timeLimit,
                "" + memoryLimit,
                "" + Conf.outputLimit(),
                "" + (timeLimit + Conf.maxExtraTime()),
                "" + Conf.maxMemory()
        );

//        EXE=$1
//        INFILE=$2
//        OUTFILE=$3
//        ERRORFILE=$4
//        METRICSFILE=$5
//        TIMELIMIT=$6
//        MEMORYLIMIT=$7
//        MAXFILE=$8
//        MAXTIME=$9
//        MAXMEMORY=$10

    }
}
