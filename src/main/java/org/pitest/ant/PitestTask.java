package org.pitest.ant;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.pitest.mutationtest.MutationCoverageReport;

public class PitestTask  extends Task {

    private static final String[] REQUIRED_OPTIONS = {"targetClasses", "reportDir", "sourceDir"};
    private Map<String, String> options = new HashMap<String, String>();
    private String classpath;

    public void execute() throws BuildException {
        try{execute(new Java(this));} catch(Throwable t) {
            t.printStackTrace();
        }
    }

    void execute(Java java) {
        java.setClasspath(generateClasspath());
        java.setClassname(MutationCoverageReport.class.getCanonicalName());
        java.setFailonerror(true);
        java.setFork(true);

        checkRequiredOptions();
        for (Map.Entry<String, String> option: options.entrySet()) {
            java.createArg().setValue("--" + option.getKey() + "=" + option.getValue());
        }

        java.execute();
    }

    private void checkRequiredOptions() {
        for (String requiredOption : REQUIRED_OPTIONS) {
            if (optionMissing(requiredOption)) {
                throw new BuildException("You must specify the " + requiredOption + ".");
            }
        }
    }

    private boolean optionMissing(String option) {
        return !options.keySet().contains(option);
    }

    private Path generateClasspath() {
        if(classpath == null) {
            throw new BuildException("You must specify the classpath.");
        }

        Object reference = getProject().getReference(classpath);
        if (reference != null) {
            classpath = reference.toString();
        }

        return new Path(getProject(), classpath);
    }

    public void setReportDir(String value) {
        options.put("reportDir", value);
    }

    public void setInScopeClasses(String value) {
        options.put("inScopeClasses", value);
    }

    public void setTargetClasses(String value) {
        options.put("targetClasses", value);
    }

    public void setTargetTests(String value) {
        options.put("targetTests", value);
    }

    public void setDependencyDistance(String value) {
        options.put("dependencyDistance", value);
    }

    public void setThreads(String value) {
        options.put("threads", value);
    }

    public void setMutateStaticInits(String value) {
        options.put("mutateStaticInits", value);
    }

    public void setIncludeJarFiles(String value) {
        options.put("includeJarFiles", value);
    }

    public void setMutators(String value) {
        options.put("mutators", value);
    }

    public void setExcludedMethods(String value) {
        options.put("excludedMethods", value);
    }

    public void setExcludedClasses(String value) {
        options.put("excludedClasses", value);
    }

    public void setAvoidCallsTo(String value) {
        options.put("avoidCallsTo", value);
    }

    public void setVerbose(String value) {
        options.put("verbose", value);
    }

    public void setTimeoutFactor(String value) {
        options.put("timeoutFactor", value);
    }

    public void setTimeoutConst(String value) {
        options.put("timeoutConst", value);
    }

    public void setMaxMutationsPerClass(String value) {
        options.put("maxMutationsPerClass", value);
    }

    public void setJvmArgs(String value) {
        options.put("jvmArgs", value);
    }

    public void setOutputFormats(String value) {
        options.put("outputFormats", value);
    }

    public void setSourceDir(String value) {
        options.put("sourceDir", value);
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

}