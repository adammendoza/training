package victor.perf;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import victor.perf.ColorConsole.TextColor;

public class ThreadUtils {
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}		
	}
	
	private static Map<Thread, ColorConsole.TextColor> threadColors = new HashMap<>();
	
	static {
		threadColors.put(Thread.currentThread(), TextColor.BLACK);
	}
	
	public static void println(String message) {
		String threadName = Thread.currentThread().getName();
		String hourStr = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"));
		TextColor color = getTextColorForCurrentThread();
		ColorConsole.println(String.format("%s - %-30s - %s",
				hourStr,
				message,
				threadName)
			, color);
	}

	private static synchronized TextColor getTextColorForCurrentThread() {
		if (threadColors.get(Thread.currentThread())!=null) {
			return threadColors.get(Thread.currentThread());					
		}
		TextColor color = Stream.of(TextColor.values())
			.filter(c-> !threadColors.values().contains(c))
			.findFirst()
			.orElse(TextColor.BLACK);
		threadColors.put(Thread.currentThread(), color);
		return color;
	}

	
	private static final Map<String, Long> methodStartTime = new HashMap<>();
	
	public static void timerStart() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		String callerMethodName = stack[2].getMethodName();
		methodStartTime.put(callerMethodName, System.currentTimeMillis());
	}
	
	public static void timerEndPrint() {
		long t1 = System.currentTimeMillis();
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		String callerMethodName = stack[2].getMethodName();
		Long t0 = methodStartTime.get(callerMethodName);
		if (t0 == null) {
			throw new IllegalStateException("Should have called timerStart from this method before!");
		}
		println("Execution took " + String.format("%,d", t1-t0) + " ms");
	}
	
}
 

class ColorConsole {

	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
	private static final String ANSI_WHITE = "\u001B[37m";
	
	public enum TextColor {
		RED(ANSI_RED),
		GREEN(ANSI_GREEN),
		YELLOW(ANSI_YELLOW),
		BLUE(ANSI_BLUE),
		PURPLE(ANSI_PURPLE),
		CYAN(ANSI_CYAN),
		BLACK(ANSI_RESET);
		public final String ansiCode;

		private TextColor(String ansiCode) {
			this.ansiCode = ansiCode;
		}
	}
	
	public static void println(String message, TextColor color) {
		System.out.println(color.ansiCode + message + ANSI_RESET);
	}
}
