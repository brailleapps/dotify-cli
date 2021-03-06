package org.daisy.dotify.cli;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.daisy.dotify.api.factory.Factory;
import org.daisy.dotify.api.factory.FactoryCatalog;
import org.daisy.dotify.api.factory.FactoryProperties;
import org.daisy.dotify.api.table.BrailleConverter;
import org.daisy.dotify.api.table.TableCatalog;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMaker;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.api.translator.TranslatorSpecification;
import org.daisy.dotify.api.translator.TranslatorType;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandDetails;
import org.daisy.streamline.cli.CommandParser;
import org.daisy.streamline.cli.CommandParserResult;
import org.daisy.streamline.cli.Definition;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;
import org.daisy.streamline.cli.ShortFormResolver;
import org.daisy.streamline.cli.SwitchArgument;
import org.daisy.streamline.cli.SwitchMap;

public class TranslateCLI implements CommandDetails {
	private static final String DEFAULT_LOCALE = Locale.getDefault().toString().replaceAll("_", "-");
	private static final String DEFAULT_MODE = TranslatorType.UNCONTRACTED.toString(); 
	private static final String META_KEY = "meta";
	private static final String LOCALE_KEY = "locale";
	private static final String MODE_KEY = "mode";
	private static final String TABLE_KEY = "table";
	private static final String HELP_KEY = "help";
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;
	private final SwitchMap switches;
	private final ShortFormResolver tableSF;
	private final CommandParser parser;
//translate  --locale=en-US --mode=grade:1 
//translate --locale=da-dk --mode=contracted/8-dot
	public TranslateCLI() {
		this.reqArgs = new ArrayList<Argument>();
		TableCatalog tableCatalog = TableCatalog.newInstance();
		Collection<String> idents = new ArrayList<String>();
		for (FactoryProperties p : tableCatalog.list()) { idents.add(p.getIdentifier()); }
		tableSF = new ShortFormResolver(idents);
		Collection<TranslatorSpecification> tr = BrailleTranslatorFactoryMaker.newInstance().listSpecifications();
		List<Definition> translations = tr.stream()
			.filter(v->!v.getMode().equals(TranslatorType.BYPASS.toString()) && !v.getMode().equals(TranslatorType.PRE_TRANSLATED.toString()))
			.map(v->v.getLocale())
			.distinct()
			.sorted()
			.map(v->new Definition(v, 
					tr.stream()
					.filter(v2->v2.getLocale().equals(v) && !v2.getMode().equals(TranslatorType.BYPASS.toString()) && !v2.getMode().equals(TranslatorType.PRE_TRANSLATED.toString()))
					.map(v2->v2.getMode())
					.distinct()
					.sorted()
					.collect(Collectors.joining(", ", "Modes: ", ""))
			))
			.collect(Collectors.toList());
		this.optionalArgs = new ArrayList<OptionalArgument>();
		optionalArgs.add(new OptionalArgument(LOCALE_KEY, "Braille locale. Note that the default locale is based on system settings, not on available braille locales.", translations, DEFAULT_LOCALE));
		optionalArgs.add(new OptionalArgument(MODE_KEY, "Braille mode. For a list of modes, see the locale option.", DEFAULT_MODE));
		optionalArgs.add(new OptionalArgument(TABLE_KEY, "Preview table to use", getDefinitionList(tableCatalog, tableSF), "unicode_braille"));
		this.switches = new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('h', HELP_KEY, META_KEY, HELP_KEY, "Help text."))
				.build();
		this.parser = CommandParser.create(this);
	}
	
	public static void main(String[] args) throws IOException {
		TranslateCLI m = new TranslateCLI();
		CommandParserResult result = m.parser.parse(args);
		if (HELP_KEY.equals(result.getOptional().get(META_KEY))) {
			m.parser.displayHelp(System.out);
			ExitCode.OK.exitSystem();
		} else {
			m.runCLI(result);
		}
	}
	
	private void runCLI(CommandParserResult cmd) throws IOException {
		try {
			String locale = cmd.getOptional().get(LOCALE_KEY);
			if (locale==null || "".equals(locale)) {
				locale = DEFAULT_LOCALE;
			}
			String mode = cmd.getOptional().get(MODE_KEY);
			if (mode==null || "".equals(mode)) {
				mode = DEFAULT_MODE;
			}

			BrailleTranslator t = BrailleTranslatorFactoryMaker.newInstance().newTranslator(locale, mode);
			TableCatalog tc = TableCatalog.newInstance();
			String table = cmd.getOptional().get(TABLE_KEY);
			BrailleConverter bc = null;
			if (table!=null && !"".equals(table)) {
				bc = tc.newTable(tableSF.resolve(table)).newBrailleConverter();
			}
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(System.in));
			String text;
			while ((text=lnr.readLine())!=null) {
				try {
					if (bc!=null) {
						System.out.println(bc.toText(t.translate(Translatable.text(text).build()).getTranslatedRemainder()));
					} else {
						System.out.println(t.translate(Translatable.text(text).build()).getTranslatedRemainder());
					}
				} catch (TranslationException e) {
					e.printStackTrace();
				}
			}
		} catch (TranslatorConfigurationException e) {
			System.err.println("Cannot find a translator.");
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "Translates text on system in to braille on system out";
	}

	@Override
	public String getName() {
		return DotifyCLI.TRANSLATE;
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return optionalArgs;
	}

	@Override
	public List<Argument> getRequiredArguments() {
		return reqArgs;
	}

	
	/**
	 * Creates a list of definitions based on the contents of the supplied FactoryCatalog.
	 * @param catalog the catalog to create definitions for
	 * @param resolver 
	 * @return returns a list of definitions
	 */
	List<Definition> getDefinitionList(FactoryCatalog<? extends Factory> catalog, ShortFormResolver resolver) {
		List<Definition> ret = new ArrayList<Definition>();
		for (String key : resolver.getShortForms()) {
			ret.add(new Definition(key, catalog.get(resolver.resolve(key)).getDescription()));
		}
		return ret;
	}

	@Override
	public SwitchMap getSwitches() {
		return switches;
	}
}
