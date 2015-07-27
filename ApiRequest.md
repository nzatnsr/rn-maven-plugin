This is the specification of Java template and request definition files used for generating Java code derived from the **BaseApiRequest** class.

### Java Template File

The Java template file should contain a section of comments, such as following:

    ...
    public class ProxyApiRequest extends extends BaseApiRequest
    {
        ...
        // NOTE: The following are generated code. Please do not edit them manually!

        // @GeneratedCode

        // END of generated code
    }

The "**// @GeneratedCode**" string is important, as it will be replaced by generated code.

### Request Definition File

The request definition file is a text file consisting of the following instructions:

- Base URL prefix for requests to be defined, such as:

        BASEURL /proxies

- Request definition, which consists of the following columns in each line:

  1. Request type id, which is an integer uniquely defined for a request

  2. Request named, which is a string of unique request identifier.

  3. Request URL path, which is the path relative to the current base URL prefix

  4. Request method, such as GET, POST, PUT, DELETE, etc.

  5. Roles allowed to submit the request, which are the remaining of the line.

    The following is an example:

        1000 ListDependents   /guardians/{cloudName}/dependents  GET    ROOT ADMIN CSR GUI

- Any empty lines or lines started with a "#" character are ignored.

-EOF- $Id: ApiRequest.md,v 1.1.1.1 2015/07/27 05:16:15 zhang Exp $
