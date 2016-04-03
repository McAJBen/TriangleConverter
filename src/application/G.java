package application;

import java.util.Random;

public class G {
	static boolean
		preDraw = true,
		postProcessing = true,
		display = true;
	static int 
		blocksWide = 10,
		maxTriangles = 2,
		samples = 1,
		threadCount = Runtime.getRuntime().availableProcessors(),
		randomPlacements = 625,
		repaintWait = 500,
		attempts = 3,
		randomBlocks = 100;
	static double
		scale = 1.0,
		postScale = 1.0;
	static boolean 
		blocksWideRandom = false,
		maxTrianglesRandom = false,
		samplesRandom = false,
		randomBlocksRandom = false;
	
	static void reset(int pixels) {
		Random rand = new Random();
		if (blocksWideRandom) {
			blocksWide = rand.nextInt(201 - threadCount) + threadCount;
		}
		if (maxTrianglesRandom) {
			maxTriangles = rand.nextInt(9) + 2;
		}
		if (samplesRandom) {
			samples = rand.nextInt(5) + 1;
		}
		if (randomBlocksRandom) {
			randomBlocks = rand.nextInt(blocksWide) * blocksWide;
		}
	}
	
	static String getTitle(int attempt) {
		return "Triangle Converter" +
        		" Wi:" + G.blocksWide + 
        		" Tr:" + G.maxTriangles + 
        		" Sa:" + G.samples + 
        		" Th:" + G.threadCount + 
        		" Sc:" + G.scale + 
        		" Ps:" + G.postScale + 
        		" At:" + attempt + "/" + G.attempts +
        		" RB:" + G.randomBlocks;
	}
}
