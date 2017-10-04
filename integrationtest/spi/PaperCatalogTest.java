package spi;

import org.daisy.braille.utils.api.paper.PaperCatalogService;
import org.daisy.braille.utils.api.paper.PaperCatalog;

import base.PaperCatalogTestbase;

@SuppressWarnings("javadoc")
public class PaperCatalogTest extends PaperCatalogTestbase {

	@Override
	public PaperCatalogService getPaperCS() {
		return PaperCatalog.newInstance();
	}
}