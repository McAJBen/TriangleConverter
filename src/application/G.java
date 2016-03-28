package application;

import java.util.Random;

public class G {
	static boolean
		preDraw = true,
		postProcessing = true;
	static int 
		blocksWide = 10,
		maxTriangles = 2,
		samples = 1,
		threadCount = Runtime.getRuntime().availableProcessors(),
		repaintWait = 500,
		attempts = 3;
	static double
		scale = 1.0,
		postScale = 1.0;
	static boolean 
		blocksWideRandom = false,
		maxTrianglesRandom = false,
		samplesRandom = false;
	
	static void reset(int pixels) {
		Random rand = new Random();
		int maxTriSamples = pixels / 50;
		do {
			if (blocksWideRandom) {
				blocksWide = rand.nextInt(201 - threadCount) + threadCount;
			}
			if (maxTrianglesRandom) {
				maxTriangles = rand.nextInt(9) + 2;
			}
			if (samplesRandom) {
				samples = rand.nextInt(5) + 1;
			}
		} while (samples * maxTriangles * blocksWide * blocksWide > maxTriSamples);
	}
}
