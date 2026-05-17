package io.github.luciegrillo.civitas.app.artifact;

import io.github.luciegrillo.civitas.core.Lattice;
import io.github.luciegrillo.civitas.core.Strategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * Renders strategy transitions using the Nowak-May qualitative palette.
 */
public final class SnapshotRenderer {
    static final int STABLE_COOPERATOR = 0x0000ff;
    static final int STABLE_DEFECTOR = 0xff0000;
    static final int BECAME_DEFECTOR = 0x00ff00;
    static final int BECAME_COOPERATOR = 0xffff00;

    private SnapshotRenderer() {
    }

    /**
     * Writes one nearest-neighbor scaled PNG.
     */
    public static void write(Path path, Lattice previous, Lattice current) throws IOException {
        if (previous.width() != current.width() || previous.height() != current.height()) {
            throw new IllegalArgumentException("snapshot dimensions do not match");
        }
        int scale = Math.max(1, Math.min(12, 800 / Math.max(current.width(), current.height())));
        BufferedImage image = new BufferedImage(
                current.width() * scale,
                current.height() * scale,
                BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < current.height(); y++) {
            for (int x = 0; x < current.width(); x++) {
                int color = color(previous.strategyAt(x, y), current.strategyAt(x, y));
                int startX = x * scale;
                int startY = y * scale;
                for (int dy = 0; dy < scale; dy++) {
                    for (int dx = 0; dx < scale; dx++) {
                        image.setRGB(startX + dx, startY + dy, color);
                    }
                }
            }
        }
        if (!ImageIO.write(image, "PNG", path.toFile())) {
            throw new IOException("no PNG writer is available");
        }
    }

    static int color(Strategy previous, Strategy current) {
        if (previous == Strategy.COOPERATE && current == Strategy.COOPERATE) {
            return STABLE_COOPERATOR;
        }
        if (previous == Strategy.DEFECT && current == Strategy.DEFECT) {
            return STABLE_DEFECTOR;
        }
        return current == Strategy.DEFECT ? BECAME_DEFECTOR : BECAME_COOPERATOR;
    }
}
