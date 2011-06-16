package org.daisy.dotify.formatter;

import org.daisy.dotify.formatter.utils.Expression;

public class DefaultVolumeTemplate implements VolumeTemplate {
	public final static String DEFAULT_VOLUME_VAR_NAME = "volume";
	public final static String DEFAULT_VOLUME_COUNT_VARIABLE_NAME = "volumes";
	private final String volumeVar, volumeCountVar, condition;
	private Iterable<VolumeSequence> preVolumeContent;
	private Iterable<VolumeSequence> postVolumeContent;
	
	public DefaultVolumeTemplate() {
		this(null, null, null);
	}

	public DefaultVolumeTemplate(String volumeVar, String volumeCountVar, String condition) {
		this.volumeVar = (volumeVar!=null?volumeVar:DEFAULT_VOLUME_VAR_NAME);
		this.volumeCountVar = (volumeCountVar!=null?volumeCountVar:DEFAULT_VOLUME_COUNT_VARIABLE_NAME);
		this.condition = condition;
	}

	public boolean appliesTo(int volume, int volumeCount) {
		if (condition==null) {
			return true;
		}
		return new Expression().evaluate(
				condition.replaceAll("\\$"+volumeVar+"(?=\\W)", ""+volume).replaceAll("\\$"+volumeCountVar+"(?=\\W)", ""+volumeCount)
			).equals(true);
	}
	
	public void setPreVolumeContent(Iterable<VolumeSequence> preVolumeContent) {
		this.preVolumeContent = preVolumeContent;
	}
	
	public void setPostVolumeContent(Iterable<VolumeSequence> postVolumeContent) {
		this.postVolumeContent = postVolumeContent;
	}

	public Iterable<VolumeSequence> getPreVolumeContent() {
		return preVolumeContent;
	}

	public Iterable<VolumeSequence> getPostVolumeContent() {
		return postVolumeContent;
	}

}
