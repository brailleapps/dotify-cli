package org.daisy.dotify.hyphenator.latex;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.hyphenator.latex.rules.LatexRulesLocator;
import org.daisy.dotify.text.FilterLocale;

class LatexHyphenatorCore {
	private static LatexHyphenatorCore instance;
	private final Properties tables = new Properties();
	private final Map<String, net.davidashen.text.Hyphenator> map;
	private final Logger logger;
	
	private LatexHyphenatorCore() {
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		map = new HashMap<String, net.davidashen.text.Hyphenator>();
		try {
	        URL tablesURL = new LatexRulesLocator().getCatalogResourceURL();
	        if(tablesURL!=null){
	        	tables.loadFromXML(tablesURL.openStream());
	        } else {
	        	logger.warning("Cannot locate hyphenation tables");
	        }
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load table list.", e);
		}
	}
	
	synchronized static LatexHyphenatorCore getInstance() {
		if (instance == null) {
			instance = new LatexHyphenatorCore();
		}
		return instance;
	}
	
	boolean supportsLocale(FilterLocale locale) {
		return tables.getProperty(locale.toString())!=null;
	}
	
	private net.davidashen.text.Hyphenator loadHyphenator(String languageFileRelativePath) {
		net.davidashen.text.Hyphenator hyphenator = new net.davidashen.text.Hyphenator();
		try {
	        InputStream language = new LatexRulesLocator().getResource(languageFileRelativePath).openStream();
	        hyphenator.setErrorHandler(new HyphenatorErrorHandler(languageFileRelativePath));        
			hyphenator.loadTable(language);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load resource: " + languageFileRelativePath);
		}
        return hyphenator;
	}
	
	net.davidashen.text.Hyphenator getHyphenator(FilterLocale locale) throws UnsupportedLocaleException {
        String languageFileRelativePath = tables.getProperty(locale.toString());
        if(languageFileRelativePath==null) {
        	throw new UnsupportedLocaleException("Locale not supported: " + locale.toString());
        } else {
        	net.davidashen.text.Hyphenator hyph = map.get(languageFileRelativePath);
        	if (hyph == null) {
        		logger.fine("Loading hyphenation file: " + languageFileRelativePath);
        		hyph = loadHyphenator(languageFileRelativePath);
        		map.put(languageFileRelativePath, hyph);
        	}
        	return hyph;
        }
	}
	
}