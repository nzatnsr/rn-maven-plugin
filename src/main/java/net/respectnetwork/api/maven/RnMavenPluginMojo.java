/*
 * Copyright (c) 2015 Respect Network Corp. All Rights Reserved.
 *
 * $Id: RnMavenPluginMojo.java,v 1.1.1.1 2015/07/27 05:16:15 zhang Exp $
 */
package net.respectnetwork.api.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.*;

/**
 * The <code>RnMavenPlugin</code> class defines various tasks that may be executed
 * in the building process of Respect Network API code base
 *
 * @author Ning Zhang ning@respectnetwork.net
 * @version $Revision: 1.1.1.1 $
 */
@Mojo( name = "rn-maven-plugin", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class RnMavenPluginMojo extends AbstractMojo
{
	public static final String ERROR_ACTION_UNKNOWN     = "Unknwon action";
	public static final String ERROR_ACTION_UNSPECIFIED = "Action unspecified";
	public static final String ERROR_API_ERROR_USAGE    = "ApiError must follow with additional 3 arguments: template error.txt target";
	public static final String ERROR_API_ERROR_FAILURE  = "ApiError code generation failed";

	public static final String ERROR_API_REQUEST_USAGE  = "ApiRequest follow with additional 3 arguments: template error.txt target";
	public static final String ERROR_API_REQUEST_FAILURE= "ApiRequest code generation failed";

	public static final String ACTION_API_ERROR         = "ApiError";
	public static final String ACTION_API_REQUEST       = "ApiRequest";

	@Parameter
	private String[] args;

	public String[] getArgs()
	{
		return this.args;
	}

	public void setArgs( String[] args )
	{
		this.args = args;
	}

	public void execute() throws MojoExecutionException
	{
		if( args == null )
		{
			getLog().error("args = null");
			throw new MojoExecutionException(ERROR_ACTION_UNSPECIFIED);
		}
		if( args.length == 0 )
		{
			getLog().error("args.length = 0");
			throw new MojoExecutionException(ERROR_ACTION_UNSPECIFIED);
		}
		for( int i = 0; i < args.length; i++ )
		{
			getLog().debug("args[" + i + "] = " + args[i]);
		}
		String action = args[0];
		if( action.equalsIgnoreCase(ACTION_API_ERROR) == true )
		{
			generateApiErrorCode(args);
		}
		else if( action.equalsIgnoreCase(ACTION_API_REQUEST) == true )
		{
			generateApiRequestCode(args);
		}
		else
		{
			throw new MojoExecutionException(ERROR_ACTION_UNKNOWN + " - " + action);
		}
	}

	private void generateApiErrorCode( String[] args ) throws MojoExecutionException
	{
		if( args.length != 4 )
		{
			throw new MojoExecutionException(ERROR_API_ERROR_USAGE);
		}

		File srcFile = null;
		File errFile = null;
		File dstFile = null;

		try
		{
			srcFile = new File(args[1]);
			errFile = new File(args[2]);
			dstFile = new File(args[3]);

			getLog().info("ApiError Template File   : " + srcFile.getAbsolutePath());
			getLog().info("ApiError Definition File : " + errFile.getAbsolutePath());
			getLog().info("ApiError Generated File  : " + dstFile.getAbsolutePath());

			BufferedReader srcFileInp = new BufferedReader(new FileReader(srcFile));
			BufferedReader errFileInp = new BufferedReader(new FileReader(errFile));
			PrintWriter    dstFileOut = new PrintWriter(dstFile);

			String line;
			String className = "ApiError";

			while( (line = srcFileInp.readLine()) != null )
			{
				if( "// @GeneratedCode".equals(line.trim()) == false )
				{
					dstFileOut.println(line);
					String[] tokens = line.split("[ |\t]+");
					if( (tokens != null) && (tokens.length >= 3) && ("public".equals(tokens[0]) == true) && ("class".equals(tokens[1]) == true) )
					{
						className = tokens[2];
					}
				}
				else
				{
					generateApiErrorCode(errFileInp, dstFileOut, className);
				}
			}
			srcFileInp.close();
			errFileInp.close();
			dstFileOut.close();
		}
		catch( Exception ex )
		{
			try
			{
				if( dstFile != null )
				{
					getLog().info("delete file - " + args[3]);
					dstFile.delete();
				}
			}
			catch( Exception ex1 )
			{
			}
			throw new MojoExecutionException("ApiError code generation failed", ex);
		}
		getLog().info("ApiError code genration successful - " + args[3]);
	}

	private void generateApiErrorCode( BufferedReader errFileInp, PrintWriter dstFileOut, String className ) throws Exception
	{
		getLog().info("ApiError Generated Class : " + className);

		dstFileOut.println("\tstatic");
		dstFileOut.println("\t{");
		dstFileOut.println("\t\t" + className + " err;");
		dstFileOut.println();
		
		int    i;
		String line;

		String language = "en_US";
		String errorurl = "https://www.respectnetwork.net/api/error/";
		StringBuffer ids = new StringBuffer();

		while( (line = errFileInp.readLine()) != null )
		{
			line = line.trim();
			if( line.length() == 0 )
			{
				continue;
			}
			if( line.charAt(0) == '#' )
			{
				continue;
			}
			String[] tokens = line.split("( |\t)+");
			if( tokens == null )
			{
				continue;
			}
			if( tokens.length == 0 )
			{
				continue;
			}
			if( "LANGUAGE".equals(tokens[0]) == true )
			{
				if( tokens.length != 2 )
				{
					getLog().warn("Skip invalid LANGUAGE statement - " + line);
					continue;
				}
				language = tokens[1];
			}
			else if( "ERRORURL".equals(tokens[0]) == true )
			{
				
				if( tokens.length != 2 )
				{
					getLog().warn("Skip invalid ERRORURL statement - " + line);
					continue;
				}
				errorurl = tokens[1];
			}
			else if( tokens.length >= 3 )
			{
				int httpCode = -1;
				try
				{
					httpCode = Integer.parseInt(tokens[0]);
				}
				catch( Exception e )
				{
					httpCode = -1;
				}
				if( httpCode <= 0 )
				{
					getLog().warn("Skip invalid error statement - " + line);
					continue;
				}
				String  id = "";
				boolean addUnderScore = false;

				for( i = 0; i < tokens[1].length(); i++ )
				{
					char c = tokens[1].charAt(i);
					if( c == ':' )
					{
						c = '_';
						addUnderScore = false;
					}
					else
					{
						if( (c >= 'A') && (c <= 'Z') )
						{
							if( addUnderScore == true )
							{
								id = id + '_';
							}
						}
						addUnderScore = true;
					}
					id = id + c;
				}
				if( id.length() == 0 )
				{
					getLog().warn("Skip invalid error statement - " + line);
					continue;
				}
				ids.append(String.format("\tpublic static final String %-64s = \"%s\";\n", id.toUpperCase(), tokens[1]));

				dstFileOut.println("\t\terr = new " + className + "();");
				dstFileOut.println("\t\terr.setHttpStatusCode(" + httpCode + ");");
				dstFileOut.println("\t\terr.setLanguageId(\"" + language + "\");");
				dstFileOut.println("\t\terr.setErrorId(\"" + tokens[1] + "\");");
				StringBuffer s = new StringBuffer(tokens[2]);
				for( i = 3; i < tokens.length; i++ )
				{
					s.append(" ");
					s.append(tokens[i]);
				}
				dstFileOut.println("\t\terr.setReason(\"" + s.toString() + "\");");
				dstFileOut.println("\t\terr.setErrorLink(\"" + errorurl + language + "/" + tokens[1] + ".html\");");
				dstFileOut.println("\t\terrorMap.put(\"" + language + ":" + tokens[1] + "\", err);");
				dstFileOut.println();
                        }
			else
			{
				getLog().warn("Skip invalid statement - " + line);
			}
		}

		dstFileOut.println("\t}");
		dstFileOut.println();
		dstFileOut.print(ids.toString());
	}

	private void generateApiRequestCode( String[] args ) throws MojoExecutionException
	{
		if( args.length != 4 )
		{
			throw new MojoExecutionException(ERROR_API_REQUEST_USAGE);
		}

		File srcFile = null;
		File reqFile = null;
		File dstFile = null;

		try
		{
			srcFile = new File(args[1]);
			reqFile = new File(args[2]);
			dstFile = new File(args[3]);

			getLog().info("ApiRequest Template File   : " + srcFile.getAbsolutePath());
			getLog().info("ApiRequest Definition File : " + reqFile.getAbsolutePath());
			getLog().info("ApiRequest Generated File  : " + dstFile.getAbsolutePath());

			BufferedReader srcFileInp = new BufferedReader(new FileReader(srcFile));
			BufferedReader reqFileInp = new BufferedReader(new FileReader(reqFile));
			PrintWriter    dstFileOut = new PrintWriter(dstFile);

			String line;
			String className = "ApiRequest";

			while( (line = srcFileInp.readLine()) != null )
			{
				if( "// @GeneratedCode".equals(line.trim()) == false )
				{
					dstFileOut.println(line);
					String[] tokens = line.split("[ |\t]+");
					if( (tokens != null) && (tokens.length >= 3) && ("public".equals(tokens[0]) == true) && ("class".equals(tokens[1]) == true) )
					{
						className = tokens[2];
					}
				}
				else
				{
					generateApiRequestCode(reqFileInp, dstFileOut, className);
				}
			}
			srcFileInp.close();
			reqFileInp.close();
			dstFileOut.close();
		}
		catch( Exception ex )
		{
			try
			{
				if( dstFile != null )
				{
					getLog().info("delete file - " + args[3]);
					dstFile.delete();
				}
			}
			catch( Exception ex1 )
			{
			}
			throw new MojoExecutionException("ApiRequest code generation failed", ex);
		}
		getLog().info("ApiRequest code genration successful - " + args[3]);
	}

	private void generateApiRequestCode( BufferedReader reqFileInp, PrintWriter dstFileOut, String className ) throws Exception
	{
		getLog().info("ApiRequest Generated Class : " + className);

		dstFileOut.println("\tstatic");
		dstFileOut.println("\t{");
		dstFileOut.println("\t\tcontrolMap = new HashMap<String, " + className + ">();");
		dstFileOut.println("\t\trequestMap = new HashMap<Integer, " + className + ">();\n");
		dstFileOut.println("\t\t" + className + " req;");
		dstFileOut.println();
		
		int    i;
		String line;

		String baseurl = "/api";
		StringBuffer ids = new StringBuffer();

		while( (line = reqFileInp.readLine()) != null )
		{
			line = line.trim();
			if( line.length() == 0 )
			{
				continue;
			}
			if( line.charAt(0) == '#' )
			{
				continue;
			}
			String[] tokens = line.split("( |\t)+");
			if( tokens == null )
			{
				continue;
			}
			if( tokens.length == 0 )
			{
				continue;
			}
			else if( "BASEURL".equals(tokens[0]) == true )
			{
				if( tokens.length != 2 )
				{
					getLog().warn("Skip invalid BASEURL statement - " + line);
					continue;
				}
				baseurl = tokens[1];
			}
			else if( tokens.length >= 5 )
			{
				int type = -1;
				try
				{
					type = Integer.parseInt(tokens[0]);
				}
				catch( Exception e )
				{
					type = -1;
				}
				if( type <= 0 )
				{
					getLog().warn("Skip invalid request statement - " + line);
					continue;
				}

				String  id = "";
				boolean addUnderScore = false;
				for( i = 0; i < tokens[1].length(); i++ )
				{
					char c = tokens[1].charAt(i);
					if( c == ':' )
					{
						c = '_';
						addUnderScore = false;
					}
					else
					{
						if( (c >= 'A') && (c <= 'Z') )
						{
							if( addUnderScore == true )
							{
								id = id + '_';
							}
						}
						addUnderScore = true;
					}
					id = id + c;
				}
				if( id.length() == 0 )
				{
					getLog().warn("Skip invalid request statement - " + line);
					continue;
				}
				ids.append(String.format("\tpublic static final int %-64s = %s;\n", id.toUpperCase(), tokens[0]));

				dstFileOut.println("\t\treq = new " + className + "();");
				dstFileOut.println("\t\treq.setId(" + type + ");");
				dstFileOut.println("\t\treq.setName(\"" + tokens[1] + "\");");
				dstFileOut.println("\t\treq.setUrl(\"" + baseurl + tokens[2] + "\");");
				dstFileOut.println("\t\treq.setMethod(\"" + tokens[3] + "\");");
				for( i = 4; i < tokens.length; i++ )
				{
					dstFileOut.println("\t\treq.getRoles().add(\"" + tokens[i] + "\");");
				}
				for( i = 4; i < tokens.length; i++ )
				{
					dstFileOut.println("\t\tcontrolMap.put(\"" + tokens[i] + ":" + type + "\", req);");
				}
				dstFileOut.println("\t\trequestMap.put(" + tokens[0] + ", req);");
				dstFileOut.println();
                        }
			else
			{
				getLog().warn("Skip invalid statement - " + line);
			}
		}

		dstFileOut.println("\t}");
		dstFileOut.println();
		dstFileOut.print(ids.toString());
	}
}
