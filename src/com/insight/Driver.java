package com.insight;

import javax.swing.SwingUtilities;

public final class Driver {
	public static void main(String... args) {
		Database.get().dummy();
		SwingUtilities.invokeLater(Insight::new);
	}
}
