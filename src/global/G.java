package global;

import java.util.Random;

public class G {
	
	public static final String 
		TAB = "\t",
		BLANK = "",
		ORIGINAL = "Original",
		NEW = "New",
		SPACE = " ",
		RUN_TIME = SPACE + "Run Time:" + SPACE,
		END = SPACE + "End?:" + SPACE,
		FINDING_FILE = "Finding File ...",
		OUT_OF_MEMORY = "Out of memory, not able to display",
		PAINT_THREAD = "paintThread",
		USER_DIR = System.getProperty("user.dir"),
		FILE_ERROR = "ERROR: Could not read file" + SPACE,
		PNG = "png",
		DOT_PNG = "." + PNG,
		DOT_JPG = ".jpg",
		DOT_BMP = ".bmp",
		BK_SLASH = "\\",
		SETTINGS_FILE = "TriangleConverter.settings",
		NO_SETTINGS_FILE = "Settings File does not exist",
		LOWER_X = "x",
		UPPER_X = "X",
		AUTO = "AUTO";
	
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
