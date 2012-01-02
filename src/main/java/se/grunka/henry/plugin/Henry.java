package se.grunka.henry.plugin;

import com.google.inject.Inject;
import org.mozilla.javascript.Function;

import java.util.logging.Logger;

public class Henry {
    private final Logger logger;
    private Function siteCallback;
    private Function postCallback;

    @Inject
    public Henry(Logger logger) {
        this.logger = logger;
    }

    public void site(Function siteCallback) {
        this.siteCallback = siteCallback;
    }

    public void post(Function postCallback) {
        this.postCallback = postCallback;
    }

    public void log(String message) {
        logger.info(message);
    }


    public Function getSiteCallback() {
        return siteCallback;
    }

    public Function getPostCallback() {
        return postCallback;
    }
}
