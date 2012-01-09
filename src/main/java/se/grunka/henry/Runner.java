package se.grunka.henry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import se.grunka.henry.log.LogFormatter;
import se.grunka.henry.processing.Content;
import se.grunka.henry.processing.ContentProcessor;
import se.grunka.henry.processing.PipelineProcessor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Runner {

    public static final Type CONTEXT_TYPE = new TypeToken<Map<String, Object>>() {}.getType();
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Object.class, new BasicTypesDeserializer()).create();
    public static final String SITE_CONTEXT_FILE = "_config.json";

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                //TODO parse files and args
                binder.bind(Configuration.class).toInstance(new Configuration());
                binder.bind(ContentProcessor.class).annotatedWith(Names.named("PipelineProcessor")).to(PipelineProcessor.class);
            }
        });
        replaceLoggingFormatter(injector.getInstance(LogFormatter.class));
        injector.getInstance(Runner.class).run();
    }

    private static void replaceLoggingFormatter(Formatter formatter) {
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setFormatter(formatter);
        }
    }

    private final Logger logger;
    private final PipelineProcessor processor;
    private final Configuration configuration;
    private final se.grunka.henry.plugin.Henry pluginContext;

    @Inject
    public Runner(Logger logger, PipelineProcessor processor, Configuration configuration, se.grunka.henry.plugin.Henry pluginContext) {
        this.logger = logger;
        this.processor = processor;
        this.configuration = configuration;
        this.pluginContext = pluginContext;
    }

    private void run() throws IOException {
        //processPlugins();
        File directory = new File("src/test/resources/site");
        Map<String, Object> siteContext = GSON.fromJson(new FileReader(new File(directory, SITE_CONTEXT_FILE)), CONTEXT_TYPE);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("site", siteContext);
        processDirectory(directory, context);
    }

    private void processPlugins() throws IOException {
        Context context = Context.enter();
        try {
            ScriptableObject scope = context.initStandardObjects();
            ScriptableObject.putProperty(scope, "Henry", Context.javaToJS(pluginContext, scope));
            File plugins = new File(configuration.getSiteDirectory(), Path.PLUGINS.toString());
            File plugin = new File(plugins, "plugin.js");
            context.evaluateReader(scope, new FileReader(plugin), plugin.getName(), 0, null);
            NativeObject globalContext = new NativeObject();
            NativeObject.putProperty(globalContext, "site", new NativeObject());
            pluginContext.getSiteCallback().call(context, scope, null, new Object[]{globalContext});
        } finally {
            Context.exit();
        }
    }

    private void processDirectory(File directory, Map<String, Object> context) throws IOException {
        for (File file : directory.listFiles()) {
            String name = file.getName();
            if (isSpecial(name)) {
                continue;
            }
            if (file.isDirectory()) {
                processDirectory(directory, context);
            } else {
                String text = IOUtils.toString(new FileReader(file));
                //TODO create default context with site object and such
                //TODO read _posts and create files for entries in there
                //TODO check the features of jekyll and see if there is anything else
                //TODO see if plugins could be handled in some way, maybe javascript using rhino
                //TODO output completed files
                Content content = processor.process(name, new Content(text, new HashMap<String, Object>(context)));
                logger.info("---");
                logger.info(content.getText());
            }
        }
    }

    private boolean isSpecial(String name) {
        return name.startsWith("_");
    }

}
