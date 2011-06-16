package org.daisy.dotify.formatter;

public interface TocSequenceEvent extends VolumeSequence {
	enum TocRange {DOCUMENT, VOLUME};
	
	public String getTocName();
	public TocRange getRange();
	
	/**
	 * Returns true if this toc sequence applies to the supplied context
	 * @param volume
	 * @param volumeCount
	 * @return
	 */
	public boolean appliesTo(int volume, int volumeCount);
	
	/**
	 * Gets the TOC events 
	 * @param volume
	 * @param volumeCount
	 * @return
	 */
	public TocEvents getTocEvents(int volume, int volumeCount);	
	
}
