package org.pitest.ant;

import static org.mockito.Mockito.*;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.mutationtest.MutationCoverageReport;

@RunWith(MockitoJUnitRunner.class)
public class PitestTaskTest {

    private PitestTask pitestTask;
    @Mock
    private Java java;
    @Mock
    private Argument arg;
    @Mock
    private Project project;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        when(java.createArg()).thenReturn(arg);

        pitestTask = new PitestTask();
        pitestTask.setClasspath("bin/");
        pitestTask.setTargetClasses("com.*");
        pitestTask.setReportDir("report/");
        pitestTask.setSourceDir("src/");
        pitestTask.setProject(project);
    }

    @Test
    public void testAllOptionsArePassedOnToTheJavaTask() throws Exception {
        pitestTask.setAvoidCallsTo("avoidCalls");
        pitestTask.setDependencyDistance("distance");
        pitestTask.setExcludedClasses("String");
        pitestTask.setExcludedMethods("toString");
        pitestTask.setIncludeJarFiles("includeJars");
        pitestTask.setInScopeClasses("MyClass");
        pitestTask.setJvmArgs("-Da=a");
        pitestTask.setMaxMutationsPerClass("10");
        pitestTask.setMutateStaticInits("true");
        pitestTask.setMutators("a,b");
        pitestTask.setOutputFormats("XML");
        pitestTask.setReportDir("report/");
        pitestTask.setTargetClasses("com.*");
        pitestTask.setTargetTests("Test*");
        pitestTask.setThreads("4");
        pitestTask.setTimeoutConst("100");
        pitestTask.setTimeoutFactor("1.20");
        pitestTask.setVerbose("true");

        pitestTask.execute(java);

        verify(arg).setValue("--avoidCallsTo=avoidCalls");
        verify(arg).setValue("--dependencyDistance=distance");
        verify(arg).setValue("--excludedClasses=String");
        verify(arg).setValue("--excludedMethods=toString");
        verify(arg).setValue("--includeJarFiles=includeJars");
        verify(arg).setValue("--inScopeClasses=MyClass");
        verify(arg).setValue("--jvmArgs=-Da=a");
        verify(arg).setValue("--maxMutationsPerClass=10");
        verify(arg).setValue("--mutateStaticInits=true");
        verify(arg).setValue("--mutators=a,b");
        verify(arg).setValue("--outputFormats=XML");
        verify(arg).setValue("--reportDir=report/");
        verify(arg).setValue("--targetClasses=com.*");
        verify(arg).setValue("--targetTests=Test*");
        verify(arg).setValue("--threads=4");
        verify(arg).setValue("--timeoutConst=100");
        verify(arg).setValue("--timeoutFactor=1.20");
        verify(arg).setValue("--verbose=true");
    }

    @Test
    public void testOnlyTheSpecifiedOptionsArePassed() throws Exception {
        pitestTask.setVerbose("true");

        pitestTask.execute(java);

        verify(arg).setValue("--verbose=true");
        verify(arg).setValue("--targetClasses=com.*");
        verify(arg).setValue("--reportDir=report/");
        verify(arg).setValue("--sourceDir=src/");
        verifyNoMoreInteractions(arg);
    }

    @Test
    public void testJavaTaskFailsOnError() throws Exception {
        pitestTask.execute(java);

        verify(java).setFailonerror(true);
    }

    @Test
    public void testJavaTaskHasForkOnTrue() throws Exception {
        pitestTask.execute(java);

        verify(java).setFork(true);
    }

    @Test
    public void testJavaTaskIsExecuted() throws Exception {
        pitestTask.execute(java);

        verify(java).execute();
    }

    @Test
    public void testItExcecutesPitMainClass() throws Exception {
        pitestTask.execute(java);

        verify(java).setClassname(MutationCoverageReport.class.getCanonicalName());
    }

    @Test
    public void testItFailsIfThereIsNoClasspath() throws Exception {
        exception.expect(BuildException.class);
        exception.expectMessage("You must specify the classpath.");

        pitestTask = new PitestTask();
        pitestTask.execute(java);
    }

    @Test
    public void testItFailsIfTargetClassesIsNotSpecified() throws Exception {
        exception.expect(BuildException.class);
        exception.expectMessage("You must specify the targetClasses.");

        pitestTask = new PitestTask();
        pitestTask.setClasspath("bin/");
        pitestTask.setProject(project);

        pitestTask.execute(java);
    }

    @Test
    public void testItFailsIfReportDirIsNotSpecified() throws Exception {
        exception.expect(BuildException.class);
        exception.expectMessage("You must specify the reportDir.");

        pitestTask = new PitestTask();
        pitestTask.setClasspath("bin/");
        pitestTask.setProject(project);
        pitestTask.setTargetClasses("com.*");

        pitestTask.execute(java);
    }

    @Test
    public void testItFailsIfSourceDirIsNotSpecified() throws Exception {
        exception.expect(BuildException.class);
        exception.expectMessage("You must specify the sourceDir.");

        pitestTask = new PitestTask();
        pitestTask.setClasspath("bin/");
        pitestTask.setProject(project);
        pitestTask.setTargetClasses("com.*");
        pitestTask.setReportDir("report/");

        pitestTask.execute(java);
    }


    @Test
    public void testClasspathIsSetToJavaTask() throws Exception {
        String classpath = "bin/;lib/util.jar";
        pitestTask.setClasspath(classpath);
        pitestTask.execute(java);

        verify(java).setClasspath(argThat(new PathMatcher(classpath)));
    }

    @Test
    public void testClasspathAntReferenceIsSetToJavaTask() throws Exception {
        String classpath = "app.classpath";
        Object reference = "antReference";
        when(project.getReference(classpath)).thenReturn(reference);

        pitestTask.setClasspath(classpath);
        pitestTask.execute(java);

        verify(java).setClasspath(argThat(new PathMatcher(reference.toString())));
    }

    private static class PathMatcher extends ArgumentMatcher<Path> {

        private static final String PATH_SEPARATOR = ";";
        private final String[] expectedPaths;

        public PathMatcher(String path) {
            this.expectedPaths = path.split(PATH_SEPARATOR);
        }

        @Override
        public boolean matches(Object argument) {
            Path argPath = (Path) argument;
            String[] paths = argPath.toString().split(PATH_SEPARATOR);
            boolean matches = paths.length == expectedPaths.length;
            if (matches) {
                for (String expectedPathElement : expectedPaths) {
                    if (isNotPresent(paths, expectedPathElement)) {
                        return false;
                    }
                }
            }
            return matches;
        }

        private boolean isNotPresent(String[] paths, String expectedPathElement) {
            String element = normalizePath(expectedPathElement);
            for (String pathElement : paths) {
                if (pathElement.endsWith(element)) {
                    return false;
                }
            }
            return true;
        }

        private String normalizePath(String expectedPathElement) {
            String element = expectedPathElement;
            element = element.replace("/" ,File.separator );
            element = element.replace("\\" ,File.separator );
            if (element.endsWith(File.separator)) {
                element = element.substring(0, element.length() - 1);
            }
            return element;
        }
     }
}
