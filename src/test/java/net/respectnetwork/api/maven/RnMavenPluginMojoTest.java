
package net.respectnetwork.api.maven;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.IOException;

import net.respectnetwork.api.maven.*;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.commons.io.FileUtils;

/**
 * Unit test for Respect Network maven plugin
 */
public class RnMavenPluginMojoTest extends AbstractMojoTestCase
{
	protected void setUp() throws Exception
	{
		// required
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		// required
		super.tearDown();
	}

	public static Test suite()
	{
		return new TestSuite( RnMavenPluginMojoTest.class );
	}

	public void testRnMaven() throws org.apache.maven.plugin.MojoExecutionException
	{
		RnMavenPluginMojo mojo = new RnMavenPluginMojo();

		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_ACTION_UNSPECIFIED, e.getMessage());
		}

		String[] args0 = { };
		mojo.setArgs(args0);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_ACTION_UNSPECIFIED, e.getMessage());
		}

		String[] args1 = { "cmd", "arg1", "arg2" };
		mojo.setArgs(args1);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertTrue(e.getMessage().startsWith(RnMavenPluginMojo.ERROR_ACTION_UNKNOWN));
		}

		assertTrue(true);
	}

	public void testApiError() throws org.apache.maven.plugin.MojoExecutionException
	{
		RnMavenPluginMojo mojo = new RnMavenPluginMojo();

		String[] args2 = { RnMavenPluginMojo.ACTION_API_ERROR };
		mojo.setArgs(args2);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_API_ERROR_USAGE, e.getMessage());
		}

		String[] args3 = { RnMavenPluginMojo.ACTION_API_ERROR, "bad-error-template", "bad-error-txt", "bad/target" };
		mojo.setArgs(args3);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_API_ERROR_FAILURE, e.getMessage());
		}

		String[] args4 = { RnMavenPluginMojo.ACTION_API_ERROR, "good-error-template", "bad-error-txt", "bad/target" };
		mojo.setArgs(args4);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_API_ERROR_FAILURE, e.getMessage());
		}

		String[] args5 = { RnMavenPluginMojo.ACTION_API_ERROR, "good-error-template", "good-error-txt", "bad/target" };
		mojo.setArgs(args5);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_API_ERROR_FAILURE, e.getMessage());
		}

		String[] args6 = { RnMavenPluginMojo.ACTION_API_ERROR, "good-error-template", "good-error-txt", "good-error-output" };
		mojo.setArgs(args6);
		mojo.execute();
		try
		{
			assertTrue(FileUtils.contentEqualsIgnoreEOL(new File("good-error-output"), new File("good-error-target"), null));
		}
		catch( IOException ioe )
		{
			throw new MojoExecutionException("assertTrue(FileUtils.contentEqualsIgnoreEOL", ioe);
		}

		File pom = getTestFile("tmp/good-error-pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());
		try
		{
			mojo = (RnMavenPluginMojo) lookupMojo("rn-maven-plugin", pom);
		}
		catch( Exception ex )
		{
			throw new MojoExecutionException("lookupMojo failed", ex);
		}
		assertNotNull(mojo);
		String[] args7 = mojo.getArgs();
		assertNotNull(args7);
		assertEquals(args7.length, 4);
		assertEquals(args7[0], "ApiError");
		assertEquals(args7[1], "good-error-template");
		assertEquals(args7[2], "good-error-txt");
		assertEquals(args7[3], "good-error-generated");
		mojo.execute();
		try
		{
			assertTrue(FileUtils.contentEqualsIgnoreEOL(new File("good-error-generated"), new File("good-error-target"), null));
		}
		catch( IOException ioe )
		{
			throw new MojoExecutionException("assertTrue(FileUtils.contentEqualsIgnoreEOL", ioe);
		}

		assertTrue(true);
	}

	public void testApiRequest() throws org.apache.maven.plugin.MojoExecutionException
	{
		RnMavenPluginMojo mojo = new RnMavenPluginMojo();

		String[] args2 = { RnMavenPluginMojo.ACTION_API_REQUEST };
		mojo.setArgs(args2);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_API_REQUEST_USAGE, e.getMessage());
		}

		String[] args3 = { RnMavenPluginMojo.ACTION_API_REQUEST, "bad-request-template", "bad-request-txt", "bad/target" };
		mojo.setArgs(args3);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_API_REQUEST_FAILURE, e.getMessage());
		}

		String[] args4 = { RnMavenPluginMojo.ACTION_API_REQUEST, "good-request-template", "bad-request-txt", "bad/target" };
		mojo.setArgs(args4);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_API_REQUEST_FAILURE, e.getMessage());
		}

		String[] args5 = { RnMavenPluginMojo.ACTION_API_REQUEST, "good-request-template", "good-request-txt", "bad/target" };
		mojo.setArgs(args5);
		try
		{
			mojo.execute();
		}
		catch( MojoExecutionException e )
		{
			assertEquals(RnMavenPluginMojo.ERROR_API_REQUEST_FAILURE, e.getMessage());
		}

		String[] args6 = { RnMavenPluginMojo.ACTION_API_REQUEST, "good-request-template", "good-request-txt", "good-request-output" };
		mojo.setArgs(args6);
		mojo.execute();
		try
		{
			assertTrue(FileUtils.contentEqualsIgnoreEOL(new File("good-request-output"), new File("good-request-target"), null));
		}
		catch( IOException ioe )
		{
			throw new MojoExecutionException("assertTrue(FileUtils.contentEqualsIgnoreEOL", ioe);
		}

		File pom = getTestFile("tmp/good-request-pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());
		try
		{
			mojo = (RnMavenPluginMojo) lookupMojo("rn-maven-plugin", pom);
		}
		catch( Exception ex )
		{
			throw new MojoExecutionException("lookupMojo failed", ex);
		}
		assertNotNull(mojo);
		String[] args7 = mojo.getArgs();
		assertNotNull(args7);
		assertEquals(args7.length, 4);
		assertEquals(args7[0], "ApiRequest");
		assertEquals(args7[1], "good-request-template");
		assertEquals(args7[2], "good-request-txt");
		assertEquals(args7[3], "good-request-generated");
		mojo.execute();
		try
		{
			assertTrue(FileUtils.contentEqualsIgnoreEOL(new File("good-request-generated"), new File("good-request-target"), null));
		}
		catch( IOException ioe )
		{
			throw new MojoExecutionException("assertTrue(FileUtils.contentEqualsIgnoreEOL", ioe);
		}

		assertTrue(true);
	}
}
