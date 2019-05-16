package de.fraunhofer.iao.querimonia.rest.restobjects.textominado;


import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * FrontendException is a RuntimeException, because it shouldn't be caught (excepted in the rest controllers)
 * it is used to give an own description for the exception which has to reach the frontend
 * if the rest controller catches the FrontendException, it has to send it as an attribute of the response. So a
 * response with payload and exception is possible.<br/>
 * If not, spring will handle it like every other exception and will return a internal server error
 *
 * @author Fabian
 *
 */
public final class FrontendException extends RuntimeException {

    private static final long serialVersionUID = 1L;


    public String name;
    public String description;

    private String stackTraceString;


    private FrontendException(String stacktrace, String name, String description) {

        this.stackTraceString = stacktrace;
        this.name = name;
        this.description = description;
    }

    private static FrontendException exceptionOf(String stacktrace, String name, String description) {
        return new FrontendException(stacktrace, name, description);
    }

    private static FrontendException exceptionOf(Throwable e, String name, String description) {
        return exceptionOf(stackTraceOf(e), name, description);
    }

    public static FrontendException GATE(Throwable e) {
        return exceptionOf(e, "GATE Exception", "Something went wrong while running GATE.");
    }

    public static FrontendException DATABASE(Throwable e) {
        return exceptionOf(e, "Database Exception", "Something went wrong while reading from database.");
    }

    public static FrontendException IO(Throwable e) {
        return exceptionOf(e, "IO Exception", "Something went wrong while reading or writing files on the server.");
    }

    public static FrontendException HEIDELTIME(Throwable e) {
        return exceptionOf(e, "Heideltime Exception", "Something went wrong with HeidelTime.");
    }

    public static FrontendException USER_MANAGEMENT(Throwable e) {
        return exceptionOf(e, "User Management Exception", "Something went wrong during user management.");
    }

    public static FrontendException PYTHON_EXCEPTION(String traceback) {
        return exceptionOf(traceback, "Python Integration Exception", "Something went wrong inside the python integration server while running your request.");
    }

    public static FrontendException OTHER(Throwable e) {
        if (e.getMessage() != null) {
            return exceptionOf(e, "Other Exception", e.getMessage());
        }

        return exceptionOf(e, "Other Exception", "Something went wrong, exception not specified.");
    }

    private static String stackTraceOf(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        return sw.toString();
    }

    public String getStackTraceString() {
        return stackTraceString;
    }
}
