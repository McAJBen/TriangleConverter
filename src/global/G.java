package global;

import java.util.Random;

public class G {
	
	private static final Random RANDOM = new Random();
	
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
	
	public static double getScale() {
		return scale;
	}
	
	public static double getPostScale() {
		return postScale;
	}
	
	public static double getFinalScale() {
		return finalScale;
	}
	
	public static boolean getDisplay() {
		return display;
	}
	
	public static boolean getPostProcessing() {
		return postProcessing;
	}
	
	public static int getMaxAttempts() {
		return attempts;
	}
	
	public static double getRandDouble() {
		return RANDOM.nextDouble();
	}
	
	public static int getRandInt(int i) {
		return RANDOM.nextInt(i);
	}
	
	public static void reset() {
		Random rand = new Random();
		if (postProcessingRandom) {
			postProcessing = rand.nextBoolean();
		}
		blocksWide = rand.nextInt(blocksWideMax - blocksWideMin + 1) + blocksWideMin;
		triangles = rand.nextInt(trianglesMax - trianglesMin + 1) + trianglesMin;
		samples = rand.nextInt(samplesMax - samplesMin + 1) + samplesMin;
		if (randomBlocksRandom) {
			if (rand.nextBoolean()) {
				blocksWide = rand.nextInt(getBlocksWide()) * getBlocksWide();
			}
			else {
				blocksWide = 0;
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
	
	public static String getTitle(int attempt) {
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
	
	public static String getShortTitle() {
		return  "_" + G.blocksWide +
        		"_" + G.triangles +
        		"_" + G.samples +
        		"_" + G.scale +
        		"_"  + G.postScale +
        		"_"  + G.finalScale +
        		"_" + G.randomBlocks;
	}

	public static int getThreadCount() {
		return threadCount;
	}
	
	public static int getMaxSamples() {
		return samples;
	}

	public static int getBlocksWide() {
		return blocksWide;
	}

	public static int getRandomBlocks() {
		return randomBlocks;
	}
	
	public static double getTotalScale() {
		return scale * postScale * finalScale;
	}
	
	public static int getTriangles() {
		return triangles;
	}
	
	public static boolean getPreDraw() {
		return preDraw;
	}
	
	public static int getPaintWait() {
		return repaintWait;
	}
}
