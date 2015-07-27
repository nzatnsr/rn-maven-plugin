This is the specification of Java template and error definition files used for generating Java code derived from the "ApiBaseError" class.

### Java Template File

The Java template file should contain a section of comments, such as following:

    ...
    import net.respectnetwork.api.common.ApiBaseError;
    ...
    public class ProxyApiError extends ApiBaseError
    {
        ...

        // NOTE: The following are generated code. Please do not edit them manually!

        // @GeneratedCode

        // END of generated code
    }

The "**// @GeneratedCode**" string is important, as it will be replaced by generated code.

### Error Definition File

The error definition file is a text file consisting of the following instructions:

- Error message langaue, such as:

        LANGUAGE en_US

- Base URL prefix for error message detail description, such as:

        ERRORURL https://www.respectnetwork.net/api/proxy

- Error message definition, which consists of the following columns in each line:

  1. Error code, which is a meaningful HTTP status code.

  2. Error id, which is a string of unique error identifier.

  3. Error text, which are the remaining of the line.

    The following is an example:

        400 ApiKeyIsBlank            API key is not specified or is blank

- Any empty lines or lines started with a "#" character are ignored.

-EOF- $Id: ApiError.md,v 1.1.1.1 2015/07/27 05:16:15 zhang Exp $
