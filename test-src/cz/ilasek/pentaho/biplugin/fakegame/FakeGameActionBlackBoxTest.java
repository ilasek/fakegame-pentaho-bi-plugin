package cz.ilasek.pentaho.biplugin.fakegame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPlatformPlugin;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.IPluginProvider;
import org.pentaho.platform.api.engine.IServiceManager;
import org.pentaho.platform.api.engine.ISolutionEngine;
import org.pentaho.platform.api.engine.PlatformPluginRegistrationException;
import org.pentaho.platform.api.engine.PluginBeanDefinition;
import org.pentaho.platform.api.engine.IPentahoDefinableObjectFactory.Scope;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.core.system.boot.PlatformInitializationException;
import org.pentaho.platform.engine.services.solution.SolutionEngine;
import org.pentaho.platform.engine.services.solution.SolutionHelper;
import org.pentaho.platform.plugin.services.pluginmgr.DefaultPluginManager;
import org.pentaho.platform.plugin.services.pluginmgr.PlatformPlugin;
import org.pentaho.platform.plugin.services.pluginmgr.PluginAdapter;
import org.pentaho.platform.plugin.services.pluginmgr.servicemgr.DefaultServiceManager;
import org.pentaho.platform.repository.solution.filebased.FileBasedSolutionRepository;
import org.pentaho.test.platform.engine.core.MicroPlatform;

import cz.ilasek.file.FileUtil;

public class FakeGameActionBlackBoxTest {
    private MicroPlatform booter;

    private StandaloneSession session;
    
    @Before
    public void setUp() throws Exception {
        booter = new MicroPlatform("solutions");
        booter.define(ISolutionEngine.class, SolutionEngine.class, Scope.GLOBAL);
        booter.define(ISolutionRepository.class, FileBasedSolutionRepository.class, Scope.GLOBAL);
        booter.define(IServiceManager.class, DefaultServiceManager.class, Scope.GLOBAL);
        booter.define(IPluginManager.class, DefaultPluginManager.class, Scope.GLOBAL);
        booter.define("systemStartupSession", StandaloneSession.class, Scope.GLOBAL);
        
        session = new StandaloneSession();
        
        LoadResultSetPreprocessor.setTest(true);
        booter.define(IPluginProvider.class, TestPluginProvider.class);
        booter.addLifecycleListener(new PluginAdapter());
        booter.start();        
    }
    
    @Test
    public void testClassTExcecution() throws PlatformInitializationException, FileNotFoundException
    {
        File refFile = new File("test-res/ref/testClassTRes.html");
        File output = new File("test-res/testClassTRes.html");
        OutputStream outputStream = new FileOutputStream(output);
        SolutionHelper.execute("testing Classification", "testuser", "bi-developers/FakeGame/tests/test-class-fake-game-t.xaction", new HashMap(), outputStream);
        compareClassOutputs(output, refFile);
    }
    
    @Test
    public void testClassWExcecution() throws PlatformInitializationException, FileNotFoundException
    {
        File refFile = new File("test-res/ref/testClassWRes.html");
        File output = new File("test-res/testClassWRes.html");
        OutputStream outputStream = new FileOutputStream(output);
        SolutionHelper.execute("testing Classification", "testuser", "bi-developers/FakeGame/tests/test-class-fake-game-w.xaction", new HashMap(), outputStream);
        compareClassOutputs(output, refFile);
    }    

    @Test
    public void testClassCExcecution() throws PlatformInitializationException, FileNotFoundException
    {
        File refFile = new File("test-res/ref/testClassCRes.html");
        File output = new File("test-res/testClassCRes.html");
        OutputStream outputStream = new FileOutputStream(output);
        SolutionHelper.execute("testing Classification", "testuser", "bi-developers/FakeGame/tests/test-class-fake-game-c.xaction", new HashMap(), outputStream);
        compareClassOutputs(output, refFile);
    }    
    
    @Test
    public void testRegTExcecution() throws PlatformInitializationException, FileNotFoundException
    {
        File refFile = new File("test-res/ref/testRegTRes.html");
        File output = new File("test-res/testRegTRes.html");
        OutputStream outputStream = new FileOutputStream(output);
        SolutionHelper.execute("testing Regression", "testuser", "bi-developers/FakeGame/tests/test-regr-fake-game-t.xaction", new HashMap(), outputStream);
        compareRegOutputs(output, refFile);
    }    
    
    @Test
    public void testRegWExcecution() throws PlatformInitializationException, FileNotFoundException
    {
        File refFile = new File("test-res/ref/testRegWRes.html");
        File output = new File("test-res/testRegWRes.html");
        OutputStream outputStream = new FileOutputStream(output);
        SolutionHelper.execute("testing Regression", "testuser", "bi-developers/FakeGame/tests/test-regr-fake-game-w.xaction", new HashMap(), outputStream);
        compareRegOutputs(output, refFile);
    }    

    @Test
    public void testRegCExcecution() throws PlatformInitializationException, FileNotFoundException
    {
        File refFile = new File("test-res/ref/testRegCRes.html");
        File output = new File("test-res/testRegCRes.html");
        OutputStream outputStream = new FileOutputStream(output);
        SolutionHelper.execute("testing Regression", "testuser", "bi-developers/FakeGame/tests/test-regr-fake-game-c.xaction", new HashMap(), outputStream);
        compareRegOutputs(output, refFile);
    }   
    
    private static void compareClassOutputs(File tested, File ref)
    {
        String testedContent = FileUtil.readFileIntoString(tested);
        String refContent = FileUtil.readFileIntoString(ref);
        
        Pattern setosaPat = Pattern.compile("setosa"); 
        
        Matcher testedMatcher = setosaPat.matcher(testedContent);
        Matcher refMatcher = setosaPat.matcher(refContent);

        while (testedMatcher.find())
            assertTrue(refMatcher.find());
        
        assertEquals(testedMatcher.find(), refMatcher.find());
    }
    
    private static void compareRegOutputs(File tested, File ref)
    {
        String testedContent = FileUtil.readFileIntoString(tested);
        String refContent = FileUtil.readFileIntoString(ref);
        
        Pattern waterPat = Pattern.compile("water"); 
        
        Matcher testedMatcher = waterPat.matcher(testedContent);
        Matcher refMatcher = waterPat.matcher(refContent);

        while (testedMatcher.find())
            assertTrue(refMatcher.find());
        
        assertEquals(testedMatcher.find(), refMatcher.find());
    }    
    
    public static class TestPluginProvider implements IPluginProvider {
        public List<IPlatformPlugin> getPlugins(IPentahoSession session)
                throws PlatformPluginRegistrationException {
            PlatformPlugin p = new PlatformPlugin();
            p.setId("test-fakeGameAction-plugin");
            p.addBean(new PluginBeanDefinition("FakeGameAction",
                    "cz.ilasek.pentaho.biplugin.fakegame.FakeGameAction"));
            return Arrays.asList((IPlatformPlugin) p);
        }
    }
}
