package org.daisy.dotify.formatter;

/**
 * <p>Leader is a data object used when separating two chunks of 
 * text within a single row.</p>
 * 
 * <p>The constructor is private, use Leader.Builder
 * to create new instances.</p>
 * 
 * @author Joel Håkansson, TPB
 */
public class Leader implements BlockContents {
	/**
	 * Alignment values for leaders
	 */
	public enum Alignment {
		/**
		 * Alignment LEFT signify that text should run to the right of the leader position
		 */
		LEFT,
		/**
		 * Alignment RIGHT signify that text should run to the left of the leader position
		 */
		RIGHT,
		/**
		 * Alignment CENTER signify that text is centered around the leader position
		 */
		CENTER
	};
	
	private final String pattern;
	private final Position position;
	private final Alignment align;

	/**
	 * The Builder is used when creating a Leader instance 
	 * @author Joel Håkansson, TPB
	 */
	public static class Builder {
		// optional
		private String pattern;
		private Alignment align;
		private Position pos;
		
		/**
		 * Create a new Builder
		 */
		public Builder() {
			this.pattern = " ";
			this.align = Alignment.LEFT;
			this.pos = new Position(0, false);
		}

		/**
		 * Set the Position for Leader instances created using this builder.
		 * For example, position "0" is at the beginning of a row and "100%" is
		 * at the very end of a row. 
		 * @param pos the Position for the leader
		 * @return returns the Builder
		 */
		public Builder position(Position pos) {
			this.pos = pos;
			return this;
		}
		
		/**
		 * Set the Alignment for Leader instances created using this builder. The alignment
		 * effects the placement of the text following the leader
		 * @param align the Alignment for the leader
		 * @return returns the Builder
		 */
		public Builder align(Alignment align) {
			this.align = align;
			return this;
		}
		
		/**
		 * Set the pattern for Leader instances created using this builder. 
		 * The pattern is used to fill up the space between the text preceding the
		 * leader and the text following it.
		 * @param pattern the pattern for the leader
		 * @return returns the Builder
		 */
		public Builder pattern(String pattern) {
			this.pattern = pattern;
			return this;
		}
		
		/**
		 * Build Leader using the current state of the Builder
		 * @return returns a new Leader instance
		 */
		public Leader build() {
			return new Leader(this);
		}
	}

	private Leader(Builder builder) {
		this.pattern = builder.pattern;
		this.position = builder.pos;
		this.align = builder.align;
	}
	
	/**
	 * Get the pattern for this Leader.
	 * The pattern is used to fill up the space between the text preceding the
	 * leader and the text following it.  
	 * @return returns the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Get the Position for this Leader.
	 * @return returns the Position
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Get the Alignment for this Leader. The alignment
	 * effects the placement of the text following the leader
	 * @return returns the Alignment
	 */
	public Alignment getAlignment() {
		return align;
	}

	public ContentType getContentType() {
		return ContentType.LEADER;
	}

}
