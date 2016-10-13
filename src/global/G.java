package global;

import java.util.Random;

public class G {
	
	public static final String TAB = "\t";
	public static final String BLANK = "";
	public static final String ORIGINAL = "Original";
	public static final String NEW = "New";
	public static final String SPACE = " ";
	public static final String RUN_TIME = " Run Time: ";
	public static final String END = " End?: ";
	public static final String FINDING_FILE = "Finding File ...";
	public static final String OUT_OF_MEMORY = "Out of memory, not able to display :(";
	public static final String PAINT_THREAD = "paintThread";
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String FILE_ERROR = "ERROR: Could not read file ";
	public static final String PNG = "png";
	public static final String DOT_PNG = ".png";
	public static final String DOT_JPG = ".jpg";
	public static final String DOT_BMP = ".bmp";
	public static final String BK_SLASH = "\\";
	public static final String SETTINGS_FILE = "TriangleConverter.settings";
	public static final String NO_SETTINGS_FILE = "Settings File does not exist";
	
	
	
	static boolean
		preDraw = true,
		postProcessing = true,
		display = true,
		trueColor = false;
	static int 
		blocksWide = 10,
		triangles = 2,
		samples = 1,
		threadCount = Runtime.getRuntime().availableProcessors(),
		repaintWait = 500,
		attempts = 3,
		randomBlocks = 0,
		randomBlockMult = -1;
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
	
	private static final Random RANDOM = new Random();
	public static final String LOWER_X = "x";
	public static final String UPPER_X = "X";
	public static final String AUTO = "AUTO";
	
	
	public static void reset() {
		if (postProcessingRandom) {
			postProcessing = RANDOM.nextBoolean();
		}
		if (randomBlocksRandom) {
			if (RANDOM.nextBoolean()) {
				randomBlocks = RANDOM.nextInt(getBlocksWide()) * getBlocksWide() * 3;
			}
			else {
				randomBlocks = 0;
			}
		}
		else if (randomBlockMult > 0) {
			randomBlocks = blocksWide * blocksWide * randomBlockMult;
		}
		if (scaleRandom) {
			scale = getRandomScale();
		}
		if (postScaleRandom) {
			postScale = getRandomScalePos();
		}
		if (finalScaleRandom) {
			finalScale = getRandomScalePos();
		}
	}
	
	public static double getRandDouble()	{ return RANDOM.nextDouble(); }
	public static float getRandFloat()		{ return RANDOM.nextFloat(); }
	public static int getRandInt(int i)		{ return RANDOM.nextInt(i); }
	public static boolean getPreDraw()		{ return preDraw; }
	public static boolean getPostProcessing()	{ return postProcessing; }
	public static boolean getDisplay()		{ return display; }
	public static boolean getTrueColor()	{ return trueColor; }
	public static int getBlocksWide()		{ return blocksWide; }
	public static int getTriangles()		{ return triangles; }
	public static int getMaxSamples()		{ return samples; }
	public static int getThreadCount()		{ return threadCount; }
	public static int getPaintWait()		{ return repaintWait; }
	public static int getMaxAttempts()		{ return attempts; }
	public static int getRandomBlocks()		{ return randomBlocks; }
	public static double getScale()			{ return scale; }
	public static double getPostScale()		{ return postScale; }
	public static double getFinalScale()	{ return finalScale; }
	public static double getTotalScale()	{ return scale * postScale * finalScale; }
	
	public static String getTitle(int attempt) {
		return  "TC Wi:" + G.blocksWide +
        		" Tr:" + G.triangles +
        		" Sa:" + G.samples +
        		" Th:" + G.threadCount +
        		" At:" + attempt + "/" + G.attempts +
        		" Sc:" + G.scale +
        		" > "  + G.postScale +
        		" > "  + G.finalScale +
        		" RB:" + G.randomBlocks +
        		(G.trueColor ? " TruCol" : " LinCol");
	}
	
	public static String getShortTitle() {
		return  "_" + G.blocksWide +
        		"_" + G.triangles +
        		"_" + G.samples +
        		"_" + G.scale +
        		"_"  + G.postScale +
        		"_"  + G.finalScale +
        		"_" + G.randomBlocks +
        		"_" + (G.trueColor ? "T" : "F");
	}
	
	private static double getRandomScalePos() {
		switch (RANDOM.nextInt(9)) {
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
				return 4.0;
		}
	}
	
	private static double getRandomScale() {
		switch (RANDOM.nextInt(12)) {
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
}
