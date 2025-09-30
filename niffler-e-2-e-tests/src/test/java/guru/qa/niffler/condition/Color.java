package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Color {
	yellow("rgba(255, 183, 3, 1)"), green("rgba(53, 173, 123, 1)");

	public final String rgb;

	public static Color getFromRgba(String rgba) {
		for (Color color : Color.values()) {
			if (color.rgb.equals(rgba)) {
				return color;
			}
		}
		throw new IllegalArgumentException("Enum Color does not contain rgba " + rgba);
	}
}
