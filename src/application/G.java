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
	
	static void reset(int attempt) {
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
			finalScale = getRandomScale(rand) * 2.0;
		}
	}
	
	private static double getRandomScale(Random r) {
		switch (r.nextInt(3)) {
		case 0:
			return 0.5;
		case 1:
		default:
			return 1.0;
		case 2:
			return 2.0;
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
