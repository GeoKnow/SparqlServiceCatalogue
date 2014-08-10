package org.aksw.servicecat.web.main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.servlet.ServletException;

import org.aksw.commons.util.StreamUtils;
import org.aksw.commons.util.slf4j.LoggerCount;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hibernate.mapping.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * 
 * 
 * http://stackoverflow.com/questions/10738816/deploying-a-servlet-
 * programmatically-with-jetty
 * http://stackoverflow.com/questions/3718221/add-resources
 * -to-jetty-programmatically
 * 
 * @author raven
 * 
 * 
 */
public class MainServiceCatServer {

    private static final Logger logger = LoggerFactory
            .getLogger(MainServiceCatServer.class);

    private static final Options cliOptions = new Options();

    static {
        cliOptions.addOption("P", "port", true, "");
    }

    public static void printClassPath() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            System.out.println(url.getFile());
        }
    }
    
    public static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public static void javascriptJsonLdTest() throws IOException, ScriptException {
        String json = StreamUtils.toString(resolver.getResource("/sd-test-data.json").getInputStream());

        String jsonLd = createSdJsonLd(json);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(gson.fromJson(jsonLd, Object.class)));
        //System.out.println(jsonLd);

        
        Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(jsonLd.getBytes()), "http://example.org/", "JSON-LD");
        model.write(System.out, "TURTLE");        
    }

    public static String createSdJsonLd(String json) throws ScriptException, IOException {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource jsonLdUtils = resolver.getResource("/json-ld-utils.js");
        Reader reader = new InputStreamReader(jsonLdUtils.getInputStream());
        //sdContext.

        String sdStaticSchema = StreamUtils.toString(resolver.getResource("/sd-jsonld-schema.js").getInputStream());
        sdStaticSchema = "(function() { return " + sdStaticSchema + " ; })();";
        
        String template = StreamUtils.toString(resolver.getResource("/sd-jsonld-gen.js").getInputStream());
        
        template = template + "()";
        
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        Bindings bindings = new SimpleBindings();
        
        bindings.put("templateStr", template);
        bindings.put("jsonStr", json);
        bindings.put("staticSchemaStr", sdStaticSchema);
        
        engine.eval(reader, bindings);
        String result = (String)bindings.get("jsonLdStr");

        return result;
    }

    // Source:
    // http://eclipsesource.com/blogs/2009/10/02/executable-wars-with-jetty/
    public static void main(String[] args) throws Exception {

        javascriptJsonLdTest();

        if (true) {
            return;
        }

        LoggerCount loggerCount = new LoggerCount(logger);

        // Class.forName("org.postgresql.Driver");

        CommandLineParser cliParser = new GnuParser();

        CommandLine commandLine = cliParser.parse(cliOptions, args);

        // AppConfig.cliDataSource = dataSource;

        // SparqlifyCliHelper.parseDataSource(commandLine, loggerCount);
        // DataSource dataSource =
        // SparqlifyCliHelper.parseDataSource(commandLine, logger);
        // Integer port = SparqlifyCliHelper.parseInt(commandLine, "P", false,
        // loggerCount);

        String portStr = commandLine.getOptionValue("P");
        Integer port = (StringUtils.isEmpty(portStr)) ? 7532 : Integer
                .parseInt(portStr);

        ProtectionDomain protectionDomain = MainServiceCatServer.class
                .getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        String externalForm = location.toExternalForm();

        logger.debug("External form: " + externalForm);

        // Try to detect whether we are being run from an
        // archive (uber jar / war) or just from compiled classes
        if (externalForm.endsWith("/classes/")) {
            externalForm = "src/main/webapp";
            // externalForm = "target/sparqlify-web-admin-server";
        }

        logger.debug("Loading webAppContext from " + externalForm);

        startServer(port, externalForm);
    }

    public static void startServer(int port, String externalForm) {

        Server server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });

        final WebAppContext webAppContext = new WebAppContext();
        // Context servletContext = webAppContext.getServletContext();

        webAppContext.addLifeCycleListener(new AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle arg0) {
                WebAppInitializer initializer = new WebAppInitializer();
                try {
                    initializer.onStartup(webAppContext.getServletContext());
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        webAppContext.setServer(server);
        webAppContext.setContextPath("/");

        // context.setDescriptor(externalForm + "/WEB-INF/web.xml");
        webAppContext.setWar(externalForm);

        server.setHandler(webAppContext);
        try {
            server.start();
            System.in.read();
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
