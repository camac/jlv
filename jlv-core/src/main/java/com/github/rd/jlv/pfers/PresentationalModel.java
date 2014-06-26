package com.github.rd.jlv.pfers;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class PresentationalModel {

	@JsonProperty(value = "img")
	private boolean levelAsImage;

	@JsonProperty(value = "font")
	private int fontSize;

	@JsonProperty(value = "items")
	private List<ModelItem> modelItems;

	public boolean isLevelAsImage() {
		return levelAsImage;
	}

	public void setLevelAsImage(boolean levelAsImage) {
		this.levelAsImage = levelAsImage;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public List<ModelItem> getModelItems() {
		return modelItems;
	}

	public void setModelItems(List<ModelItem> modelItems) {
		this.modelItems = modelItems;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fontSize;
		result = prime * result + (levelAsImage ? 1231 : 1237);
		result = prime * result + ((modelItems == null) ? 0 : modelItems.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PresentationalModel other = (PresentationalModel) obj;
		if (fontSize != other.fontSize) {
			return false;
		}
		if (levelAsImage != other.levelAsImage) {
			return false;
		}
		if (modelItems == null) {
			if (other.modelItems != null) {
				return false;
			}
		} else if (!modelItems.equals(other.modelItems)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PresentationalModel [levelAsImage=" + levelAsImage + ", fontSize=" + fontSize + ", modelItems="
				+ modelItems + "]";
	}

	public static class ModelItem {

		@JsonProperty(value = "level")
		private String levelName;

		@JsonProperty(value = "fcolor")
		private Rgb foreground;

		@JsonProperty(value = "bcolor")
		private Rgb background;

		public String getLevelName() {
			return levelName;
		}

		public void setLevelName(String levelName) {
			this.levelName = levelName;
		}

		public Rgb getForeground() {
			return foreground;
		}

		public void setForeground(Rgb foreground) {
			this.foreground = foreground;
		}

		public Rgb getBackground() {
			return background;
		}

		public void setBackground(Rgb background) {
			this.background = background;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((background == null) ? 0 : background.hashCode());
			result = prime * result + ((foreground == null) ? 0 : foreground.hashCode());
			result = prime * result + ((levelName == null) ? 0 : levelName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ModelItem other = (ModelItem) obj;
			if (background == null) {
				if (other.background != null) {
					return false;
				}
			} else if (!background.equals(other.background)) {
				return false;
			}
			if (foreground == null) {
				if (other.foreground != null) {
					return false;
				}
			} else if (!foreground.equals(other.foreground)) {
				return false;
			}
			if (levelName == null) {
				if (other.levelName != null) {
					return false;
				}
			} else if (!levelName.equals(other.levelName)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "ModelItem [levelName=" + levelName + ", foreground=" + foreground + ", background=" + background
					+ "]";
		}

		public static class Rgb {

			@JsonProperty(value = "r")
			private int red;

			@JsonProperty(value = "g")
			private int green;

			@JsonProperty(value = "b")
			private int blue;

			public Rgb(int red, int green, int blue) {
				this.red = red;
				this.green = green;
				this.blue = blue;
			}

			public Rgb() {
				// no code
			}

			public int getRed() {
				return red;
			}

			public void setRed(int red) {
				this.red = red;
			}

			public int getGreen() {
				return green;
			}

			public void setGreen(int green) {
				this.green = green;
			}

			public int getBlue() {
				return blue;
			}

			public void setBlue(int blue) {
				this.blue = blue;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + blue;
				result = prime * result + green;
				result = prime * result + red;
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (getClass() != obj.getClass()) {
					return false;
				}
				Rgb other = (Rgb) obj;
				if (blue != other.blue) {
					return false;
				}
				if (green != other.green) {
					return false;
				}
				if (red != other.red) {
					return false;
				}
				return true;
			}

			@Override
			public String toString() {
				return "Rgb [red=" + red + ", green=" + green + ", blue=" + blue + "]";
			}
		}
	}
}