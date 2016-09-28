package application;

import java.util.Random;

public class G {
	
	static final Random RANDOM = new Random();
	
	static boolean
		preDraw = true,
		postProcessing = true,
		display = true;
	static int 
		blocksWide = 10,
		blocksWideMin = 5,
		blocksWideMax = 100,
		triangles = 2,
		trianglesMin = 2,
		trianglesMax = 5,
		samples = 1,
		samplesMin = 1,
		samplesMax = 5,
		threadCount = Runtime.getRuntime().availableProcessors(),
		repaintWait = 500,
		attempts = 3,
		randomBlocks = 100;
	static double
		scale = 1.0,
		postScale = 1.0,
		finalScale = 1.0;
	static boolean 
		scaleRandom = false,
		postScaleRandom = false,
		randomBlocksRandom = false,
		postProcessingRandom = false,
		finalScaleRandom = false;
	
	static void reset() {
		Random rand = new Random();
		if (postProcessingRandom) {
			postProcessing = rand.nextBoolean();
		}
		blocksWide = rand.nextInt(blocksWideMax - blocksWideMin + 1) + blocksWideMin;
		triangles = rand.nextInt(trianglesMax - trianglesMin + 1) + trianglesMin;
		samples = rand.nextInt(samplesMax - samplesMin + 1) + samplesMin;
		if (randomBlocksRandom) {
			if (rand.nextBoolean()) {
				randomBlocks = rand.nextInt(blocksWide) * blocksWide;
			}
			else {
				randomBlocks = 0;
			}
		}
		if (scaleRandom) {
			scale = getRandomScale(rand);
		}
		if (postScaleRandom) {
			postScale = getRandomScale(rand);
		}
		if (finalScaleRandom) {
			finalScale = getRandomScale(rand);
		}
	}
	
	private static double getRandomScale(Random r) {
		switch (r.nextInt(12)) {
			default:
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				return 1.0;
			case 6:
			case 7:
				return 2.0;
			case 8:
			case 9:
				return 0.5;
			case 10:
				return 4.0;
			case 11:
				return 0.25;
		}
	}
	
	static String getTitle(int attempt) {
		return  " TC Wi:" + G.blocksWide +
        		" Tr:" + G.triangles +
        		" Sa:" + G.samples +
        		" Th:" + G.threadCount +
        		" At:" + attempt + "/" + G.attempts +
        		" Sc:" + G.scale +
        		" > "  + G.postScale +
        		" > "  + G.finalScale +
        		" RB:" + G.randomBlocks;
	}
}
