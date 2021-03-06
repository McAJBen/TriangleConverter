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
		LOAD_THREAD = "LoadingThread",
		USER_DIR = System.getProperty("user.dir"),
		FILE_ERROR = "ERROR: Could not read file" + SPACE,
		PNG = "png",
		DOT_PNG = "." + PNG,
		DOT_JPG = ".jpg",
		DOT_BMP = ".bmp",
		SETTINGS_FILE = "TriangleConverter.settings",
		NO_SETTINGS_FILE = "Settings File does not exist",
		AUTO = "AUTO";
	
	static boolean
		preDraw = true,
		preDrawOutline = false,
		preDrawShowBest = true,
		allowCollision = true,
		trueColor = false,
		transparentTriangles = true;
	static int 
		blocksWide = 10,
		triangles = 2,
		samples = 1,
		threadCount = Runtime.getRuntime().availableProcessors(),
		repaintWait = 250,
		attempts = 10,
		randomBlocks = 0,
		randomBlockMult = 0;
	static double
		scale = 0.5,
		postScale = 2.0;
	static boolean
		sequential = false;
	static int seqCount = 0;
	
	private static final Random RANDOM = new Random();
	
	public static void reset() {
		if (sequential) {
			Settings.reset(seqCount++);
			if (seqCount >= Settings.seqSize()) {
				seqCount = 0;
			}
		}
		randomBlocks = blocksWide * blocksWide * randomBlockMult;
	}
	
	public static double getRandDouble()	{ return RANDOM.nextDouble(); }
	public static float getRandFloat()		{ return RANDOM.nextFloat(); }
	public static int getRandInt(int i)		{ return RANDOM.nextInt(i); }
	public static boolean getPreDraw()		{ return preDraw; }
	public static boolean getPreDrawOutline(){return preDrawOutline; }
	public static boolean getPreDrawShowBest(){return preDrawShowBest; }
	public static boolean getAllowCollision(){return allowCollision; }
	public static boolean getTrueColor()	{ return trueColor; }
	public static boolean getTransparentTriangles(){return transparentTriangles; }
	public static int getBlocksWide()		{ return blocksWide; }
	public static int getTriangles()		{ return triangles; }
	public static int getMaxSamples()		{ return samples; }
	public static int getThreadCount()		{ return threadCount; }
	public static int getPaintWait()		{ return repaintWait; }
	public static int getMaxAttempts()		{ return attempts; }
	public static int getRandomBlocks()		{ return randomBlocks; }
	public static double getScale()			{ return scale; }
	public static double getPostScale()		{ return postScale; }
	public static double getTotalScale()	{ return scale * postScale; }
	
	public static String getTitle(int attempt) {
		return  "TC Wi:" + blocksWide +
        		" Tr:" + triangles +
        		" Sa:" + samples +
        		" Th:" + threadCount +
        		" At:" + attempt + "/" + attempts +
        		" Sc:" + scale +
        		" > "  + postScale +
        		" RB:" + randomBlocks +
        		(trueColor ? " TruCol" : " LinCol") + 
        		(transparentTriangles ? " Transp" : " Opaq");
	}
	
	public static String getShortTitle() {
		return  "_" + blocksWide +
        		"_" + triangles +
        		"_" + samples +
        		"_" + scale +
        		"_" + postScale +
        		"_" + randomBlocks +
        		"_" + (trueColor ? "T" : "F") +
        		(transparentTriangles ? "T" : "F");
	}
}